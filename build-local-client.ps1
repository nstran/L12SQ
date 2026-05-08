param(
    [string]$SourceJar = "",
    [string]$OutputJar = "",
    [string]$TargetHost = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$toolsDir = Join-Path $repoRoot "tools"
$patchSrc = Join-Path $toolsDir "PatchClientHost.java"
$patchOut = Join-Path $toolsDir "build-patch-client"

if ([string]::IsNullOrWhiteSpace($SourceJar)) {
    $SourceJar = Join-Path $repoRoot "loan-12-su-quan.jar"
}
if ([string]::IsNullOrWhiteSpace($OutputJar)) {
    $OutputJar = Join-Path $repoRoot "loan-12-su-quan-local.jar"
}

if (-not (Test-Path $SourceJar)) {
    Write-Error "Missing source JAR: $SourceJar (place loan-12-su-quan.jar here, or pass -SourceJar)."
    exit 1
}
if (-not (Test-Path $patchSrc)) {
    Write-Error "Missing PatchClientHost.java: $patchSrc"
    exit 1
}

New-Item -ItemType Directory -Force -Path $patchOut | Out-Null
javac -encoding UTF-8 -d $patchOut $patchSrc

Push-Location $repoRoot
try {
    $javaArgs = @($SourceJar, $OutputJar)
    if (-not [string]::IsNullOrWhiteSpace($TargetHost)) {
        $javaArgs += $TargetHost.Trim()
    }
    & java -cp $patchOut PatchClientHost @javaArgs
}
finally {
    Pop-Location
}
