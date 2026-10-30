param(
    [string]$PgBin = (Split-Path (Get-Command psql -ErrorAction Stop).Source),
    [int]$Port = 55439
)
$ErrorActionPreference = 'Stop'
if ($Port -ne 55439) { throw 'Las suites solo permiten el puerto aislado 55439.' }
if (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue) {
    throw 'El puerto de pruebas 55439 ya esta ocupado; no se modificara ese servidor.'
}
$testRoot = Join-Path ([System.IO.Path]::GetTempPath()) ('epmapa-pg-' + [guid]::NewGuid().ToString('N'))
$mavenCommand = (Get-Command mvn -ErrorAction Stop).Source
$previousTestUrl = $env:EPMAPA_TEST_PG_ADMIN
$pgProcess = $null
$testExit = 1
try {
    & (Join-Path $PgBin 'initdb.exe') -D $testRoot -A trust -U postgres --no-locale -E UTF8
    if ($LASTEXITCODE -ne 0) { throw 'Fallo al inicializar PostgreSQL de pruebas.' }
    $pgProcess = Start-Process -FilePath (Join-Path $PgBin 'postgres.exe') -ArgumentList @('-D', ('"' + $testRoot + '"'), '-p', "$Port", '-h', '127.0.0.1') -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $testRoot 'stdout.log') -RedirectStandardError (Join-Path $testRoot 'stderr.log')
    $ready = $false
    for ($n = 0; $n -lt 40; $n++) {
        & (Join-Path $PgBin 'pg_isready.exe') -h 127.0.0.1 -p $Port -U postgres -q
        if ($LASTEXITCODE -eq 0) { $ready = $true; break }
        Start-Sleep -Milliseconds 250
    }
    if (!$ready) { throw 'PostgreSQL de pruebas no arranco.' }
    $env:EPMAPA_TEST_PG_ADMIN = 'jdbc:postgresql://127.0.0.1:55439/postgres'
    $ErrorActionPreference = 'Continue'
    try {
        & $mavenCommand -f (Join-Path (Split-Path $PSScriptRoot) 'pom.xml') -pl emails,sri-files -am package '-Dtest=*Test,!*ApplicationTests' '-Dsurefire.failIfNoSpecifiedTests=false'
        $testExit = $LASTEXITCODE
    } finally { $ErrorActionPreference = 'Stop' }
} finally {
    $env:EPMAPA_TEST_PG_ADMIN = $previousTestUrl
    if ($pgProcess) { & (Join-Path $PgBin 'pg_ctl.exe') -D $testRoot stop -m fast -w }
    Write-Output ("Directorio temporal de pruebas conservado: " + $testRoot)
}
exit $testExit
