$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
docker compose --env-file (Join-Path $root ".env.docker") up -d --build
