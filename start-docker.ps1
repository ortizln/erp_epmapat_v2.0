$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $root "load-env.ps1")
Import-EnvFiles -RootPath $root -FileNames @(".env", ".env.docker")

docker compose up -d --build
