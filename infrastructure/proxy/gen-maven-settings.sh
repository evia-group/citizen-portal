#!/bin/sh
# Generate a Maven settings.xml with corporate-proxy entries for the BE builds.
#
# Proxy values come from the HTTP_PROXY/HTTPS_PROXY/NO_PROXY env vars (resolved by
# docker-compose from the host environment) or, when those are empty, from the
# optional /tmp/proxy/devcontainer.env baked into the build context — the same
# canonical file .devcontainer/setup-proxy.sh reads. On a proxy-less host both
# sources are empty and this script is a no-op. Maven's artifact resolution
# ignores proxy env vars, so a settings.xml is required. The https proxy entry is
# derived from HTTPS_PROXY when set, falling back to HTTP_PROXY otherwise.
set -e

if [ -z "$HTTP_PROXY" ] && [ -f /tmp/proxy/devcontainer.env ]; then
  HTTP_PROXY="$(grep -m1 '^HTTP_PROXY=' /tmp/proxy/devcontainer.env | cut -d= -f2-)"
  HTTPS_PROXY="$(grep -m1 '^HTTPS_PROXY=' /tmp/proxy/devcontainer.env | cut -d= -f2-)"
  NO_PROXY="$(grep -m1 '^NO_PROXY=' /tmp/proxy/devcontainer.env | cut -d= -f2-)"
fi

: "${HTTPS_PROXY:=$HTTP_PROXY}"

[ -n "$HTTP_PROXY" ] || exit 0

hostport="${HTTP_PROXY#*://}"; hostport="${hostport%%/*}"
host="${hostport%%:*}"; port="${hostport##*:}"
[ "$port" = "$host" ] && port=3128

shostport="${HTTPS_PROXY#*://}"; shostport="${shostport%%/*}"
shost="${shostport%%:*}"; sport="${shostport##*:}"
[ "$sport" = "$shost" ] && sport=3128

nph="$(printf ',%s' "${NO_PROXY:-localhost,127.0.0.1}" | sed -e 's/,\./,*./g' -e 's/,/|/g' -e 's/^|//')"

mkdir -p /root/.m2
{
  echo '<?xml version="1.0" encoding="UTF-8"?>'
  echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">'
  echo '  <proxies>'
  echo "    <proxy><id>env-http</id><active>true</active><protocol>http</protocol><host>${host}</host><port>${port}</port><nonProxyHosts>${nph}</nonProxyHosts></proxy>"
  echo "    <proxy><id>env-https</id><active>true</active><protocol>https</protocol><host>${shost}</host><port>${sport}</port><nonProxyHosts>${nph}</nonProxyHosts></proxy>"
  echo '  </proxies>'
  echo '</settings>'
} > /root/.m2/settings.xml
