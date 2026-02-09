# Azure Infrastructure - TechBookStore

このディレクトリには、TechBookStore アプリケーションを Azure にデプロイするための Infrastructure as Code (IaC) ファイルが含まれています。

## 📁 ファイル構成

- `main.bicep` - メインの Bicep テンプレート (すべての Azure リソースを定義)
- `main.parameters.json` - デプロイメントパラメータファイル
- `deploy.ps1` - PowerShell デプロイスクリプト  
- `post-provision.ps1` - プロビジョニング後の設定スクリプト

## 🏗️ プロビジョニングされるリソース

| リソース種類 | リソース名パターン | 目的 |
|------------|----------------|------|
| Resource Group | rg-techbookstore-prod | すべてのリソースを含むコンテナ |
| Container Apps Environment | azcae{token} | Container Apps のホスティング環境 |
| Container App (Backend) | azcaback{token} | Spring Boot アプリケーション |
| Container App (Frontend) | azcafront{token} | React + Nginx アプリケーション |
| Container Registry | azcr{token} | コンテナイメージストレージ |
| PostgreSQL Server | azpsql{token} | 本番データベース |
| Redis Cache | azredis{token} | キャッシュとセッション管理 |
| Key Vault | azkv{token} | シークレット管理 |
| Application Insights | azai{token} | アプリケーション監視 |
| Log Analytics | azlog{token} | ログ集約 |
| User Managed Identity | azid{token} | マネージド ID (Container Apps 用) |

*{token} は `uniqueString()` 関数で生成される一意の文字列です*

## 🚀 デプロイ手順

### 前提条件

1. **Azure CLI** がインストールされていること
   ```powershell
   az --version
   ```

2. **Azure にログイン**
   ```powershell
   az login
   az account set --subscription <subscription-id>
   ```

3. **Service Connector 拡張機能をインストール**
   ```powershell
   az extension add --name serviceconnector-passwordless --upgrade
   ```

### ステップ 1: 環境変数の設定

```powershell
$env:AZURE_ENV_NAME = "techbookstore-prod"
$env:AZURE_LOCATION = "eastus2"
$env:POSTGRESQL_ADMIN_USERNAME = "techbookadmin"
$env:POSTGRESQL_ADMIN_PASSWORD = "<strong-password>"  # 強力なパスワードを設定
$env:POSTGRESQL_DATABASE_NAME = "techbookstore"
```

### ステップ 2: リソースグループの作成

```powershell
az group create `
  --name rg-techbookstore-prod `
  --location eastus2
```

### ステップ 3: インフラストラクチャのデプロイ

```powershell
az deployment group create `
  --resource-group rg-techbookstore-prod `
  --template-file infra/main.bicep `
  --parameters infra/main.parameters.json `
  --parameters `
    environmentName=$env:AZURE_ENV_NAME `
    location=$env:AZURE_LOCATION `
    postgresqlAdminUsername=$env:POSTGRESQL_ADMIN_USERNAME `
    postgresqlAdminPassword=$env:POSTGRESQL_ADMIN_PASSWORD `
    postgresqlDatabaseName=$env:POSTGRESQL_DATABASE_NAME
```

### ステップ 4: デプロイ出力の取得

```powershell
$deployment = az deployment group show `
  --resource-group rg-techbookstore-prod `
  --name main `
  --query properties.outputs `
  -o json | ConvertFrom-Json

$backendAppName = $deployment.BACKEND_APP_NAME.value
$frontendAppName = $deployment.FRONTEND_APP_NAME.value
$postgresqlHost = $deployment.AZURE_POSTGRESQL_HOST.value
$postgresqlDatabase = $deployment.AZURE_POSTGRESQL_DATABASE.value
$subscriptionId = $deployment.AZURE_SUBSCRIPTION_ID.value
$userIdentityClientId = $deployment.USER_MANAGED_IDENTITY_CLIENT_ID.value

Write-Host "✓ Backend App: $backendAppName"
Write-Host "✓ Frontend App: $frontendAppName"
Write-Host "✓ PostgreSQL: $postgresqlHost"
```

### ステップ 5: Post-Provisioning 設定

#### PostgreSQL 接続の設定 (System Managed Identity)

```powershell
az containerapp connection create postgres-flexible `
  --connection postgres-connection `
  --source-id "/subscriptions/$subscriptionId/resourceGroups/rg-techbookstore-prod/providers/Microsoft.App/containerApps/$backendAppName" `
  --target-id "/subscriptions/$subscriptionId/resourceGroups/rg-techbookstore-prod/providers/Microsoft.DBforPostgreSQL/flexibleServers/$($deployment.AZURE_POSTGRESQL_HOST.value -replace '\..*$','')" `
  --database $postgresqlDatabase `
  --system-identity `
  --client-type springBoot `
  -y
```

**注:** Service Connector は Spring Boot アプリケーションに必要な環境変数を自動的に設定します

#### Redis 接続の設定 (System Managed Identity)

```powershell
$redisName = $deployment.AZURE_REDIS_HOST.value -replace '\..*$',''

az containerapp connection create redis `
  --connection redis-connection `
  --source-id "/subscriptions/$subscriptionId/resourceGroups/rg-techbookstore-prod/providers/Microsoft.App/containerApps/$backendAppName" `
  --target-id "/subscriptions/$subscriptionId/resourceGroups/rg-techbookstore-prod/providers/Microsoft.Cache/redis/$redisName" `
  --system-identity `
  --client-type springBoot `
  -y
```

#### 接続の確認

```powershell
# PostgreSQL 接続の確認
az containerapp connection show `
  --resource-group rg-techbookstore-prod `
  --name $backendAppName `
  --connection postgres-connection `
  -o json

# Redis 接続の確認
az containerapp connection show `
  --resource-group rg-techbookstore-prod `
  --name $backendAppName `
  --connection redis-connection `
  -o json

# すべての接続をリスト
az containerapp connection list `
  --resource-group rg-techbookstore-prod `
  --name $backendAppName `
  -o table
```

## 🔐 セキュリティ設定

### Key Vault ロール割り当て

Bicep テンプレートは、User-Assigned Managed Identity に以下のロールを自動的に割り当てます:

- **Key Vault Secrets Officer** - シークレットの管理
- **Key Vault Secrets User** - シークレットの読み取り
- **AcrPull** - Container Registry からのイメージ取得

### マネージド ID の確認

```powershell
# User-Assigned Managed Identity の確認
az identity show `
  --name $deployment.USER_MANAGED_IDENTITY_NAME.value `
  --resource-group rg-techbookstore-prod

# Backend Container App の System-Assigned Managed Identity の確認
az containerapp show `
  --name $backendAppName `
  --resource-group rg-techbookstore-prod `
  --query identity
```

## 📊 リソースの確認

### すべてのリソースをリスト

```powershell
az resource list `
  --resource-group rg-techbookstore-prod `
  --output table
```

### Container Apps のエンドポイントを取得

```powershell
# Backend URL
$backendUrl = $deployment.BACKEND_APP_URL.value
Write-Host "Backend API: $backendUrl"

# Frontend URL
$frontendUrl = $deployment.FRONTEND_APP_URL.value
Write-Host "Frontend App: $frontendUrl"
```

### Application Insights の確認

```powershell
az monitor app-insights component show `
  --app $deployment.AZURE_APPINSIGHTS_NAME.value `
  --resource-group rg-techbookstore-prod
```

## 🗑️ リソースのクリーンアップ

すべてのリソースを削除する場合:

```powershell
az group delete `
  --name rg-techbookstore-prod `
  --yes `
  --no-wait
```

## 📝 重要な注意事項

1. **Container Images**: 初期デプロイ時は、Container Apps は `mcr.microsoft.com/azuredocs/containerapps-helloworld:latest` イメージを使用します。実際のアプリケーションイメージは別途デプロイが必要です。

2. **Service Connector**: Service Connector は Spring Boot アプリケーション用に以下の環境変数を自動設定します:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME` (Managed Identity を使用する場合は不要)
   - `SPRING_DATASOURCE_PASSWORD` (Managed Identity を使用する場合は不要)

3. **Key Vault ネットワークルール**: Key Vault は最初にすべてのネットワークからのアクセスを許可するように設定されています。デプロイ後、Container App の IP アドレスを取得してネットワークルールを厳格化できます。

4. **コスト最適化**: 開発環境では、以下の SKU を使用してコストを削減できます:
   - PostgreSQL: `Burstable B1ms` (現在は `GeneralPurpose Standard_D2ds_v5` を使用)
   - Redis: `Basic C0` (現在の設定)

## 🔄 次のステップ

1. **アプリケーションのビルドとデプロイ**:
   - Docker イメージをビルド
   - Container Registry にプッシュ
   - Container Apps を更新

2. **カスタムドメインの設定** (オプション):
   ```powershell
   az containerapp hostname add `
     --hostname example.com `
     --name $frontendAppName `
     --resource-group rg-techbookstore-prod
   ```

3. **スケーリングルールの設定**:
   ```powershell
   az containerapp update `
     --name $backendAppName `
     --resource-group rg-techbookstore-prod `
     --min-replicas 1 `
     --max-replicas 10
   ```

## 🆘 トラブルシューティング

### デプロイメントログの確認

```powershell
az deployment group show `
  --resource-group rg-techbookstore-prod `
  --name main `
  --query properties.error
```

### Container App ログの確認

```powershell
az containerapp logs show `
  --name $backendAppName `
  --resource-group rg-techbookstore-prod `
  --follow
```

### PostgreSQL 接続のテスト

```powershell
# psql クライアントを使用
psql "host=$postgresqlHost port=5432 dbname=$postgresqlDatabase user=$env:POSTGRESQL_ADMIN_USERNAME sslmode=require"
```
