param(
    [switch]$CompileOnly
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$src = Join-Path $root "src"
$out = Join-Path $root "build"

New-Item -ItemType Directory -Force -Path $out | Out-Null

$sources = Get-ChildItem -Recurse -File $src\*.java | ForEach-Object { $_.FullName }
javac -d $out $sources

if ($CompileOnly) {
    Write-Host "Compile OK: $out"
    exit 0
}

java -cp $out l12sq.server.Main
