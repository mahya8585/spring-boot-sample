# ============================================================================
# TechBookStore Azure Infrastructure Deployment Script
# ============================================================================
# This script deploys all Azure resources for the TechBookStore application
# using Bicep templates and Azure CLI.
# ============================================================================

param(
    [Parameter(Mandatory=$false)]
    [string]$EnvironmentName = "techbookstore-prod",
    
    [Parameter(Mandatory=$false)]
    [string]$Location = "eastus2",
    
    [Parameter(Mandatory=$false)]
    [string]$ResourceGroupName = "rg-techbookstore-prod",
    
    [Parameter(Mandatory=$true)]
    [string]$PostgresqlAdminUsername,
    
    [Parameter(Mandatory=$true)]
    [SecureString]$PostgresqlAdminPassword,
    
    [Parameter(Mandatory=$false)]
    [string]$PostgresqlDatabaseName = "techbookstore",
    
    [Parameter(Mandatory=$false)]
    [switch]$SkipConfirmation
)

# Error handling
$ErrorActionPreference = "Stop"

# ============================================================================
# Helper Functions
# ============================================================================

function Write-Header {
    param([string]$Message)
    Write-Host "`n============================================================================" -ForegroundColor Cyan
    Write-Host " $Message" -ForegroundColor Cyan
    Write-Host "============================================================================`n" -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Write-Info {
    param([string]$Message)
    Write-Host "ℹ $Message" -ForegroundColor Blue
}

function Write-Error-Custom {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

# ============================================================================
# Pre-flight Checks
# ============================================================================

Write-Header "Pre-flight Checks"

# Check if Azure CLI is installed
Write-Info "Checking Azure CLI installation..."
try {
    $azVersion = az --version 2>&1 | Select-Object -First 1
    Write-Success "Azure CLI is installed: $azVersion"
} catch {
    Write-Error-Custom "Azure CLI is not installed. Please install from https://aka.ms/installazurecliwindows"
    exit 1
}

# Check if logged in to Azure
Write-Info "Checking Azure login status..."
try {
    $account = az account show 2>&1 | ConvertFrom-Json
    Write-Success "Logged in to Azure as: $($account.user.name)"
    Write-Success "Subscription: $($account.name) ($($account.id))"
} catch {
    Write-Error-Custom "Not logged in to Azure. Running 'az login'..."
    az login
    if ($LASTEXITCODE -ne 0) {
        Write-Error-Custom "Azure login failed"
        exit 1
    }
}

# Check if Service Connector extension is installed
Write-Info "Checking Service Connector extension..."
$extensions = az extension list --query "[?name=='serviceconnector-passwordless'].name" -o tsv
if (-not $extensions) {
    Write-Info "Installing Service Connector extension..."
    az extension add --name serviceconnector-passwordless --upgrade
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Service Connector extension installed"
    } else {
        Write-Error-Custom "Failed to install Service Connector extension"
        exit 1
    }
} else {
    Write-Success "Service Connector extension is already installed"
    az extension update --name serviceconnector-passwordless
}

# Validate Bicep files
Write-Info "Validating Bicep template..."
az bicep build --file "$PSScriptRoot\main.bicep" 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Success "Bicep template validation passed"
} else {
    Write-Error-Custom "Bicep template validation failed"
    exit 1
}

# ============================================================================
# Deployment Confirmation
# ============================================================================

if (-not $SkipConfirmation) {
    Write-Header "Deployment Configuration"
    Write-Host "Environment Name       : $EnvironmentName"
    Write-Host "Location               : $Location"
    Write-Host "Resource Group         : $ResourceGroupName"
    Write-Host "PostgreSQL Admin User  : $PostgresqlAdminUsername"
    Write-Host "PostgreSQL Database    : $PostgresqlDatabaseName"
    Write-Host ""
    
    $confirmation = Read-Host "Do you want to proceed with the deployment? (yes/no)"
    if ($confirmation -ne "yes") {
        Write-Info "Deployment cancelled by user"
        exit 0
    }
}

# ============================================================================
# Create Resource Group
# ============================================================================

Write-Header "Creating Resource Group"

$existingRg = az group exists --name $ResourceGroupName
if ($existingRg -eq "true") {
    Write-Info "Resource group '$ResourceGroupName' already exists"
} else {
    Write-Info "Creating resource group '$ResourceGroupName' in '$Location'..."
    az group create --name $ResourceGroupName --location $Location --output none
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Resource group created successfully"
    } else {
        Write-Error-Custom "Failed to create resource group"
        exit 1
    }
}

# ============================================================================
# Deploy Infrastructure
# ============================================================================

Write-Header "Deploying Azure Infrastructure"
Write-Info "This may take 15-20 minutes..."

# Convert SecureString to plain text for deployment
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($PostgresqlAdminPassword)
$plainPassword = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)

try {
    $deploymentResult = az deployment group create `
        --resource-group $ResourceGroupName `
        --template-file "$PSScriptRoot\main.bicep" `
        --parameters `
            environmentName=$EnvironmentName `
            location=$Location `
            postgresqlAdminUsername=$PostgresqlAdminUsername `
            postgresqlAdminPassword=$plainPassword `
            postgresqlDatabaseName=$PostgresqlDatabaseName `
        --output json | ConvertFrom-Json
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Infrastructure deployment completed successfully"
    } else {
        Write-Error-Custom "Infrastructure deployment failed"
        exit 1
    }
} catch {
    Write-Error-Custom "Deployment failed with error: $_"
    exit 1
} finally {
    # Clear the plain password from memory
    $plainPassword = $null
}

# ============================================================================
# Extract Deployment Outputs
# ============================================================================

Write-Header "Deployment Outputs"

$outputs = $deploymentResult.properties.outputs

Write-Host ""
Write-Host "📦 Deployed Resources:" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green

Write-Host ""
Write-Host "🌍 General Information:" -ForegroundColor Yellow
Write-Host "  Subscription ID      : $($outputs.AZURE_SUBSCRIPTION_ID.value)"
Write-Host "  Resource Group       : $($outputs.AZURE_RESOURCE_GROUP.value)"
Write-Host "  Location             : $($outputs.AZURE_LOCATION.value)"
Write-Host "  Tenant ID            : $($outputs.AZURE_TENANT_ID.value)"

Write-Host ""
Write-Host "🐳 Container Resources:" -ForegroundColor Yellow
Write-Host "  Container Registry   : $($outputs.AZURE_CONTAINER_REGISTRY_NAME.value)"
Write-Host "  Registry Endpoint    : $($outputs.AZURE_CONTAINER_REGISTRY_ENDPOINT.value)"

Write-Host ""
Write-Host "🚀 Container Apps:" -ForegroundColor Yellow
Write-Host "  Backend App Name     : $($outputs.BACKEND_APP_NAME.value)"
Write-Host "  Backend URL          : $($outputs.BACKEND_APP_URL.value)"
Write-Host "  Frontend App Name    : $($outputs.FRONTEND_APP_NAME.value)"
Write-Host "  Frontend URL         : $($outputs.FRONTEND_APP_URL.value)"

Write-Host ""
Write-Host "🗄️ Database Resources:" -ForegroundColor Yellow
Write-Host "  PostgreSQL Host      : $($outputs.AZURE_POSTGRESQL_HOST.value)"
Write-Host "  PostgreSQL Database  : $($outputs.AZURE_POSTGRESQL_DATABASE.value)"
Write-Host "  Redis Host           : $($outputs.AZURE_REDIS_HOST.value)"
Write-Host "  Redis Port           : $($outputs.AZURE_REDIS_PORT.value)"

Write-Host ""
Write-Host "🔒 Security Resources:" -ForegroundColor Yellow
Write-Host "  Key Vault Name       : $($outputs.AZURE_KEY_VAULT_NAME.value)"
Write-Host "  Key Vault URI        : $($outputs.AZURE_KEY_VAULT_ENDPOINT.value)"

Write-Host ""
Write-Host "📊 Monitoring Resources:" -ForegroundColor Yellow
Write-Host "  App Insights Name    : $($outputs.AZURE_APPINSIGHTS_NAME.value)"
Write-Host "  Instrumentation Key  : $($outputs.AZURE_APPINSIGHTS_INSTRUMENTATION_KEY.value)"
Write-Host "  Connection String    : $($outputs.AZURE_APPINSIGHTS_CONNECTION_STRING.value)"

Write-Host ""
Write-Host "🔐 Managed Identity:" -ForegroundColor Yellow
Write-Host "  Identity Name        : $($outputs.USER_MANAGED_IDENTITY_NAME.value)"
Write-Host "  Client ID            : $($outputs.USER_MANAGED_IDENTITY_CLIENT_ID.value)"
Write-Host "  Principal ID         : $($outputs.USER_MANAGED_IDENTITY_PRINCIPAL_ID.value)"

Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green

# ============================================================================
# Save Outputs to File
# ============================================================================

Write-Header "Saving Deployment Outputs"

$outputFile = "$PSScriptRoot\deployment-outputs.json"
$outputs | ConvertTo-Json -Depth 10 | Out-File -FilePath $outputFile -Encoding UTF8
Write-Success "Deployment outputs saved to: $outputFile"

# Also save as environment variables file for easy sourcing
$envFile = "$PSScriptRoot\.env"
$envContent = @"
# TechBookStore Deployment Environment Variables
# Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

AZURE_SUBSCRIPTION_ID=$($outputs.AZURE_SUBSCRIPTION_ID.value)
AZURE_RESOURCE_GROUP=$($outputs.AZURE_RESOURCE_GROUP.value)
AZURE_LOCATION=$($outputs.AZURE_LOCATION.value)
AZURE_TENANT_ID=$($outputs.AZURE_TENANT_ID.value)

AZURE_CONTAINER_REGISTRY_NAME=$($outputs.AZURE_CONTAINER_REGISTRY_NAME.value)
AZURE_CONTAINER_REGISTRY_ENDPOINT=$($outputs.AZURE_CONTAINER_REGISTRY_ENDPOINT.value)

BACKEND_APP_NAME=$($outputs.BACKEND_APP_NAME.value)
BACKEND_APP_URL=$($outputs.BACKEND_APP_URL.value)
FRONTEND_APP_NAME=$($outputs.FRONTEND_APP_NAME.value)
FRONTEND_APP_URL=$($outputs.FRONTEND_APP_URL.value)

AZURE_POSTGRESQL_HOST=$($outputs.AZURE_POSTGRESQL_HOST.value)
AZURE_POSTGRESQL_DATABASE=$($outputs.AZURE_POSTGRESQL_DATABASE.value)
AZURE_REDIS_HOST=$($outputs.AZURE_REDIS_HOST.value)
AZURE_REDIS_PORT=$($outputs.AZURE_REDIS_PORT.value)

AZURE_KEY_VAULT_NAME=$($outputs.AZURE_KEY_VAULT_NAME.value)
AZURE_KEY_VAULT_ENDPOINT=$($outputs.AZURE_KEY_VAULT_ENDPOINT.value)

AZURE_APPINSIGHTS_NAME=$($outputs.AZURE_APPINSIGHTS_NAME.value)
AZURE_APPINSIGHTS_INSTRUMENTATION_KEY=$($outputs.AZURE_APPINSIGHTS_INSTRUMENTATION_KEY.value)
AZURE_APPINSIGHTS_CONNECTION_STRING=$($outputs.AZURE_APPINSIGHTS_CONNECTION_STRING.value)

USER_MANAGED_IDENTITY_NAME=$($outputs.USER_MANAGED_IDENTITY_NAME.value)
USER_MANAGED_IDENTITY_CLIENT_ID=$($outputs.USER_MANAGED_IDENTITY_CLIENT_ID.value)
USER_MANAGED_IDENTITY_PRINCIPAL_ID=$($outputs.USER_MANAGED_IDENTITY_PRINCIPAL_ID.value)
"@

$envContent | Out-File -FilePath $envFile -Encoding UTF8
Write-Success "Environment variables saved to: $envFile"

# ============================================================================
# Next Steps
# ============================================================================

Write-Header "Next Steps"

Write-Host ""
Write-Host "🎉 Infrastructure deployment completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "To complete the setup, please run the post-provisioning script:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  .\infra\post-provision.ps1" -ForegroundColor Cyan
Write-Host ""
Write-Host "This script will:" -ForegroundColor Yellow
Write-Host "  1. Configure PostgreSQL connection with Managed Identity"
Write-Host "  2. Configure Redis connection with Managed Identity"
Write-Host "  3. Verify all service connections"
Write-Host ""
Write-Host "After post-provisioning, you can deploy your applications:" -ForegroundColor Yellow
Write-Host "  1. Build and push Docker images to Container Registry"
Write-Host "  2. Update Container Apps with your application images"
Write-Host ""

Write-Success "Deployment script completed"
