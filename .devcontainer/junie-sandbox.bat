@echo off
rem Junie CLI sandbox without devcontainer tooling -- Windows twin of junie-sandbox.sh.
rem Plain-docker replica of .devcontainer/devcontainer.json (image, Node 22 feature,
rem named volumes, JAVA_HOME, auth-callback port 62345, postCreateCommand).
rem No admin rights needed -- only Docker access (docker-users group membership).
rem
rem Usage: .devcontainer\junie-sandbox.bat [--rebuild]
rem   --rebuild  remove the existing container and rebuild the image first
rem
rem First run inside the container: `junie` -> JetBrains Account login (callback must
rem print http://localhost:62345 -- the port is published 1:1 for that reason).
setlocal

for %%I in ("%~dp0..") do set "REPO_ROOT=%%~fI"
set "IMAGE=citizen-portal-junie-sandbox"
set "CONTAINER=citizen-portal-junie-sandbox"
set "WORKDIR=/workspaces/citizen-portal"
set "TMPDF=%TEMP%\junie-sandbox.Dockerfile"

if /i "%~1"=="--rebuild" (
    docker rm -f %CONTAINER% >nul 2>&1
    docker rmi %IMAGE% >nul 2>&1
)

rem Reattach if the container already exists (volumes keep Junie auth + caches anyway,
rem but reusing the container also keeps the warmed workspace state).
docker container inspect %CONTAINER% >nul 2>&1
if not errorlevel 1 (
    echo ^>^> Reattaching to existing container %CONTAINER%
    docker start %CONTAINER% >nul || exit /b 1
    goto :attach
)

docker image inspect %IMAGE% >nul 2>&1
if errorlevel 1 (
    echo ^>^> Building %IMAGE% ^(devcontainer Dockerfile + Node 22^)
    docker build -t %IMAGE%:base "%REPO_ROOT%\.devcontainer" || exit /b 1
    rem cmd has no heredoc: write the Node-22 layer to a temp Dockerfile. Node 22 goes
    rem in via the base image's nvm (base ships Node 18 as default, which would shadow
    rem a system install).
    > "%TMPDF%" echo FROM %IMAGE%:base
    >> "%TMPDF%" echo USER vscode
    >> "%TMPDF%" echo RUN bash -lc '. /usr/local/share/nvm/nvm.sh ^&^& nvm install 22 ^&^& nvm alias default 22'
    docker build -t %IMAGE% -f "%TMPDF%" "%REPO_ROOT%\.devcontainer" || exit /b 1
    del "%TMPDF%" >nul 2>&1
)

echo ^>^> Starting %CONTAINER% ^(workspace bind-mounted at %WORKDIR%^)
rem Mirrors devcontainer.json: named volumes for ~/.junie (auth), ~/.m2, ~/.npm;
rem JAVA_HOME pinned; 62345 published 1:1 for the Junie OAuth loopback redirect.
docker run -dit --name %CONTAINER% ^
  --user vscode ^
  -v "%REPO_ROOT%:%WORKDIR%" ^
  -v citizen-portal-junie:/home/vscode/.junie ^
  -v citizen-portal-m2:/home/vscode/.m2 ^
  -v citizen-portal-npm:/home/vscode/.npm ^
  -p 62345:62345 ^
  -e JAVA_HOME=/usr/lib/jvm/msopenjdk-current ^
  -w %WORKDIR% ^
  %IMAGE% bash >nul || exit /b 1

rem postCreateCommand equivalent: named volumes mount root-owned -> chown once, then
rem warm dependency caches (failure-tolerant, same as devcontainer.json).
rem No -it here: setup is non-interactive, and exec -it fails ("stdin is not a
rem terminal") when the script runs without a real console (IDE terminal, npm, mintty).
echo ^>^> Running one-time setup ^(chown volumes, npm install, maven go-offline^)
docker exec -w %WORKDIR% %CONTAINER% bash -c "sudo chown -R vscode:vscode /home/vscode/.junie /home/vscode/.m2 /home/vscode/.npm; npm install || true; ./mvnw -q -DskipTests dependency:go-offline || true"

:attach
docker exec -it -w %WORKDIR% %CONTAINER% bash
set "RC=%errorlevel%"
if not "%RC%"=="0" (
    echo ^>^> Could not attach an interactive shell ^(this terminal has no TTY^).
    echo ^>^> The container is running. Attach from cmd/PowerShell/Windows Terminal:
    echo      docker exec -it -w %WORKDIR% %CONTAINER% bash
)
exit /b %RC%
