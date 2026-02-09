# 🚀 TechBookStore Azure Deployment - Quick Start Guide

## 概要

このガイドは、TechBookStore アプリケーションを Azure にデプロイするための最速の方法を提供します。

## 前提条件チェックリスト

- [ ] Azure CLI インストール済み (`az --version` で確認)
- [ ] Azure アカウントにログイン済み (`az login`)
- [ ] Azure サブスクリプションの権限あり (Contributor 以上)
- [ ] Docker インストール済み (アプリケーションデプロイ時に必要)
- [ ] PowerShell 5.1 以上 (Windows の場合)

## 📋 5ステップデプロイ

### Step 1: 環境変数の準備

PowerShell で以下を実行:

```powershell
# 必須: PostgreSQL 管理者の認証情報を設定
$env:POSTGRESQL_ADMIN_USERNAME = "techbookadmin"
$securePassword = Read-Host -AsSecureString -Prompt "PostgreSQL password"

# オプション: カスタマイズ可能
$env:AZURE_ENV_NAME = "techbookstore-prod"
$env:AZURE_LOCATION = "eastus2"
$env:POSTGRESQL_DATABASE_NAME = "techbookstore"
```

**重要:** PostgreSQL パスワードは以下の要件を満たす必要があります:
- 最低 8 文字
- 大文字、小文字、数字、記号を含む

### Step 2: リソースグループの作成

```powershell
az group create `
  --name rg-techbookstore-prod `
  --location eastus2
```

**所要時間:** 約 10 秒

### Step 3: インフラストラクチャのデプロイ

```powershell
cd infra
.\deploy.ps1 `
  -PostgresqlAdminUsername $env:POSTGRESQL_ADMIN_USERNAME `
  -PostgresqlAdminPassword $securePassword `
  -SkipConfirmation
```

**所要時間:** 約 15-20 分

このスクリプトは以下を実行します:
- ✅ Azure CLI と拡張機能の検証
- ✅ Bicep テンプレートのバリデーション
- ✅ すべての Azure リソースのプロビジョニング
- ✅ デプロイ出力の保存 (`.env` ファイル)

### Step 4: ポストプロビジョニング設定

```powershell
.\post-provision.ps1
```

**所要時間:** 約 2-3 分

このスクリプトは以下を実行します:
- ✅ PostgreSQL への Service Connector 接続設定
- ✅ Redis への Service Connector 接続設定
- ✅ マネージド ID の設定確認
- ✅ 接続のテスト

### Step 5: アプリケーションのデプロイ

```powershell
# .env ファイルから変数を読み込む
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#=]+)=(.*)$') {
        Set-Variable -Name $matches[1].Trim() -Value $matches[2].Trim()
    }
}

# Container Registry にログイン
az acr login --name $AZURE_CONTAINER_REGISTRY_NAME

# バックエンドイメージのビルドとプッシュ
cd ../backend
docker build -t ${AZURE_CONTAINER_REGISTRY_ENDPOINT}/backend:latest .
docker push ${AZURE_CONTAINER_REGISTRY_ENDPOINT}/backend:latest

# フロントエンドイメージのビルドとプッシュ
cd ../frontend
docker build -t ${AZURE_CONTAINER_REGISTRY_ENDPOINT}/frontend:latest .
docker push ${AZURE_CONTAINER_REGISTRY_ENDPOINT}/frontend:latest

# Container Apps の更新
az containerapp update `
  --name $BACKEND_APP_NAME `
  --resource-group $AZURE_RESOURCE_GROUP `
  --image ${AZURE_CONTAINER_REGISTRY_ENDPOINT}/backend:latest

az containerapp update `
  --name $FRONTEND_APP_NAME `
  --resource-group $AZURE_RESOURCE_GROUP `
  --image ${AZURE_CONTAINER_REGISTRY_ENDPOINT}/frontend:latest
```

**所要時間:** 約 10-15 分

## ✅ デプロイ確認

### アプリケーションの動作確認

```powershell
# バックエンド API のヘルスチェック
$backendUrl = $BACKEND_APP_URL
Invoke-WebRequest -Uri "$backendUrl/actuator/health"

# フロントエンドアプリケーションにアクセス
Start-Process $FRONTEND_APP_URL
```

### リソースの確認

```powershell
# デプロイされたリソースをリスト
az resource list `
  --resource-group rg-techbookstore-prod `
  --output table

# Container Apps のログを確認
az containerapp logs show `
  --name $BACKEND_APP_NAME `
  --resource-group rg-techbookstore-prod `
  --follow
```

## 🎯 主要なエンドポイント

デプロイ完了後、以下の URL が利用可能になります:

| サービス | URL | 説明 |
|---------|-----|------|
| Frontend | `$FRONTEND_APP_URL` | React アプリケーション |
| Backend API | `$BACKEND_APP_URL` | Spring Boot REST API |
| Health Check | `$BACKEND_APP_URL/actuator/health` | ヘルスチェック |
| API Docs | `$BACKEND_APP_URL/swagger-ui.html` | OpenAPI ドキュメント |

## 🔧 トラブルシューティング

### デプロイが失敗した場合

1. **エラーログの確認:**
   ```powershell
   az deployment group show `
     --resource-group rg-techbookstore-prod `
     --name main `
     --query properties.error
   ```

2. **リージョンの変更:**
   - エラーが "quota" や "capacity" に関連する場合、別のリージョンを試してください
   ```powershell
   $env:AZURE_LOCATION = "centralus"  # または westus2, swedencentral
   ```

3. **PostgreSQL SKU の変更:**
   - コスト削減や利用可能性のため、SKU を変更できます
   - `main.bicep` の `Standard_D2ds_v5` を `Standard_B1ms` に変更

### Container App が起動しない場合

1. **ログの確認:**
   ```powershell
   az containerapp logs show `
     --name $BACKEND_APP_NAME `
     --resource-group rg-techbookstore-prod `
     --tail 100
   ```

2. **リビジョンの確認:**
   ```powershell
   az containerapp revision list `
     --name $BACKEND_APP_NAME `
     --resource-group rg-techbookstore-prod `
     --output table
   ```

3. **環境変数の確認:**
   ```powershell
   az containerapp show `
     --name $BACKEND_APP_NAME `
     --resource-group rg-techbookstore-prod `
     --query properties.template.containers[0].env
   ```

### データベース接続エラーが発生した場合

1. **Service Connector の状態確認:**
   ```powershell
   az containerapp connection show `
     --resource-group rg-techbookstore-prod `
     --name $BACKEND_APP_NAME `
     --connection postgres-connection
   ```

2. **PostgreSQL ファイアウォール確認:**
   ```powershell
   az postgres flexible-server firewall-rule list `
     --resource-group rg-techbookstore-prod `
     --name $($AZURE_POSTGRESQL_HOST -replace '\..*$','') `
     --output table
   ```

3. **マネージド ID の確認:**
   ```powershell
   az containerapp show `
     --name $BACKEND_APP_NAME `
     --resource-group rg-techbookstore-prod `
     --query identity
   ```

## 🗑️ クリーンアップ

テスト後、すべてのリソースを削除する場合:

```powershell
# 確認付きで削除
az group delete --name rg-techbookstore-prod

# 確認なしで削除 (バックグラウンドで実行)
az group delete --name rg-techbookstore-prod --yes --no-wait
```

## 📚 追加リソース

- **詳細ガイド:** [`infra/README.md`](infra/README.md)
- **コンプライアンスレポート:** [`infra/compliance-report.md`](infra/compliance-report.md)
- **デプロイメントプラン:** [`.azure/plan.copilotmd`](.azure/plan.copilotmd)

## 💡 ヒント

### コスト最適化

開発環境では、以下の変更でコストを削減できます:

1. **PostgreSQL SKU の変更:**
   - `Standard_D2ds_v5` → `Standard_B1ms` (約 70% コスト削減)

2. **Container Apps のレプリカ数削減:**
   - `minReplicas: 1, maxReplicas: 3` → `minReplicas: 0, maxReplicas: 2`

3. **Redis SKU の変更:**
   - 現在既に最小の `Basic C0` を使用中

### セキュリティ強化

本番環境では、以下の設定を推奨します:

1. **Key Vault ネットワークルール:**
   ```powershell
   az keyvault network-rule add `
     --name $AZURE_KEY_VAULT_NAME `
     --ip-address <your-container-app-ip>
   ```

2. **PostgreSQL Private Endpoint:**
   - パブリックアクセスを無効化し、Private Endpoint 経由で接続

3. **Custom Domain と SSL:**
   ```powershell
   az containerapp hostname add `
     --hostname yourdomain.com `
     --name $FRONTEND_APP_NAME `
     --resource-group rg-techbookstore-prod
   ```

## ⏱️ 総所要時間

- **インフラストラクチャプロビジョニング:** 15-20 分
- **ポストプロビジョニング設定:** 2-3 分
- **アプリケーションビルドとデプロイ:** 10-15 分
- **合計:** 約 30-40 分

## 🆘 サポート

問題が発生した場合:

1. [`infra/README.md`](infra/README.md) のトラブルシューティングセクションを確認
2. [Azure サポート](https://azure.microsoft.com/support/) に問い合わせ
3. プロジェクトの GitHub Issues を確認

---

**最終更新:** 2026年2月9日  
**バージョン:** 1.0
