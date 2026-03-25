$ErrorActionPreference = "Stop"

$repoUrl = "https://github.com/nicolycanan/gertec-recognition-app.git"
$projectRoot = Get-Location

Write-Host "Organizando projeto Android..." -ForegroundColor Cyan

# ==============================
# 1. Criar .gitignore
# ==============================
@"
# Gradle
.gradle/
build/
**/build/

# Local config
local.properties

# IDE
.idea/
*.iml
.vscode/

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# APK / AAB
*.apk
*.aab

# Captures
captures/

# Native
.cxx/
.externalNativeBuild/

# Keystore
*.jks
*.keystore

# Generated
**/generated/
"@ | Out-File ".gitignore" -Encoding utf8

Write-Host ".gitignore criado" -ForegroundColor Green

# ==============================
# 2. Criar README.md
# ==============================
@"
# Gertec Recognition App

Aplicativo Android para reconhecimento de equipamentos utilizando câmera e TensorFlow Lite.

## Funcionalidades

- Captura de imagem com CameraX
- Reconhecimento de equipamentos com TensorFlow Lite
- Exibição de detalhes do produto
- Integração com labels geradas no pipeline Python

## Estrutura

- `CameraActivity`: captura e processamento da imagem
- `ProductDetailsActivity`: exibição dos detalhes do equipamento
- `TFLiteHelper`: inferência com o modelo `.tflite`
- `ProductDatabase`: base local de informações dos equipamentos

## Tecnologias

- Android (Java)
- CameraX
- TensorFlow Lite
- ML Kit
"@ | Out-File "README.md" -Encoding utf8

Write-Host "README criado" -ForegroundColor Green

# ==============================
# 3. Git init (se necessário)
# ==============================
if (-not (Test-Path ".git")) {
    git init
    git branch -M main
    Write-Host "Repositório git inicializado" -ForegroundColor Green
} else {
    Write-Host "Repositório git já existe" -ForegroundColor Yellow
}

# ==============================
# 4. Configurar remote
# ==============================
git remote remove origin 2>$null
git remote add origin $repoUrl
Write-Host "Remote origin configurado" -ForegroundColor Green

# ==============================
# 5. Add / Commit / Push
# ==============================
git add .

$statusOutput = git status --porcelain
if (-not [string]::IsNullOrWhiteSpace($statusOutput)) {
    git commit -m "Atualização do projeto"
    Write-Host "Commit realizado" -ForegroundColor Green
} else {
    Write-Host "Nenhuma alteração para commit" -ForegroundColor Yellow
}

try {
    git push -u origin main
    Write-Host "Push realizado com sucesso" -ForegroundColor Green
} catch {
    Write-Host "Falha no push automático. Tente manualmente: git push -u origin main" -ForegroundColor Red
}

Write-Host "Tudo pronto 🚀" -ForegroundColor Cyan