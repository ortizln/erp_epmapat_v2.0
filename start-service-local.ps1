param(
    [Parameter(Mandatory = $true)]
    [string]$Service
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $root "load-env.ps1")

Import-EnvFiles -RootPath $root -FileNames @(".env", ".env.local")

$serviceMap = @{
    "config" = "config"
    "eureka" = "eureka"
    "gateway" = "gateway"
    "bandred" = "bandred"
    "pagosonline" = "pagosonline"
    "login" = "login"
    "recaudacion" = "recaudacion"
    "comercializacion" = "comercializacion"
    "sri" = "sri-files"
    "rrhh" = "rrhh"
    "contabilidad" = "contabilidad"
    "reportesjr" = "reportes-jr"
    "epmapaapi" = "epmapaapi"
    "emails" = "emails"
    "gestiondocumental" = "gestiondocumental"
}

$normalizedService = $Service.Trim().ToLowerInvariant()

if (-not $serviceMap.ContainsKey($normalizedService)) {
    Write-Host "Servicio no reconocido: $Service"
    Write-Host "Opciones: $($serviceMap.Keys | Sort-Object | Join-String -Separator ', ')"
    exit 1
}

$serviceDir = Join-Path $root $serviceMap[$normalizedService]

if (-not (Test-Path $serviceDir)) {
    Write-Host "No existe el directorio del servicio: $serviceDir"
    exit 1
}

Write-Host "Iniciando $normalizedService con perfil local..."
Set-Location $serviceDir
mvn spring-boot:run
