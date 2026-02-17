targetScope = 'resourceGroup'

@minLength(1)
@maxLength(64)
@description('Name of the environment used to generate a short unique hash for resources.')
param environmentName string

@minLength(1)
@description('Primary location for all resources')
@allowed([
  'eastus2'
  'centralus'
  'westus2'
  'swedencentral'
  'southeastasia'
])
param location string = 'eastus2'

@description('PostgreSQL administrator username')
@secure()
param postgresqlAdminUsername string

@description('PostgreSQL administrator password')
@secure()
param postgresqlAdminPassword string

@description('PostgreSQL database name')
param postgresqlDatabaseName string = 'techbookstore'

// Generate unique resource token
var resourceToken = uniqueString(subscription().id, resourceGroup().id, location, environmentName)

// Resource naming following the pattern: az{resourcePrefix}{resourceToken}
var containerRegistryName = 'azcr${resourceToken}'
var logAnalyticsName = 'azlog${resourceToken}'
var appInsightsName = 'azai${resourceToken}'
var keyVaultName = 'azkv${resourceToken}'
var containerAppsEnvName = 'azcae${resourceToken}'
var postgresqlServerName = 'azpsql${resourceToken}'
var redisName = 'azredis${resourceToken}'
var backendAppName = 'azcaback${resourceToken}'
var frontendAppName = 'azcafront${resourceToken}'
var userManagedIdentityName = 'azid${resourceToken}'

// Tags for all resources
var tags = {
  'azd-env-name': environmentName
  project: 'TechBookStore'
}

// User-Assigned Managed Identity for Container Apps
resource userManagedIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: userManagedIdentityName
  location: location
  tags: tags
}

// Container Registry
resource containerRegistry 'Microsoft.ContainerRegistry/registries@2023-07-01' = {
  name: containerRegistryName
  location: location
  tags: tags
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: false
    publicNetworkAccess: 'Enabled'
  }
}

// AcrPull role assignment for user-assigned managed identity
resource acrPullRoleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(containerRegistry.id, userManagedIdentity.id, 'acrpull')
  scope: containerRegistry
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', '7f951dda-4ed3-4680-a7ca-43fe172d538d') // AcrPull role
    principalId: userManagedIdentity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

// Log Analytics Workspace
resource logAnalytics 'Microsoft.OperationalInsights/workspaces@2022-10-01' = {
  name: logAnalyticsName
  location: location
  tags: tags
  properties: {
    sku: {
      name: 'PerGB2018'
    }
    retentionInDays: 30
  }
}

// Application Insights
resource appInsights 'Microsoft.Insights/components@2020-02-02' = {
  name: appInsightsName
  location: location
  tags: tags
  kind: 'web'
  properties: {
    Application_Type: 'web'
    WorkspaceResourceId: logAnalytics.id
  }
}

// Key Vault
resource keyVault 'Microsoft.KeyVault/vaults@2023-07-01' = {
  name: keyVaultName
  location: location
  tags: tags
  properties: {
    sku: {
      family: 'A'
      name: 'standard'
    }
    tenantId: subscription().tenantId
    enableRbacAuthorization: true
    publicNetworkAccess: 'Enabled'
    networkAcls: {
      defaultAction: 'Allow'
      bypass: 'AzureServices'
    }
  }
}

// Key Vault Secrets Officer role for user-assigned managed identity
resource keyVaultSecretsOfficerRole 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(keyVault.id, userManagedIdentity.id, 'secretsofficer')
  scope: keyVault
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', 'b86a8fe4-44ce-4948-aee5-eccb2c155cd7') // Key Vault Secrets Officer
    principalId: userManagedIdentity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

// Key Vault Secrets User role for user-assigned managed identity
resource keyVaultSecretsUserRole 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(keyVault.id, userManagedIdentity.id, 'secretsuser')
  scope: keyVault
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', '4633458b-17de-408a-b874-0445c86b69e6') // Key Vault Secrets User
    principalId: userManagedIdentity.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

// PostgreSQL Flexible Server
resource postgresqlServer 'Microsoft.DBforPostgreSQL/flexibleServers@2023-03-01-preview' = {
  name: postgresqlServerName
  location: location
  tags: tags
  sku: {
    name: 'Standard_D2ds_v5'
    tier: 'GeneralPurpose'
  }
  properties: {
    version: '17'
    administratorLogin: postgresqlAdminUsername
    administratorLoginPassword: postgresqlAdminPassword
    storage: {
      storageSizeGB: 32
      autoGrow: 'Enabled'
    }
    backup: {
      backupRetentionDays: 7
      geoRedundantBackup: 'Disabled'
    }
    highAvailability: {
      mode: 'Disabled'
    }
  }
}

// PostgreSQL Database
resource postgresqlDatabase 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2023-03-01-preview' = {
  parent: postgresqlServer
  name: postgresqlDatabaseName
  properties: {
    charset: 'UTF8'
    collation: 'en_US.UTF8'
  }
}

// PostgreSQL Firewall Rule for Azure Services
resource postgresqlFirewallAzure 'Microsoft.DBforPostgreSQL/flexibleServers/firewallRules@2023-03-01-preview' = {
  parent: postgresqlServer
  name: 'AllowAzureServices'
  properties: {
    startIpAddress: '0.0.0.0'
    endIpAddress: '0.0.0.0'
  }
}

// Store PostgreSQL connection string in Key Vault
resource postgresqlConnectionStringSecret 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: keyVault
  name: 'postgresql-connection-string'
  properties: {
    value: 'jdbc:postgresql://${postgresqlServer.properties.fullyQualifiedDomainName}:5432/${postgresqlDatabaseName}?ssl=true&sslmode=require'
  }
  dependsOn: [
    keyVaultSecretsOfficerRole
  ]
}

// Store PostgreSQL username in Key Vault
resource postgresqlUsernameSecret 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: keyVault
  name: 'postgresql-username'
  properties: {
    value: postgresqlAdminUsername
  }
  dependsOn: [
    keyVaultSecretsOfficerRole
  ]
}

// Store PostgreSQL password in Key Vault
resource postgresqlPasswordSecret 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: keyVault
  name: 'postgresql-password'
  properties: {
    value: postgresqlAdminPassword
  }
  dependsOn: [
    keyVaultSecretsOfficerRole
  ]
}

// Azure Cache for Redis
resource redis 'Microsoft.Cache/redis@2023-08-01' = {
  name: redisName
  location: location
  tags: tags
  properties: {
    sku: {
      name: 'Basic'
      family: 'C'
      capacity: 0
    }
    enableNonSslPort: false
    minimumTlsVersion: '1.2'
    publicNetworkAccess: 'Enabled'
  }
}

// Store Redis connection string in Key Vault
resource redisConnectionStringSecret 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: keyVault
  name: 'redis-connection-string'
  properties: {
    value: '${redis.properties.hostName}:${redis.properties.sslPort},password=${redis.listKeys().primaryKey},ssl=True,abortConnect=False'
  }
  dependsOn: [
    keyVaultSecretsOfficerRole
  ]
}

// Store Redis host in Key Vault
resource redisHostSecret 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: keyVault
  name: 'redis-host'
  properties: {
    value: redis.properties.hostName
  }
  dependsOn: [
    keyVaultSecretsOfficerRole
  ]
}

// Store Redis key in Key Vault
resource redisKeySecret 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: keyVault
  name: 'redis-key'
  properties: {
    value: redis.listKeys().primaryKey
  }
  dependsOn: [
    keyVaultSecretsOfficerRole
  ]
}

// Container Apps Environment
resource containerAppsEnv 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: containerAppsEnvName
  location: location
  tags: tags
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: logAnalytics.properties.customerId
        sharedKey: logAnalytics.listKeys().primarySharedKey
      }
    }
  }
}

// Backend Container App
resource backendApp 'Microsoft.App/containerApps@2024-03-01' = {
  name: backendAppName
  location: location
  tags: union(tags, { 'azd-service-name': 'backend' })
  identity: {
    type: 'SystemAssigned,UserAssigned'
    userAssignedIdentities: {
      '${userManagedIdentity.id}': {}
    }
  }
  properties: {
    environmentId: containerAppsEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8080
        transport: 'http'
        corsPolicy: {
          allowedOrigins: ['*']
          allowedMethods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS']
          allowedHeaders: ['*']
          allowCredentials: false
        }
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          identity: userManagedIdentity.id
        }
      ]
      secrets: [
        {
          name: 'postgresql-connection-string'
          value: 'jdbc:postgresql://${postgresqlServer.properties.fullyQualifiedDomainName}:5432/${postgresqlDatabaseName}?ssl=true&sslmode=require'
        }
        {
          name: 'postgresql-username'
          value: postgresqlAdminUsername
        }
        {
          name: 'postgresql-password'
          value: postgresqlAdminPassword
        }
        {
          name: 'redis-host'
          value: redis.properties.hostName
        }
        {
          name: 'redis-key'
          value: redis.listKeys().primaryKey
        }
        {
          name: 'appinsights-key'
          value: appInsights.properties.InstrumentationKey
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'backend'
          image: 'mcr.microsoft.com/azuredocs/containerapps-helloworld:latest'
          resources: {
            cpu: json('1.0')
            memory: '2Gi'
          }
          env: [
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: 'prod'
            }
            {
              name: 'AZURE_KEYVAULT_ENDPOINT'
              value: keyVault.properties.vaultUri
            }
            {
              name: 'AZURE_CLIENT_ID'
              value: userManagedIdentity.properties.clientId
            }
            {
              name: 'APPLICATIONINSIGHTS_INSTRUMENTATION_KEY'
              secretRef: 'appinsights-key'
            }
            {
              name: 'SPRING_DATASOURCE_URL'
              secretRef: 'postgresql-connection-string'
            }
            {
              name: 'SPRING_DATASOURCE_USERNAME'
              secretRef: 'postgresql-username'
            }
            {
              name: 'SPRING_DATASOURCE_PASSWORD'
              secretRef: 'postgresql-password'
            }
            {
              name: 'SPRING_DATA_REDIS_HOST'
              secretRef: 'redis-host'
            }
            {
              name: 'SPRING_DATA_REDIS_PASSWORD'
              secretRef: 'redis-key'
            }
            {
              name: 'SPRING_DATA_REDIS_PORT'
              value: '6380'
            }
            {
              name: 'SPRING_DATA_REDIS_SSL_ENABLED'
              value: 'true'
            }
          ]
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 3
      }
    }
  }
  dependsOn: [
    acrPullRoleAssignment
    keyVaultSecretsUserRole
    postgresqlConnectionStringSecret
    postgresqlUsernameSecret
    postgresqlPasswordSecret
    redisHostSecret
    redisKeySecret
  ]
}

// Frontend Container App
resource frontendApp 'Microsoft.App/containerApps@2024-03-01' = {
  name: frontendAppName
  location: location
  tags: union(tags, { 'azd-service-name': 'frontend' })
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${userManagedIdentity.id}': {}
    }
  }
  properties: {
    environmentId: containerAppsEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 80
        transport: 'http'
        corsPolicy: {
          allowedOrigins: ['*']
          allowedMethods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS']
          allowedHeaders: ['*']
          allowCredentials: false
        }
      }
      registries: [
        {
          server: containerRegistry.properties.loginServer
          identity: userManagedIdentity.id
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'frontend'
          image: 'mcr.microsoft.com/azuredocs/containerapps-helloworld:latest'
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
          env: [
            {
              name: 'REACT_APP_API_URL'
              value: 'https://${backendApp.properties.configuration.ingress.fqdn}'
            }
          ]
        }
      ]
      scale: {
        minReplicas: 1
        maxReplicas: 2
      }
    }
  }
  dependsOn: [
    acrPullRoleAssignment
  ]
}

// Outputs
output AZURE_LOCATION string = location
output AZURE_TENANT_ID string = subscription().tenantId
output AZURE_SUBSCRIPTION_ID string = subscription().subscriptionId
output AZURE_RESOURCE_GROUP string = resourceGroup().name
output RESOURCE_GROUP_ID string = resourceGroup().id

output AZURE_CONTAINER_REGISTRY_NAME string = containerRegistry.name
output AZURE_CONTAINER_REGISTRY_ENDPOINT string = containerRegistry.properties.loginServer

output AZURE_KEY_VAULT_NAME string = keyVault.name
output AZURE_KEY_VAULT_ENDPOINT string = keyVault.properties.vaultUri

output AZURE_POSTGRESQL_HOST string = postgresqlServer.properties.fullyQualifiedDomainName
output AZURE_POSTGRESQL_DATABASE string = postgresqlDatabaseName

output AZURE_REDIS_HOST string = redis.properties.hostName
output AZURE_REDIS_PORT int = redis.properties.sslPort

output AZURE_APPINSIGHTS_NAME string = appInsights.name
output AZURE_APPINSIGHTS_INSTRUMENTATION_KEY string = appInsights.properties.InstrumentationKey
output AZURE_APPINSIGHTS_CONNECTION_STRING string = appInsights.properties.ConnectionString

output BACKEND_APP_NAME string = backendApp.name
output BACKEND_APP_URL string = 'https://${backendApp.properties.configuration.ingress.fqdn}'

output FRONTEND_APP_NAME string = frontendApp.name
output FRONTEND_APP_URL string = 'https://${frontendApp.properties.configuration.ingress.fqdn}'

output USER_MANAGED_IDENTITY_NAME string = userManagedIdentity.name
output USER_MANAGED_IDENTITY_CLIENT_ID string = userManagedIdentity.properties.clientId
output USER_MANAGED_IDENTITY_PRINCIPAL_ID string = userManagedIdentity.properties.principalId
