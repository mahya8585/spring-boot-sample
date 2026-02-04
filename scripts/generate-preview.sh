#!/bin/bash

# Simple site generator for local testing
# This script creates a basic HTML preview of the codelabs

CODELABS_DIR="codelabs"
DOCS_DIR="docs"
OUTPUT_DIR="$DOCS_DIR/preview"

echo "🚀 Generating local preview site..."

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Generate a simple index
cat > "$OUTPUT_DIR/index.html" << 'EOF'
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Codelabs Preview - Legacy Spring Boot Workshop</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 2rem; background: #f5f5f5; }
        .container { max-width: 800px; margin: 0 auto; background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        h1 { color: #1976d2; border-bottom: 2px solid #1976d2; padding-bottom: 0.5rem; }
        .codelab { background: #f8f9fa; padding: 1rem; margin: 1rem 0; border-radius: 4px; border-left: 4px solid #1976d2; }
        .status { display: inline-block; background: #4caf50; color: white; padding: 0.2rem 0.5rem; border-radius: 12px; font-size: 0.8rem; }
        .duration { color: #666; font-size: 0.9rem; }
        pre { background: #263238; color: #eeff41; padding: 1rem; border-radius: 4px; overflow-x: auto; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 Legacy Spring Boot Modernization Workshop</h1>
        <p>Google Codelabs形式で学ぶ実践的モダナイゼーション手法</p>
        
        <div class="codelab">
            <h2>第1章: 現状分析フェーズ</h2>
            <p>GitHub Copilotを活用したレガシーアプリケーションの現状分析手法を習得する実践ワークショップ</p>
            <div class="duration">⏱️ 4時間40分 (280分)</div>
            <div class="status">Ready</div>
            <p><strong>注意:</strong> このプレビューは開発用です。実際のcodelab体験には<code>claat</code>でのビルドが必要です。</p>
        </div>
        
        <h3>🛠️ ローカル開発</h3>
        <p>完全なcodelab体験のために:</p>
        <pre>
# claat のインストール
go install github.com/googlecodelabs/tools/claat@latest

# ビルド実行
cd codelabs
claat export chapter1-current-state-analysis.md

# 結果の確認
open chapter1-current-state-analysis/index.html
        </pre>
        
        <h3>📋 ファイル情報</h3>
        <ul id="files"></ul>
        
        <script>
            // Simple file listing
            const files = [
                'codelabs/chapter1-current-state-analysis.md - Main codelab content',
                'docs/README.md - Documentation for site structure',
                '.github/workflows/codelabs.yml - CI/CD automation',
                'scripts/validate-codelab.sh - Format validation'
            ];
            
            const filesList = document.getElementById('files');
            files.forEach(file => {
                const li = document.createElement('li');
                li.textContent = file;
                filesList.appendChild(li);
            });
        </script>
    </div>
</body>
</html>
EOF

echo "✅ Generated preview at: $OUTPUT_DIR/index.html"
echo "🌐 Open in browser: file://$(pwd)/$OUTPUT_DIR/index.html"

# List created files
echo
echo "📁 Generated files:"
find "$OUTPUT_DIR" -type f -name "*.html" | sort