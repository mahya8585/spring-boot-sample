# Azure Infrastructure Instructions - Cloud Deployment

## 🎯 Context
This file provides specific instructions for working with the **Azure infrastructure components** of the TechBookStore workshop, including Azure Developer CLI (azd) configuration, Bicep templates, and cloud deployment automation.

## ☁️ Infrastructure Overview

### Architecture Pattern
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  Application    │    │   Azure          │    │  Database &     │
│  Gateway        │    │   Container      │    │  Cache Layer    │
│                 │    │   Apps           │    │                 │
├─────────────────┤    ├──────────────────┤    ├─────────────────┤
│ • SSL/TLS       │────│ • Frontend       │────│ • PostgreSQL    │
│ • Load Balancing│    │ • Backend APIs   │    │ • Redis Cache   │
│ • Web App       │    │ • Auto-scaling   │    │ • Private       │
│   Firewall      │    │ • Health Checks  │    │   Endpoints     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌────────────┴──────────────┐
                    │    Observability Stack    │
                    │                           │
                    │ • Application Insights   │
                    │ • Log Analytics          │
                    │ • Azure Monitor          │
                    │ • Key Vault Secrets      │
                    └───────────────────────────┘
```

### Technology Stack
```
- Infrastructure as Code: Bicep Templates
- Deployment Automation: Azure Developer CLI (azd)
- Container Platform: Azure Container Apps
- Database: Azure Database for PostgreSQL
- Cache: Azure Cache for Redis
- Load Balancer: Azure Application Gateway
- Security: Azure Key Vault
- Monitoring: Application Insights + Log Analytics
- Networking: Virtual Networks with Private Endpoints
```

## 📁 Infrastructure Components

### Azure Developer CLI Configuration (`/AZD-PROJECT/azure.yaml`)
```yaml
# Core project configuration
name: techbookstore
metadata:
  template: techbookstore-modernization@0.0.1-beta
  description: 'Azure deployment for legacy modernization workshop'

# Service definitions
services:
  backend:
    project: ../backend
    language: java
    host: containerapp
    docker:
      path: ../backend/Dockerfile
      context: ../backend
    env:
      SPRING_PROFILES_ACTIVE: ${AZURE_ENV_NAME}
      DATABASE_URL: ${DATABASE_URL}
      REDIS_URL: ${REDIS_URL}

  frontend:
    project: ../frontend
    language: js
    host: containerapp
    docker:
      path: ../frontend/Dockerfile
      context: ../frontend
    env:
      REACT_APP_API_BASE_URL: ${BACKEND_URL}
      REACT_APP_ENV: ${AZURE_ENV_NAME}

# Infrastructure configuration
infra:
  provider: bicep
  path: ../BICEP-TEMPLATES

# Multi-environment support
environments:
  dev:
    dotenv: .env.dev
  staging:
    dotenv: .env.staging
  prod:
    dotenv: .env.prod
```

### Bicep Template Structure (`/BICEP-TEMPLATES/`)
```
BICEP-TEMPLATES/
├── main.bicep                      # Main orchestration template
└── modules/                        # Modular infrastructure components
    ├── network.bicep              # Virtual networks and subnets
    ├── monitoring.bicep           # Application Insights and Log Analytics
    ├── keyvault.bicep            # Azure Key Vault for secrets
    ├── database.bicep            # PostgreSQL with private endpoints
    ├── redis.bicep               # Azure Cache for Redis
    ├── container-apps.bicep      # Container Apps environment
    ├── app-gateway.bicep         # Application Gateway with SSL
    └── security.bicep            # Security Center and compliance
```

## 🔧 Infrastructure Development Guidelines

### Main Template Pattern (`main.bicep`)
```bicep
@description('Environment name (dev, staging, prod)')
param environment string = 'dev'

@description('Application name prefix')
param appName string = 'techbookstore'

@description('Database administrator password')
@secure()
@minLength(12)
@maxLength(128)
param dbAdminPassword string

// Common variables for consistency
var commonTags = {
  Environment: environment
  Application: appName
  DeployedBy: 'Bicep'
  CostCenter: 'Engineering'
  Owner: 'TechBookStore-Team'
}

var resourceSuffix = '${appName}-${environment}'

// Module deployments with dependency management
module network 'modules/network.bicep' = {
  name: 'network-deployment'
  params: {
    location: location
    resourceSuffix: resourceSuffix
    tags: commonTags
  }
}

module database 'modules/database.bicep' = {
  name: 'database-deployment'
  params: {
    location: location
    resourceSuffix: resourceSuffix
    adminPassword: dbAdminPassword
    subnetId: network.outputs.databaseSubnetId
    logAnalyticsWorkspaceId: monitoring.outputs.logAnalyticsWorkspaceId
    tags: commonTags
  }
  dependsOn: [
    network
    monitoring
  ]
}

// Output values for application configuration
output frontendUrl string = appGateway.outputs.publicUrl
output backendUrl string = containerApps.outputs.backendUrl
output keyVaultName string = keyVault.outputs.keyVaultName
```

### Network Module Pattern (`modules/network.bicep`)
```bicep
@description('Location for all resources')
param location string

@description('Resource naming suffix')
param resourceSuffix string

@description('Common tags for all resources')
param tags object

// Virtual Network with multiple subnets
resource vnet 'Microsoft.Network/virtualNetworks@2023-04-01' = {
  name: 'vnet-${resourceSuffix}'
  location: location
  tags: tags
  properties: {
    addressSpace: {
      addressPrefixes: [
        '10.0.0.0/16'
      ]
    }
    subnets: [
      {
        name: 'subnet-apps'
        properties: {
          addressPrefix: '10.0.1.0/24'
          serviceEndpoints: [
            {
              service: 'Microsoft.KeyVault'
            }
            {
              service: 'Microsoft.Sql'
            }
          ]
        }
      }
      {
        name: 'subnet-database'
        properties: {
          addressPrefix: '10.0.2.0/24'
          delegations: [
            {
              name: 'dlg-database'
              properties: {
                serviceName: 'Microsoft.DBforPostgreSQL/flexibleServers'
              }
            }
          ]
        }
      }
      {
        name: 'subnet-gateway'
        properties: {
          addressPrefix: '10.0.3.0/24'
        }
      }
    ]
  }
}

// Outputs for other modules
output vnetId string = vnet.id
output appSubnetId string = vnet.properties.subnets[0].id
output databaseSubnetId string = vnet.properties.subnets[1].id
output gatewaySubnetId string = vnet.properties.subnets[2].id
```

### Container Apps Module (`modules/container-apps.bicep`)
```bicep
@description('Key Vault name for secrets')
param keyVaultName string

@description('Database connection string')
@secure()
param databaseConnectionString string

// Container Apps Environment
resource containerAppEnvironment 'Microsoft.App/managedEnvironments@2023-05-01' = {
  name: 'cae-${resourceSuffix}'
  location: location
  tags: tags
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: logAnalyticsWorkspace.properties.customerId
        sharedKey: logAnalyticsWorkspace.listKeys().primarySharedKey
      }
    }
    vnetConfiguration: {
      infrastructureSubnetId: subnetId
      internal: false
    }
  }
}

// Backend Container App
resource backendApp 'Microsoft.App/containerApps@2023-05-01' = {
  name: 'ca-backend-${resourceSuffix}'
  location: location
  tags: tags
  properties: {
    managedEnvironmentId: containerAppEnvironment.id
    configuration: {
      activeRevisionsMode: 'Single'
      ingress: {
        external: true
        targetPort: 8080
        allowInsecure: false
        traffic: [
          {
            weight: 100
            latestRevision: true
          }
        ]
      }
      secrets: [
        {
          name: 'database-connection-string'
          keyVaultUrl: '${keyVault.properties.vaultUri}secrets/DatabaseConnectionString'
          identity: userAssignedIdentity.id
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'backend'
          image: 'techbookstore/backend:latest'
          env: [
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: 'prod'
            }
            {
              name: 'DATABASE_URL'
              secretRef: 'database-connection-string'
            }
          ]
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 10
        rules: [
          {
            name: 'http-scaling'
            http: {
              metadata: {
                concurrentRequests: '30'
              }
            }
          }
        ]
      }
    }
  }
}

output backendUrl string = 'https://${backendApp.properties.configuration.ingress.fqdn}'
```

## 🚀 Deployment Workflows

### Environment Setup
```bash
# Initialize azd project
azd init

# Set environment variables
azd env set AZURE_LOCATION "japaneast"
azd env set DATABASE_PASSWORD "SecurePassword123!"

# Deploy infrastructure and applications
azd up

# Deploy only infrastructure
azd provision

# Deploy only applications
azd deploy

# View deployment status
azd show

# Clean up resources
azd down
```

### Multi-Environment Management
```bash
# Create development environment
azd env new dev
azd env select dev
azd env set AZURE_LOCATION "japaneast"
azd env set DATABASE_PASSWORD "DevPassword123!"

# Create staging environment
azd env new staging
azd env select staging
azd env set AZURE_LOCATION "japaneast"
azd env set DATABASE_PASSWORD "StagingPassword123!"

# Deploy to specific environment
azd up --environment dev
azd up --environment staging
```

## 🔐 Security Configuration

### Key Vault Integration
```bicep
// Key Vault with network restrictions
resource keyVault 'Microsoft.KeyVault/vaults@2023-02-01' = {
  name: 'kv-${resourceSuffix}'
  location: location
  tags: tags
  properties: {
    sku: {
      family: 'A'
      name: 'standard'
    }
    tenantId: subscription().tenantId
    enabledForDeployment: false
    enabledForTemplateDeployment: true
    enabledForDiskEncryption: false
    enableRbacAuthorization: true
    enableSoftDelete: true
    softDeleteRetentionInDays: 90
    enablePurgeProtection: true
    networkAcls: {
      bypass: 'AzureServices'
      defaultAction: 'Deny'
      virtualNetworkRules: [
        {
          id: appSubnetId
          ignoreMissingVnetServiceEndpoint: false
        }
      ]
    }
  }
}

// Managed Identity for Container Apps
resource userAssignedIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: 'id-${resourceSuffix}'
  location: location
  tags: tags
}

// Key Vault access policy
resource keyVaultAccessPolicy 'Microsoft.KeyVault/vaults/accessPolicies@2023-02-01' = {
  parent: keyVault
  name: 'add'
  properties: {
    accessPolicies: [
      {
        tenantId: subscription().tenantId
        objectId: userAssignedIdentity.properties.principalId
        permissions: {
          secrets: [
            'get'
            'list'
          ]
        }
      }
    ]
  }
}
```

### Database Security
```bicep
// PostgreSQL with private endpoint
resource postgresqlServer 'Microsoft.DBforPostgreSQL/flexibleServers@2023-03-01-preview' = {
  name: 'psql-${resourceSuffix}'
  location: location
  tags: tags
  sku: {
    name: 'Standard_B1ms'
    tier: 'Burstable'
  }
  properties: {
    administratorLogin: 'psqladmin'
    administratorLoginPassword: adminPassword
    version: '14'
    storage: {
      storageSizeGB: 128
    }
    backup: {
      backupRetentionDays: 7
      geoRedundantBackup: 'Disabled'
    }
    network: {
      delegatedSubnetResourceId: subnetId
      privateDnsZoneArmResourceId: privateDnsZone.id
    }
    highAvailability: {
      mode: 'Disabled'
    }
    maintenanceWindow: {
      customWindow: 'Enabled'
      dayOfWeek: 0
      startHour: 2
      startMinute: 0
    }
  }
}
```

## 📊 Monitoring and Observability

### Application Insights Configuration
```bicep
// Log Analytics Workspace
resource logAnalyticsWorkspace 'Microsoft.OperationalInsights/workspaces@2022-10-01' = {
  name: 'log-${resourceSuffix}'
  location: location
  tags: tags
  properties: {
    sku: {
      name: 'PerGB2018'
    }
    retentionInDays: 30
    features: {
      enableLogAccessUsingOnlyResourcePermissions: true
    }
  }
}

// Application Insights
resource applicationInsights 'Microsoft.Insights/components@2020-02-02' = {
  name: 'appi-${resourceSuffix}'
  location: location
  tags: tags
  kind: 'web'
  properties: {
    Application_Type: 'web'
    WorkspaceResourceId: logAnalyticsWorkspace.id
    IngestionMode: 'LogAnalytics'
    publicNetworkAccessForIngestion: 'Enabled'
    publicNetworkAccessForQuery: 'Enabled'
  }
}

// Output connection string for applications
output applicationInsightsConnectionString string = applicationInsights.properties.ConnectionString
output logAnalyticsWorkspaceId string = logAnalyticsWorkspace.id
```

### Custom Dashboards
```bicep
// Azure Dashboard for monitoring
resource dashboard 'Microsoft.Portal/dashboards@2020-09-01-preview' = {
  name: 'dashboard-${resourceSuffix}'
  location: location
  tags: tags
  properties: {
    lenses: [
      {
        order: 0
        parts: [
          {
            position: {
              x: 0
              y: 0
              rowSpan: 4
              colSpan: 6
            }
            metadata: {
              inputs: [
                {
                  name: 'resourceTypeMode'
                  isOptional: true
                }
                {
                  name: 'ComponentId'
                  value: {
                    SubscriptionId: subscription().subscriptionId
                    ResourceGroup: resourceGroup().name
                    Name: applicationInsights.name
                  }
                }
              ]
              type: 'Extension/AppInsightsExtension/PartType/AspNetOverviewPinnedPart'
            }
          }
        ]
      }
    ]
  }
}
```

## 🎯 Workshop Integration

### Deployment Hooks in azure.yaml
```yaml
hooks:
  preprovision:
    shell: sh
    run: |
      echo "🚀 Starting infrastructure provisioning..."
      echo "Environment: ${AZURE_ENV_NAME}"
      echo "Location: ${AZURE_LOCATION}"

  postprovision:
    shell: sh
    run: |
      echo "✅ Infrastructure provisioned successfully"

      # Set Key Vault secrets
      echo "🔑 Setting up Key Vault secrets..."
      az keyvault secret set --vault-name ${KEY_VAULT_NAME} --name "DatabasePassword" --value "${DATABASE_PASSWORD}"
      az keyvault secret set --vault-name ${KEY_VAULT_NAME} --name "JwtSecret" --value "${JWT_SECRET}"

  predeploy:
    shell: sh
    run: |
      echo "🔨 Building application images..."

      # Backend build and test
      cd backend
      ./mvnw clean package -DskipTests=false
      cd ..

      # Frontend build and test
      cd frontend
      npm ci
      npm run build
      npm test -- --coverage --watchAll=false
      cd ..

  postdeploy:
    shell: sh
    run: |
      echo "🧪 Running post-deployment verification..."

      FRONTEND_URL=$(azd env get-values | grep FRONTEND_URL | cut -d'=' -f2 | tr -d '"')
      BACKEND_URL=$(azd env get-values | grep BACKEND_URL | cut -d'=' -f2 | tr -d '"')

      # Health checks
      curl -f "$BACKEND_URL/actuator/health" || echo "⚠️ Backend health check failed"
      curl -f "$FRONTEND_URL" > /dev/null || echo "⚠️ Frontend check failed"

      echo "✅ Deployment verification completed"
```

### Cost Optimization
```bicep
// Parameter for environment-specific sizing
@allowed([
  'dev'
  'staging'
  'prod'
])
param environment string

// Variable SKU selection based on environment
var skuMap = {
  dev: {
    database: 'Standard_B1ms'
    redis: 'Basic'
    appGateway: 'Standard_v2'
  }
  staging: {
    database: 'Standard_B2s'
    redis: 'Standard'
    appGateway: 'Standard_v2'
  }
  prod: {
    database: 'Standard_D2s_v3'
    redis: 'Premium'
    appGateway: 'WAF_v2'
  }
}

// Use environment-appropriate SKUs
resource postgresqlServer 'Microsoft.DBforPostgreSQL/flexibleServers@2023-03-01-preview' = {
  sku: {
    name: skuMap[environment].database
    tier: environment == 'dev' ? 'Burstable' : 'GeneralPurpose'
  }
}
```

## 🎯 GitHub Copilot Prompts for Infrastructure

```
"Create a Bicep module for Azure Container Apps with auto-scaling and health checks"

"Generate a secure database configuration with private endpoints and backup policies"

"Help me implement proper tagging strategy across all Azure resources"

"Create monitoring alerts for Container Apps performance and availability"

"Optimize this Bicep template for cost efficiency across dev/staging/prod environments"

"Add disaster recovery configuration to this PostgreSQL deployment"
```

## 🚨 Important Considerations

### Environment-Specific Configurations
- **Development**: Cost-optimized, single-region, basic SKUs
- **Staging**: Production-like, moderate performance, limited redundancy
- **Production**: High availability, performance optimized, geo-redundant backups

### Security Best Practices
- **Network Isolation**: Private endpoints and VNet integration
- **Identity Management**: Managed identities for service-to-service auth
- **Secrets Management**: Azure Key Vault for all sensitive data
- **Compliance**: Security Center and policy enforcement

### Cost Management
- **Resource Tagging**: Consistent tagging for cost tracking
- **Auto-scaling**: Appropriate scaling policies for Container Apps
- **Resource Sizing**: Environment-appropriate SKU selection
- **Lifecycle Management**: Automated cleanup for development environments

This infrastructure setup provides a production-ready foundation for the TechBookStore workshop while demonstrating modern Azure best practices and cost-effective resource management.
