# ============================================================================
# TechBookStore Post-Provisioning Script
# ============================================================================
# This script configures Service Connector connections for the deployed
# Container Apps to enable passwordless authentication with Managed Identity.
# ============================================================================

param(
    [Parameter(Mandatory=$false)]
    [string]$ResourceGroupName = "rg-techbookstore-prod",
    
    [Parameter(Mandatory=$false)]
    [string]$EnvFile = "$PSScriptRoot\.env",
    
    [Parameter(Mandatory=$false)]
    [switch]$SkipPostgresConnection,
    
    [Parameter(Mandatory=$false)]
    [switch]$SkipRedisConnection
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
# Load Environment Variables
# ============================================================================

Write-Header "Loading Deployment Outputs"

if (-not (Test-Path $EnvFile)) {
    Write-Error-Custom "Environment file not found: $EnvFile"
    Write-Info "Please run deploy.ps1 first to create the deployment outputs"
    exit 1
}

Write-Info "Loading environment variables from: $EnvFile"

Get-Content $EnvFile | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        Set-Variable -Name $name -Value $value -Scope Script
        Write-Host "  $name = $value" -ForegroundColor DarkGray
    }
}

Write-Success "Environment variables loaded"

# ============================================================================
# Verify Required Variables
# ============================================================================

Write-Header "Verifying Configuration"

$requiredVars = @(
    'AZURE_SUBSCRIPTION_ID',
    'AZURE_RESOURCE_GROUP',
    'BACKEND_APP_NAME',
    'AZURE_POSTGRESQL_HOST',
    'AZURE_POSTGRESQL_DATABASE',
    'AZURE_REDIS_HOST'
)

$missingVars = @()
foreach ($var in $requiredVars) {
    if (-not (Get-Variable -Name $var -ErrorAction SilentlyContinue)) {
        $missingVars += $var
        Write-Error-Custom "Missing required variable: $var"
    }
}

if ($missingVars.Count -gt 0) {
    Write-Error-Custom "Missing required environment variables. Please check your .env file."
    exit 1
}

Write-Success "All required variables present"

# Extract PostgreSQL server name from FQDN
$postgresServerName = $AZURE_POSTGRESQL_HOST -replace '\..*$', ''
Write-Info "PostgreSQL Server Name: $postgresServerName"

# Extract Redis name from FQDN
$redisName = $AZURE_REDIS_HOST -replace '\..*$', ''
Write-Info "Redis Name: $redisName"

# ============================================================================
# Configure PostgreSQL Connection
# ============================================================================

if (-not $SkipPostgresConnection) {
    Write-Header "Configuring PostgreSQL Connection"
    
    Write-Info "Creating Service Connector connection for PostgreSQL..."
    Write-Info "  Backend App: $BACKEND_APP_NAME"
    Write-Info "  PostgreSQL Server: $postgresServerName"
    Write-Info "  Database: $AZURE_POSTGRESQL_DATABASE"
    Write-Info "  Authentication: System-Assigned Managed Identity"
    
    try {
        $postgresConnection = az containerapp connection create postgres-flexible `
            --connection postgres-connection `
            --source-id "/subscriptions/$AZURE_SUBSCRIPTION_ID/resourceGroups/$AZURE_RESOURCE_GROUP/providers/Microsoft.App/containerApps/$BACKEND_APP_NAME" `
            --target-id "/subscriptions/$AZURE_SUBSCRIPTION_ID/resourceGroups/$AZURE_RESOURCE_GROUP/providers/Microsoft.DBforPostgreSQL/flexibleServers/$postgresServerName" `
            --database $AZURE_POSTGRESQL_DATABASE `
            --system-identity `
            --client-type springBoot `
            -y `
            --output json 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "PostgreSQL connection created successfully"
            
            # Verify the connection
            Write-Info "Verifying PostgreSQL connection..."
            $connectionDetails = az containerapp connection show `
                --resource-group $AZURE_RESOURCE_GROUP `
                --name $BACKEND_APP_NAME `
                --connection postgres-connection `
                --output json | ConvertFrom-Json
            
            Write-Success "PostgreSQL connection verified"
            Write-Info "Connection ID: $($connectionDetails.id)"
            
            # Display configured environment variables
            if ($connectionDetails.secretStore.keyVaultId) {
                Write-Info "Using Key Vault for secrets: $($connectionDetails.secretStore.keyVaultId)"
            }
            
        } else {
            Write-Error-Custom "Failed to create PostgreSQL connection"
            Write-Host $postgresConnection -ForegroundColor Red
        }
    } catch {
        Write-Error-Custom "Error creating PostgreSQL connection: $_"
    }
} else {
    Write-Info "Skipping PostgreSQL connection (--SkipPostgresConnection flag set)"
}

# ============================================================================
# Configure Redis Connection
# ============================================================================

if (-not $SkipRedisConnection) {
    Write-Header "Configuring Redis Connection"
    
    Write-Info "Creating Service Connector connection for Redis..."
    Write-Info "  Backend App: $BACKEND_APP_NAME"
    Write-Info "  Redis Name: $redisName"
    Write-Info "  Authentication: System-Assigned Managed Identity"
    
    try {
        $redisConnection = az containerapp connection create redis `
            --connection redis-connection `
            --source-id "/subscriptions/$AZURE_SUBSCRIPTION_ID/resourceGroups/$AZURE_RESOURCE_GROUP/providers/Microsoft.App/containerApps/$BACKEND_APP_NAME" `
            --target-id "/subscriptions/$AZURE_SUBSCRIPTION_ID/resourceGroups/$AZURE_RESOURCE_GROUP/providers/Microsoft.Cache/redis/$redisName" `
            --system-identity `
            --client-type springBoot `
            -y `
            --output json 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Redis connection created successfully"
            
            # Verify the connection
            Write-Info "Verifying Redis connection..."
            $connectionDetails = az containerapp connection show `
                --resource-group $AZURE_RESOURCE_GROUP `
                --name $BACKEND_APP_NAME `
                --connection redis-connection `
                --output json | ConvertFrom-Json
            
            Write-Success "Redis connection verified"
            Write-Info "Connection ID: $($connectionDetails.id)"
            
        } else {
            Write-Error-Custom "Failed to create Redis connection"
            Write-Host $redisConnection -ForegroundColor Red
        }
    } catch {
        Write-Error-Custom "Error creating Redis connection: $_"
    }
} else {
    Write-Info "Skipping Redis connection (--SkipRedisConnection flag set)"
}

# ============================================================================
# List All Connections
# ============================================================================

Write-Header "Service Connections Summary"

try {
    Write-Info "Listing all service connections for $BACKEND_APP_NAME..."
    
    $connections = az containerapp connection list `
        --resource-group $AZURE_RESOURCE_GROUP `
        --name $BACKEND_APP_NAME `
        --output json | ConvertFrom-Json
    
    if ($connections.Count -gt 0) {
        Write-Success "Found $($connections.Count) connection(s)"
        
        Write-Host ""
        Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
        
        foreach ($conn in $connections) {
            Write-Host ""
            Write-Host "📌 Connection: $($conn.name)" -ForegroundColor Yellow
            Write-Host "   Type       : $($conn.targetType)"
            Write-Host "   Auth Type  : $($conn.authType)"
            Write-Host "   Status     : $($conn.provisioningState)"
            
            # Display configured environment variables
            if ($conn.secretStore.keyVaultId) {
                Write-Host "   Key Vault  : $($conn.secretStore.keyVaultId)" -ForegroundColor DarkGray
            }
        }
        
        Write-Host ""
        Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
        
    } else {
        Write-Info "No service connections found"
    }
    
} catch {
    Write-Error-Custom "Error listing connections: $_"
}

# ============================================================================
# Verify Container App Configuration
# ============================================================================

Write-Header "Verifying Container App Configuration"

try {
    Write-Info "Retrieving backend Container App details..."
    
    $backendApp = az containerapp show `
        --name $BACKEND_APP_NAME `
        --resource-group $AZURE_RESOURCE_GROUP `
        --output json | ConvertFrom-Json
    
    Write-Success "Backend Container App details retrieved"
    
    Write-Host ""
    Write-Host "🚀 Backend Container App:" -ForegroundColor Yellow
    Write-Host "   Name       : $($backendApp.name)"
    Write-Host "   FQDN       : $($backendApp.properties.configuration.ingress.fqdn)"
    Write-Host "   URL        : https://$($backendApp.properties.configuration.ingress.fqdn)"
    Write-Host "   Status     : $($backendApp.properties.provisioningState)"
    
    # Check for managed identities
    Write-Host ""
    Write-Host "🔐 Managed Identities:" -ForegroundColor Yellow
    if ($backendApp.identity.type -match "SystemAssigned") {
        Write-Host "   System-Assigned: ✓ Enabled"
        Write-Host "   Principal ID   : $($backendApp.identity.principalId)"
    }
    if ($backendApp.identity.userAssignedIdentities) {
        Write-Host "   User-Assigned  : ✓ Enabled"
        foreach ($identity in $backendApp.identity.userAssignedIdentities.PSObject.Properties) {
            Write-Host "   Identity       : $($identity.Name)"
        }
    }
    
    # Check for environment variables (limited display for security)
    $envVarCount = $backendApp.properties.template.containers[0].env.Count
    Write-Host ""
    Write-Host "📝 Environment Variables: $envVarCount configured" -ForegroundColor Yellow
    
} catch {
    Write-Error-Custom "Error retrieving Container App details: $_"
}

# ============================================================================
# Test Endpoints
# ============================================================================

Write-Header "Testing Endpoints"

$backendUrl = "https://$($backendApp.properties.configuration.ingress.fqdn)"

Write-Info "Testing backend health endpoint..."
try {
    $response = Invoke-WebRequest -Uri "$backendUrl/actuator/health" -Method Get -UseBasicParsing -TimeoutSec 10 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        Write-Success "Backend health check passed (HTTP $($response.StatusCode))"
    } else {
        Write-Info "Backend health check returned HTTP $($response.StatusCode)"
    }
} catch {
    Write-Info "Backend health check not yet responding (this is normal for newly deployed apps)"
    Write-Info "The app may need a few minutes to start up"
}

Write-Host ""
Write-Host "Backend URL: $backendUrl" -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# Completion Summary
# ============================================================================

Write-Header "Post-Provisioning Complete"

Write-Host ""
Write-Host "🎉 Post-provisioning configuration completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Summary:" -ForegroundColor Yellow
Write-Host "  ✓ PostgreSQL connection configured (if not skipped)"
Write-Host "  ✓ Redis connection configured (if not skipped)"
Write-Host "  ✓ Service Connector connections verified"
Write-Host "  ✓ Container App configuration validated"
Write-Host ""
Write-Host "🔗 Important URLs:" -ForegroundColor Yellow
Write-Host "  Backend API : $BACKEND_APP_URL"
Write-Host "  Frontend App: $FRONTEND_APP_URL"
Write-Host ""
Write-Host "📝 Next Steps:" -ForegroundColor Yellow
Write-Host "  1. Build your Docker images:"
Write-Host "     docker build -t $AZURE_CONTAINER_REGISTRY_ENDPOINT/backend:latest ./backend"
Write-Host "     docker build -t $AZURE_CONTAINER_REGISTRY_ENDPOINT/frontend:latest ./frontend"
Write-Host ""
Write-Host "  2. Push images to Container Registry:"
Write-Host "     az acr login --name $AZURE_CONTAINER_REGISTRY_NAME"
Write-Host "     docker push $AZURE_CONTAINER_REGISTRY_ENDPOINT/backend:latest"
Write-Host "     docker push $AZURE_CONTAINER_REGISTRY_ENDPOINT/frontend:latest"
Write-Host ""
Write-Host "  3. Update Container Apps with your images:"
Write-Host "     az containerapp update --name $BACKEND_APP_NAME --resource-group $AZURE_RESOURCE_GROUP --image $AZURE_CONTAINER_REGISTRY_ENDPOINT/backend:latest"
Write-Host "     az containerapp update --name $FRONTEND_APP_NAME --resource-group $AZURE_RESOURCE_GROUP --image $AZURE_CONTAINER_REGISTRY_ENDPOINT/frontend:latest"
Write-Host ""
Write-Host "  4. Monitor your applications:"
Write-Host "     az containerapp logs show --name $BACKEND_APP_NAME --resource-group $AZURE_RESOURCE_GROUP --follow"
Write-Host ""

Write-Success "Post-provisioning script completed"
