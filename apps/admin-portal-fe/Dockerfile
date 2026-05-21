FROM node:20-alpine AS admin-portal-fe-base

# This is copy/pasted from https://github.com/vercel/turbo/blob/main/examples/with-docker/apps/web/Dockerfile

FROM admin-portal-fe-base AS admin-portal-fe-builder
# Check https://github.com/nodejs/docker-node/tree/b4117f9333da4138b03a546ec926ef50a31506c3#nodealpine to understand why libc6-compat might be needed.
RUN apk add --no-cache libc6-compat
RUN apk update
# Set working directory
WORKDIR /app
COPY . .
RUN npx turbo prune admin-portal-fe --docker

# Add lockfile and package.json's of isolated subworkspace
FROM admin-portal-fe-base AS admin-portal-fe-installer
RUN apk add --no-cache libc6-compat
RUN apk update
WORKDIR /app

# First install the dependencies (as they change less often)
COPY .gitignore .gitignore
COPY --from=admin-portal-fe-builder /app/out/json/ .
COPY --from=admin-portal-fe-builder /app/out/package-lock.json ./package-lock.json
RUN npm install

# Build the project
COPY --from=admin-portal-fe-builder /app/out/full/ .
COPY turbo.json turbo.json

ENV NEXT_PUBLIC_API_URL="https://bp-dev-dnstest.germanywestcentral.cloudapp.azure.com/portal-admin/api/v1"
ENV NEXT_PUBLIC_KEYCLOAK_URL="https://keycloak.eviaconsulting.com"
ENV NEXT_PUBLIC_BASE_PATH="/portal-admin"

RUN npx turbo build --filter=admin-portal-fe...

FROM admin-portal-fe-base AS admin-portal-fe-runner
WORKDIR /app

# Don't run production as root
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs
USER nextjs

COPY --from=admin-portal-fe-installer /app/apps/admin-portal-fe/next.config.js .
COPY --from=admin-portal-fe-installer /app/apps/admin-portal-fe/package.json .

# Automatically leverage output traces to reduce image size
# https://nextjs.org/docs/advanced-features/output-file-tracing
COPY --from=admin-portal-fe-installer --chown=nextjs:nodejs /app/apps/admin-portal-fe/.next/standalone ./
COPY --from=admin-portal-fe-installer --chown=nextjs:nodejs /app/apps/admin-portal-fe/.next/static ./apps/admin-portal-fe/.next/static
COPY --from=admin-portal-fe-installer --chown=nextjs:nodejs /app/apps/admin-portal-fe/public ./apps/admin-portal-fe/public

CMD node apps/admin-portal-fe/server.js
