<#
.SYNOPSIS
    GitHub Actions パイプライン用のAzure認証を自動設定するスクリプト

.DESCRIPTION
    このスクリプトは、GitHub ActionsからAzureにデプロイするための
    User-Assigned Managed IdentityとFederated Credentialsを自動的に作成します。

.PARAMETER SubscriptionId
    使用するAzureサブスクリプションID

.PARAMETER GitHubOrg
    GitHubの組織名またはユーザー名

.PARAMETER GitHubRepo
    GitHubリポジトリ名

.PARAMETER Location
    Azureリソースのデプロイ先リージョン（デフォルト: eastus2）

.EXAMPLE
    .\setup-azure-auth-for-pipeline.ps1 -SubscriptionId "your-subscription-id" -GitHubOrg "shinyay" -GitHubRepo "legacy-spring-boot-sample"
#>

param(
    [Parameter(Mandatory=$false)]
    [string]$SubscriptionId,

    [Parameter(Mandatory=$false)]
    [string]$GitHubOrg,

    [Parameter(Mandatory=$false)]
    [string]$GitHubRepo,

    [Parameter(Mandatory=$false)]
    [string]$Location = "eastus2"
)

# エラー時に停止
$ErrorActionPreference = "Stop"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Azure Authentication Setup for GitHub Actions" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Subscription IDの取得または確認
if (-not $SubscriptionId) {
    Write-Host "現在のAzureサブスクリプションを確認中..." -ForegroundColor Yellow
    $currentSub = az account show --query id -o tsv
    if ($LASTEXITCODE -eq 0) {
        Write-Host "現在のサブスクリプション: $currentSub" -ForegroundColor Green
        $confirm = Read-Host "このサブスクリプションを使用しますか? (Y/n)"
        if ($confirm -eq "" -or $confirm -eq "Y" -or $confirm -eq "y") {
            $SubscriptionId = $currentSub
        } else {
            $SubscriptionId = Read-Host "使用するサブスクリプションIDを入力してください"
        }
    } else {
        Write-Host "Azureにログインしていません。ログインしてください。" -ForegroundColor Red
        az login
        $SubscriptionId = Read-Host "使用するサブスクリプションIDを入力してください"
    }
}

# GitHub情報の取得
if (-not $GitHubOrg) {
    $GitHubOrg = Read-Host "GitHubの組織名またはユーザー名を入力してください (例: shinyay)"
}

if (-not $GitHubRepo) {
    $GitHubRepo = Read-Host "GitHubリポジトリ名を入力してください (例: legacy-spring-boot-sample)"
}

# 設定値の表示
Write-Host ""
Write-Host "設定内容:" -ForegroundColor Cyan
Write-Host "  Subscription ID: $SubscriptionId" -ForegroundColor White
Write-Host "  GitHub Organization/User: $GitHubOrg" -ForegroundColor White
Write-Host "  GitHub Repository: $GitHubRepo" -ForegroundColor White
Write-Host "  Azure Location: $Location" -ForegroundColor White
Write-Host ""

$confirm = Read-Host "この内容で設定を開始しますか? (Y/n)"
if ($confirm -ne "" -and $confirm -ne "Y" -and $confirm -ne "y") {
    Write-Host "処理を中止しました。" -ForegroundColor Yellow
    exit 0
}

# サブスクリプションの設定
Write-Host ""
Write-Host "[1/7] Azureサブスクリプションを設定中..." -ForegroundColor Yellow
az account set --subscription $SubscriptionId
if ($LASTEXITCODE -ne 0) {
    Write-Host "エラー: サブスクリプションの設定に失敗しました。" -ForegroundColor Red
    exit 1
}
Write-Host "✓ サブスクリプション設定完了" -ForegroundColor Green

# 変数の定義
$RESOURCE_GROUP_IDENTITY = "rg-pipeline-identity"
$IDENTITY_NAME = "id-github-actions-pipeline"
$TENANT_ID = az account show --query tenantId -o tsv

# リソースグループの作成
Write-Host ""
Write-Host "[2/7] Managed Identity用リソースグループを作成中..." -ForegroundColor Yellow
$rgExists = az group exists --name $RESOURCE_GROUP_IDENTITY
if ($rgExists -eq "false") {
    az group create --name $RESOURCE_GROUP_IDENTITY --location $Location --tags purpose="CI/CD Pipeline Identity"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "エラー: リソースグループの作成に失敗しました。" -ForegroundColor Red
        exit 1
    }
    Write-Host "✓ リソースグループ作成完了: $RESOURCE_GROUP_IDENTITY" -ForegroundColor Green
} else {
    Write-Host "✓ リソースグループは既に存在します: $RESOURCE_GROUP_IDENTITY" -ForegroundColor Green
}

# User-Assigned Managed Identityの作成
Write-Host ""
Write-Host "[3/7] User-Assigned Managed Identityを作成中..." -ForegroundColor Yellow
$identityExists = az identity show --name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY 2>$null
if (-not $identityExists) {
    az identity create --name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY --location $Location
    if ($LASTEXITCODE -ne 0) {
        Write-Host "エラー: Managed Identityの作成に失敗しました。" -ForegroundColor Red
        exit 1
    }
    Write-Host "✓ Managed Identity作成完了: $IDENTITY_NAME" -ForegroundColor Green
} else {
    Write-Host "✓ Managed Identityは既に存在します: $IDENTITY_NAME" -ForegroundColor Green
}

# Identity情報の取得
$IDENTITY_CLIENT_ID = az identity show --name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY --query clientId -o tsv
$IDENTITY_PRINCIPAL_ID = az identity show --name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY --query principalId -o tsv

Write-Host ""
Write-Host "Identity情報:" -ForegroundColor Cyan
Write-Host "  Client ID: $IDENTITY_CLIENT_ID" -ForegroundColor White
Write-Host "  Principal ID: $IDENTITY_PRINCIPAL_ID" -ForegroundColor White

# Federated Credentialsの作成
Write-Host ""
Write-Host "[4/7] Federated Credentialsを設定中..." -ForegroundColor Yellow

$environments = @("dev", "staging", "production")
foreach ($env in $environments) {
    $credName = "github-actions-$env"
    $subject = "repo:${GitHubOrg}/${GitHubRepo}:environment:$env"
    
    Write-Host "  - $env 環境用の設定中..." -ForegroundColor White
    
    $credExists = az identity federated-credential show --name $credName --identity-name $IDENTITY_NAME --resource-group $RESOURCE_GROUP_IDENTITY 2>$null
    if (-not $credExists) {
        az identity federated-credential create `
            --name $credName `
            --identity-name $IDENTITY_NAME `
            --resource-group $RESOURCE_GROUP_IDENTITY `
            --issuer "https://token.actions.githubusercontent.com" `
            --subject $subject `
            --audiences "api://AzureADTokenExchange"
        
        if ($LASTEXITCODE -ne 0) {
            Write-Host "    警告: $env 環境のFederated Credential作成に失敗しました。" -ForegroundColor Red
        } else {
            Write-Host "    ✓ $env 環境の設定完了" -ForegroundColor Green
        }
    } else {
        Write-Host "    ✓ $env 環境の設定は既に存在します" -ForegroundColor Green
    }
}

# 各環境のリソースグループに対するRBACロールの割り当て
Write-Host ""
Write-Host "[5/7] RBACロールを割り当て中..." -ForegroundColor Yellow

$resourceGroups = @(
    @{Name="rg-techbookstore-dev"; Env="dev"},
    @{Name="rg-techbookstore-staging"; Env="staging"},
    @{Name="rg-techbookstore-production"; Env="production"}
)

foreach ($rg in $resourceGroups) {
    Write-Host "  - $($rg.Env) 環境: $($rg.Name)" -ForegroundColor White
    
    # リソースグループが存在するか確認
    $rgExists = az group exists --name $rg.Name
    if ($rgExists -eq "false") {
        Write-Host "    ! リソースグループが存在しません。作成してください。" -ForegroundColor Yellow
        continue
    }
    
    # Contributorロールの割り当て
    $scope = "/subscriptions/${SubscriptionId}/resourceGroups/$($rg.Name)"
    az role assignment create `
        --assignee $IDENTITY_PRINCIPAL_ID `
        --role "Contributor" `
        --scope $scope 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "    ✓ Contributorロール割り当て完了" -ForegroundColor Green
    } else {
        Write-Host "    ! ロール割り当ては既に存在するか、エラーが発生しました" -ForegroundColor Yellow
    }
}

# GitHub Secretsの設定指示
Write-Host ""
Write-Host "[6/7] GitHub Secretsの設定が必要です" -ForegroundColor Yellow
Write-Host ""
Write-Host "以下のコマンドを実行してGitHub Secretsを設定してください:" -ForegroundColor Cyan
Write-Host ""
Write-Host "gh secret set AZURE_CLIENT_ID --body `"$IDENTITY_CLIENT_ID`"" -ForegroundColor White
Write-Host "gh secret set AZURE_TENANT_ID --body `"$TENANT_ID`"" -ForegroundColor White
Write-Host "gh secret set AZURE_SUBSCRIPTION_ID --body `"$SubscriptionId`"" -ForegroundColor White
Write-Host "gh secret set POSTGRESQL_ADMIN_USERNAME --body `"techbookadmin`"" -ForegroundColor White
Write-Host 'gh secret set POSTGRESQL_ADMIN_PASSWORD --body "<YOUR_SECURE_PASSWORD>"' -ForegroundColor White
Write-Host ""

# GitHub環境変数の設定指示
Write-Host "[7/7] GitHub環境変数の設定が必要です" -ForegroundColor Yellow
Write-Host ""
Write-Host "インフラストラクチャデプロイメント前に、以下の環境変数を設定してください:" -ForegroundColor Cyan
Write-Host ""

foreach ($env in $environments) {
    $rgName = "rg-techbookstore-$env"
    Write-Host "--- $env 環境 ---" -ForegroundColor Yellow
    Write-Host "gh variable set AZURE_RESOURCE_GROUP --body `"$rgName`" --env $env" -ForegroundColor White
    Write-Host "gh variable set AZURE_LOCATION --body `"$Location`" --env $env" -ForegroundColor White
    Write-Host ""
}

Write-Host "インフラストラクチャデプロイメント後に、出力から取得した値で以下を設定してください:" -ForegroundColor Cyan
Write-Host ""
Write-Host 'gh variable set AZURE_CONTAINER_REGISTRY_NAME --body "<ACR_NAME>" --env dev' -ForegroundColor White
Write-Host 'gh variable set BACKEND_APP_NAME --body "<BACKEND_APP_NAME>" --env dev' -ForegroundColor White
Write-Host 'gh variable set FRONTEND_APP_NAME --body "<FRONTEND_APP_NAME>" --env dev' -ForegroundColor White
Write-Host "# staging と production も同様に設定" -ForegroundColor Gray
Write-Host ""

# 完了メッセージ
Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "✓ Azure認証セットアップ完了!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "次のステップ:" -ForegroundColor Yellow
Write-Host "1. 上記のghコマンドでGitHub Secretsを設定" -ForegroundColor White
Write-Host "2. 各環境のGitHub環境変数を設定" -ForegroundColor White
Write-Host "3. Infrastructure Deploymentワークフローを実行" -ForegroundColor White
Write-Host "4. デプロイメント出力から残りの環境変数を設定" -ForegroundColor White
Write-Host "5. CD パイプラインを実行" -ForegroundColor White
Write-Host ""
Write-Host "詳細は .azure/pipeline-setup.md を参照してください。" -ForegroundColor Cyan
