# CI/CD パイプライン セットアップガイド

このガイドでは、GitHub ActionsとAzureを使用したCI/CDパイプラインの設定手順を説明します。

## 📋 前提条件

- Azure サブスクリプション
- Azure CLI がインストールされていること
- GitHub CLI (`gh`) がインストールされていること（推奨）
- リポジトリへの管理者アクセス権限

## 🏗️ アーキテクチャ概要

このCI/CDパイプラインは以下の3つのワークフローで構成されています：

1. **CI（継続的インテグレーション）**: ビルドとテスト
2. **Infrastructure Deployment**: Azureインフラストラクチャのプロビジョニング
3. **CD（継続的デプロイメント）**: 複数環境へのアプリケーションデプロイ

### デプロイメント環境

- **dev**: 開発環境（自動デプロイ）
- **staging**: ステージング環境（承認後デプロイ）
- **production**: 本番環境（承認後デプロイ）

## 🔐 ステップ1: Azure認証の設定

### 1.1 自動セットアップスクリプトの実行

プロジェクトルートから以下のスクリプトを実行します：

```powershell
.\scripts\setup-azure-auth-for-pipeline.ps1
```

このスクリプトは以下を自動的に実行します：
- User-Assigned Managed Identityの作成
- Federated Credentialsの設定（dev、staging、production環境用）
- 必要なRBACロールの割り当て

### 1.2 手動設定（スクリプトが使用できない場合）

#### Azureにログイン
```powershell
az login
az account set --subscription "<YOUR_SUBSCRIPTION_ID>"
```

#### 環境変数の設定
```powershell
$SUBSCRIPTION_ID = "<YOUR_SUBSCRIPTION_ID>"
$RESOURCE_GROUP_IDENTITY = "rg-pipeline-identity"
$LOCATION = "eastus2"
$IDENTITY_NAME = "id-github-actions-pipeline"
$GITHUB_ORG = "shinyay"  # GitHubの組織名またはユーザー名
$GITHUB_REPO = "legacy-spring-boot-sample"  # リポジトリ名
```

#### Managed Identityの作成
```powershell
# リソースグループの作成
az group create --name $RESOURCE_GROUP_IDENTITY --location $LOCATION

# User-Assigned Managed Identityの作成
az identity create --name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY --location $LOCATION

# Identity情報の取得
$IDENTITY_CLIENT_ID = az identity show --name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY --query clientId -o tsv
$IDENTITY_PRINCIPAL_ID = az identity show --name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY --query principalId -o tsv
```

#### Federated Credentialsの設定

各環境用のフェデレーテッド資格情報を作成します：

**Dev環境:**
```powershell
az identity federated-credential create `
  --name "github-actions-dev" `
  --identity-name $IDENTITY_NAME `
  --resource-group $RESOURCE_GROUP_IDENTITY `
  --issuer "https://token.actions.githubusercontent.com" `
  --subject "repo:${GITHUB_ORG}/${GITHUB_REPO}:environment:dev" `
  --audiences "api://AzureADTokenExchange"
```

**Staging環境:**
```powershell
az identity federated-credential create `
  --name "github-actions-staging" `
  --identity-name $IDENTITY_NAME `
  --resource-group $RESOURCE_GROUP_IDENTITY `
  --issuer "https://token.actions.githubusercontent.com" `
  --subject "repo:${GITHUB_ORG}/${GITHUB_REPO}:environment:staging" `
  --audiences "api://AzureADTokenExchange"
```

**Production環境:**
```powershell
az identity federated-credential create `
  --name "github-actions-production" `
  --identity-name $IDENTITY_NAME `
  --resource-group $RESOURCE_GROUP_IDENTITY `
  --issuer "https://token.actions.githubusercontent.com" `
  --subject "repo:${GITHUB_ORG}/${GITHUB_REPO}:environment:production" `
  --audiences "api://AzureADTokenExchange"
```

#### RBACロールの割り当て

各環境のリソースグループに対して、Contributorロールを割り当てます：

```powershell
# Dev環境
az role assignment create `
  --assignee $IDENTITY_PRINCIPAL_ID `
  --role "Contributor" `
  --scope "/subscriptions/${SUBSCRIPTION_ID}/resourceGroups/rg-techbookstore-dev"

# Staging環境
az role assignment create `
  --assignee $IDENTITY_PRINCIPAL_ID `
  --role "Contributor" `
  --scope "/subscriptions/${SUBSCRIPTION_ID}/resourceGroups/rg-techbookstore-staging"

# Production環境
az role assignment create `
  --assignee $IDENTITY_PRINCIPAL_ID `
  --role "Contributor" `
  --scope "/subscriptions/${SUBSCRIPTION_ID}/resourceGroups/rg-techbookstore-production"
```

## 🌍 ステップ2: GitHub環境の設定

### 2.1 GitHub CLI を使用した設定（推奨）

```powershell
# 各環境の作成
gh api -X PUT /repos/$GITHUB_ORG/$GITHUB_REPO/environments/dev
gh api -X PUT /repos/$GITHUB_ORG/$GITHUB_REPO/environments/staging
gh api -X PUT /repos/$GITHUB_ORG/$GITHUB_REPO/environments/production
```

### 2.2 GitHub UI を使用した設定

1. GitHubリポジトリに移動
2. **Settings** → **Environments** に移動
3. **New environment** をクリック
4. 以下の環境を作成：
   - `dev`
   - `staging`
   - `production`

### 2.3 環境保護ルールの設定

**Staging環境:**
1. `staging` 環境をクリック
2. **Environment protection rules** で以下を設定：
   - ✅ Required reviewers（最低1人のレビュアーを追加）
   - Deployment branches: `Selected branches` → `main`

**Production環境:**
1. `production` 環境をクリック
2. **Environment protection rules** で以下を設定：
   - ✅ Required reviewers（最低2人のレビュアーを追加推奨）
   - Deployment branches: `Selected branches` → `main`

## 🔑 ステップ3: GitHub Secretsの設定

### 3.1 リポジトリレベルのSecrets

以下のシークレットを設定します（全環境で共通）：

```powershell
# GitHub CLI を使用
gh secret set AZURE_CLIENT_ID --body $IDENTITY_CLIENT_ID
gh secret set AZURE_TENANT_ID --body $(az account show --query tenantId -o tsv)
gh secret set AZURE_SUBSCRIPTION_ID --body $SUBSCRIPTION_ID
gh secret set POSTGRESQL_ADMIN_USERNAME --body "techbookadmin"
gh secret set POSTGRESQL_ADMIN_PASSWORD --body "<SECURE_PASSWORD>"
```

または、GitHub UIで設定：
1. **Settings** → **Secrets and variables** → **Actions**
2. **New repository secret** をクリック
3. 以下を追加：
   - `AZURE_CLIENT_ID`: Managed IdentityのClient ID
   - `AZURE_TENANT_ID`: AzureテナントID
   - `AZURE_SUBSCRIPTION_ID`: AzureサブスクリプションID
   - `POSTGRESQL_ADMIN_USERNAME`: PostgreSQL管理者ユーザー名
   - `POSTGRESQL_ADMIN_PASSWORD`: PostgreSQL管理者パスワード

### 3.2 環境変数の設定

各環境に対して以下の変数を設定します：

#### Dev環境の変数:
```powershell
gh variable set AZURE_RESOURCE_GROUP --body "rg-techbookstore-dev" --env dev
gh variable set AZURE_LOCATION --body "eastus2" --env dev
gh variable set AZURE_CONTAINER_REGISTRY_NAME --body "<ACR_NAME_DEV>" --env dev
gh variable set BACKEND_APP_NAME --body "<BACKEND_APP_NAME_DEV>" --env dev
gh variable set FRONTEND_APP_NAME --body "<FRONTEND_APP_NAME_DEV>" --env dev
```

#### Staging環境の変数:
```powershell
gh variable set AZURE_RESOURCE_GROUP --body "rg-techbookstore-staging" --env staging
gh variable set AZURE_LOCATION --body "eastus2" --env staging
gh variable set AZURE_CONTAINER_REGISTRY_NAME --body "<ACR_NAME_STAGING>" --env staging
gh variable set BACKEND_APP_NAME --body "<BACKEND_APP_NAME_STAGING>" --env staging
gh variable set FRONTEND_APP_NAME --body "<FRONTEND_APP_NAME_STAGING>" --env staging
```

#### Production環境の変数:
```powershell
gh variable set AZURE_RESOURCE_GROUP --body "rg-techbookstore-production" --env production
gh variable set AZURE_LOCATION --body "eastus2" --env production
gh variable set AZURE_CONTAINER_REGISTRY_NAME --body "<ACR_NAME_PRODUCTION>" --env production
gh variable set BACKEND_APP_NAME --body "<BACKEND_APP_NAME_PRODUCTION>" --env production
gh variable set FRONTEND_APP_NAME --body "<FRONTEND_APP_NAME_PRODUCTION>" --env production
```

**注意**: `<ACR_NAME_*>`, `<BACKEND_APP_NAME_*>`, `<FRONTEND_APP_NAME_*>` は、インフラストラクチャデプロイメント後に取得できる値です。最初のインフラデプロイメント後に設定してください。

## 🚀 ステップ4: パイプラインの実行

### 4.1 インフラストラクチャのデプロイ

1. GitHubリポジトリの **Actions** タブに移動
2. **Infrastructure Deployment** ワークフローを選択
3. **Run workflow** をクリック
4. 環境を選択（例：`dev`）
5. **Run workflow** を実行

デプロイメント完了後、出力からリソース名を取得し、ステップ3.2の環境変数を更新します。

### 4.2 アプリケーションのデプロイ

初回インフラデプロイメント後：

1. `main` ブランチにコードをプッシュ
2. CI/CDパイプラインが自動的に実行されます
   - CI: ビルドとテスト
   - CD: Dev → Staging → Production の順にデプロイ

または、手動実行：
1. **Actions** タブ → **CD - Deploy to Azure**
2. **Run workflow** をクリック

## 📊 パイプラインワークフロー

```
┌─────────────────────────────────────────────────────────────┐
│                    Code Push to main                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  CI: Build & Test                                            │
│  - Backend build (Maven)                                     │
│  - Frontend build (npm)                                      │
│  - Docker build verification                                 │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  CD: Deploy to Dev                                           │
│  - Build & push Docker images to ACR                         │
│  - Update Container Apps                                     │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼ (Requires approval)
┌─────────────────────────────────────────────────────────────┐
│  CD: Deploy to Staging                                       │
│  - Build & push Docker images to ACR                         │
│  - Update Container Apps                                     │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼ (Requires approval)
┌─────────────────────────────────────────────────────────────┐
│  CD: Deploy to Production                                    │
│  - Build & push Docker images to ACR                         │
│  - Update Container Apps                                     │
└─────────────────────────────────────────────────────────────┘
```

## 🔍 トラブルシューティング

### OIDC認証エラー

**エラー**: `Error: Login failed with Error: Unable to get OIDC token`

**解決策**:
- Federated Credentialsの `subject` が正しいことを確認
- GitHub環境名が正確に一致していることを確認
- Managed IdentityにRBACロールが割り当てられていることを確認

### ACRアクセスエラー

**エラー**: `Error: unauthorized: authentication required`

**解決策**:
```powershell
# 環境ごとのACRに対してAcrPullロールを割り当て
az role assignment create `
  --assignee $IDENTITY_PRINCIPAL_ID `
  --role "AcrPull" `
  --scope "/subscriptions/${SUBSCRIPTION_ID}/resourceGroups/<RESOURCE_GROUP>/providers/Microsoft.ContainerRegistry/registries/<ACR_NAME>"
```

### Container Appデプロイメントエラー

**解決策**:
- リソースグループ名が正しいことを確認
- Container App名が正しいことを確認
- Managed IdentityがContainer Appsにアクセスできることを確認

## 📚 参考資料

- [Azure Managed Identities](https://learn.microsoft.com/azure/active-directory/managed-identities-azure-resources/)
- [GitHub Actions OIDC with Azure](https://docs.github.com/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-azure)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)
- [GitHub Environments](https://docs.github.com/actions/deployment/targeting-different-environments/using-environments-for-deployment)

## ✅ チェックリスト

セットアップ完了前に以下を確認してください：

- [ ] User-Assigned Managed Identityが作成されている
- [ ] 各環境用のFederated Credentialsが設定されている
- [ ] 各環境のリソースグループにContributorロールが割り当てられている
- [ ] GitHubで3つの環境（dev, staging, production）が作成されている
- [ ] Staging/Productionに承認者が設定されている
- [ ] リポジトリレベルのSecretsが設定されている
- [ ] 各環境のVariablesが設定されている
- [ ] インフラストラクチャデプロイメントが成功している
- [ ] CI/CDパイプラインがエラーなく実行される

## 🎉 完了

セットアップが完了しました！`main`ブランチにコードをプッシュすると、CI/CDパイプラインが自動的に実行されます。
