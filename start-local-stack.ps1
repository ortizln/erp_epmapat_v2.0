$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
. (Join-Path $root "load-env.ps1")
Import-EnvFiles -RootPath $root -FileNames @(".env", ".env.local")

$services = @(
    @{ Name = "config"; Dir = "config"; Args = "spring-boot:run" },
    @{ Name = "eureka"; Dir = "eureka"; Args = "spring-boot:run" },
    @{ Name = "gateway"; Dir = "gateway"; Args = "spring-boot:run" },
    @{ Name = "bandred"; Dir = "bandred"; Args = "spring-boot:run" },
    @{ Name = "pagosonline"; Dir = "pagosonline"; Args = "spring-boot:run" },
    @{ Name = "login"; Dir = "login"; Args = "spring-boot:run" },
    @{ Name = "recaudacion"; Dir = "recaudacion"; Args = "spring-boot:run" },
    @{ Name = "comercializacion"; Dir = "comercializacion"; Args = "spring-boot:run" },
    @{ Name = "sri"; Dir = "sri-files"; Args = "spring-boot:run" }
)

foreach ($svc in $services) {
    $workdir = Join-Path $root $svc.Dir
    $command = "Set-Location '$workdir'; " +
        "[System.Environment]::SetEnvironmentVariable('CONFIG_SERVER_URL','$env:CONFIG_SERVER_URL','Process'); " +
        "[System.Environment]::SetEnvironmentVariable('EUREKA_SERVICEURL','$env:EUREKA_SERVICEURL','Process'); " +
        "[System.Environment]::SetEnvironmentVariable('SPRING_PROFILES_ACTIVE','$env:SPRING_PROFILES_ACTIVE','Process'); " +
        "[System.Environment]::SetEnvironmentVariable('TZ','$env:TZ','Process'); " +
        "[System.Environment]::SetEnvironmentVariable('JAVA_TOOL_OPTIONS','$env:JAVA_TOOL_OPTIONS','Process'); " +
        "mvn $($svc.Args)"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
}
