# TechBookStore（レガシースタック）— フルスタックサンプルアプリケーション

TechBookStore は、技術書専門書店のための **動作する** サンプルアプリケーションです：

- **バックエンド**: Spring Boot 3.5.0（Java 21）REST API
- **フロントエンド**: React 16 + Material-UI 4 シングルページアプリ
- **データ**: ローカル開発用 H2 インメモリデータベース（シード済み）、ステージング/本番用 PostgreSQL/Redis
- **I18n**: 日本語 / 英語 UI


## できること

- 書籍カタログの閲覧・検索、書籍詳細の表示・編集
- 在庫管理操作（入庫/販売/調整、アラート、回転率・分析）
- 顧客・注文管理
- ダッシュボード・レポート（売上/在庫/トレンド）
- UI 言語切替（日本語/英語）

## リポジトリ構成

- `backend/`: Spring Boot API
- `frontend/`: React UI
- `start-app.sh`: バックエンド＋フロントエンド同時起動（ログ・PIDは `/tmp` に出力）
- `status-app.sh`: プロセス・ヘルスチェック
- `stop-app.sh`: プロセス停止（`--clean-logs` オプションあり）
- `Dockerfile`: フロントエンドビルド＋バックエンド jar を 1 コンテナ化

## クイックスタート（ローカル開発）

### 前提条件

- JDK 21
- Node.js（Create React App 4 との互換性のため古いバージョン推奨。Docker ビルドは Node 12）
- npm

新しい Node.js で `ERR_OSSL_EVP_UNSUPPORTED` が出る場合は、`NODE_OPTIONS=--openssl-legacy-provider` を設定してください。

### オプションA: 付属スクリプトで起動

すべて起動（バックエンド＋フロントエンド）:

```bash
./start-app.sh
```

ステータス確認:

```bash
./status-app.sh
```

停止:

```bash
./stop-app.sh
```

補足:

- スクリプトは以下にログを出力します：
  - `/tmp/techbookstore_backend.log`
  - `/tmp/techbookstore_frontend.log`
- PIDは以下に保存されます：
  - `/tmp/techbookstore_backend.pid`
  - `/tmp/techbookstore_frontend.pid`
- `start-app.sh` は **`/workspace`** 配下での実行を想定しています。
  - Dev Container/コンテナ化ワークスペース以外の場合は、下記「オプションB（手動起動）」を利用してください。

### オプションB: 手動起動（どの環境でも動作）

バックエンド（Spring Boot）:

```bash
cd backend
./mvnw spring-boot:run
```

フロントエンド（React）:

```bash
cd frontend
npm install
npm start
```

### アクセスURL

- フロントエンド: http://localhost:3000
- バックエンドヘルス: http://localhost:8080/actuator/health
- Swagger UI（springdoc-openapi）: http://localhost:8080/swagger-ui/index.html
- OpenAPI仕様（JSON）: http://localhost:8080/v3/api-docs
- H2 コンソール（dev プロファイル）: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - ユーザー名: `sa`
  - パスワード: （空欄）

## アーキテクチャ概要

フロントエンド（React）は HTTP 経由でバックエンドにアクセスします：

- フロントエンド開発サーバーは `:3000` で稼働
- フロントエンドの `proxy` 設定で API を `http://localhost:8080` に転送（`frontend/package.json` 参照）
- バックエンドは `/api/v1/*` 配下で REST エンドポイントを公開

データ・キャッシュ：

- **dev プロファイル**（`SPRING_PROFILES_ACTIVE=dev`、デフォルト）：H2 インメモリDB＋オプションで Redis（デフォルトはホスト名 `redis`）
- **staging プロファイル**：ローカル PostgreSQL（`jdbc:postgresql://localhost:5432/techbookstore`）
- **prod プロファイル**：PostgreSQL/Redis の環境変数を期待（`backend/src/main/resources/application.yml` 参照）

## 設定

バックエンドのプロファイルは `backend/src/main/resources/application.yml` で定義されています。

主な環境変数：

- `SPRING_PROFILES_ACTIVE`: `dev`（デフォルト）、`staging`、`prod`
- `CORS_ALLOWED_ORIGINS`: デフォルト `http://localhost:3000`

Dev プロファイル（Redisは任意）：

- `REDIS_HOST`（デフォルト: `redis`）
- `REDIS_PORT`（デフォルト: `6379`）

ローカルで Redis が無い場合は、インメモリキャッシュを強制できます：

- `SPRING_CACHE_TYPE=simple`（`spring.cache.type=simple` と同等）

Staging プロファイル（PostgreSQL）：

- `DB_USERNAME`（デフォルト: `postgres`）
- `DB_PASSWORD`（デフォルト: `postgres`）

Prod プロファイルの変数は `AZURE_*` プレフィックス（Azure 用）です：

- `AZURE_POSTGRESQL_HOST`, `AZURE_POSTGRESQL_DATABASE`, `AZURE_POSTGRESQL_USERNAME`, `AZURE_POSTGRESQL_PASSWORD`
- `AZURE_REDIS_HOST`, `AZURE_REDIS_KEY`

## データモデルとシードデータ

デフォルトの dev プロファイルでは、DB は起動時に作成され、
`backend/src/main/resources/data.sql` からシードされます。

## API 概要

主な API グループ（すべて `/api/v1` 配下）：

- `/books`: 書籍カタログ操作
- `/inventory`: 在庫操作・分析
- `/reports`: ダッシュボード・レポート系エンドポイント

API ドキュメントは springdoc-openapi（OpenAPI 3）で `/swagger-ui/index.html` から参照できます。

## テスト

バックエンド：

```bash
cd backend
./mvnw test
```

フロントエンド：

```bash
cd frontend
npm test
```

CI スタイルのフロントエンドテスト：

```bash
cd frontend
npm run test:ci
```

I18n バリデーション補助：

- `validate-i18n.sh` がありますが、GitHub Actions ランナー用パスでハードコーディングされているため、ローカルでは調整が必要な場合があります。

## Docker

ルートの `Dockerfile` で以下をビルドします：

1) フロントエンドの静的ビルド
2) バックエンド jar
3) バックエンドをポート `8080` で起動

重要：

- コンテナはデフォルトで `SPRING_PROFILES_ACTIVE=prod` を設定します。
- `prod` では、バックエンドは PostgreSQL/Redis の環境変数を期待します（「設定」参照）。

## セキュリティ・本番利用時の注意

このリポジトリはサンプルアプリです。

- 一部の設定（セキュリティ含む）はデモ・ローカル用に簡略化されています。
- 本番テンプレートとして利用する場合は、必ず設定を見直し・強化してください。

## ライセンス

[MIT ライセンス](https://gist.githubusercontent.com/shinyay/56e54ee4c0e22db8211e05e70a63247e/raw/f3ac65a05ed8c8ea70b653875ccac0c6dbc10ba1/LICENSE) で公開しています。

## 元著者

- GitHub: <https://github.com/shinyay>
- Twitter: <https://twitter.com/yanashin18618>
- Mastodon: <https://mastodon.social/@yanashin>

## 変更者
- GitHub: <https://github.com/mahya8585>
- Twitter: <https://twitter.com/maaya8585>
