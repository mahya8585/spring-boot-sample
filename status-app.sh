#!/bin/bash

# TechBookStore - アプリケーション状態確認スクリプト
# Backend と Frontend の状態を表示します

set -e

# 色付きログ出力用の関数
log_info() {
    echo -e "\033[32m[INFO]\033[0m $1"
}

log_warn() {
    echo -e "\033[33m[WARN]\033[0m $1"
}

log_error() {
    echo -e "\033[31m[ERROR]\033[0m $1"
}

log_success() {
    echo -e "\033[92m[SUCCESS]\033[0m $1"
}

# プロセスID格納用ファイル
BACKEND_PID_FILE="/tmp/techbookstore_backend.pid"
FRONTEND_PID_FILE="/tmp/techbookstore_frontend.pid"

# ヘルス状態チェック関数
check_backend_health() {
    if curl -s "http://localhost:8080/actuator/health" > /dev/null 2>&1; then
        echo "✅ ヘルシー"
    else
        echo "❌ 応答なし"
    fi
}

check_frontend_health() {
    if curl -s "http://localhost:3000" > /dev/null 2>&1; then
        echo "✅ ヘルシー"
    else
        echo "❌ 応答なし"
    fi
}

# メモリ使用量を取得
get_memory_usage() {
    local pid=$1
    if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
        ps -o pid,ppid,rss,vsz,pcpu,comm -p "$pid" 2>/dev/null | tail -n 1 | awk '{printf "RSS: %s KB, VSZ: %s KB, CPU: %s%%", $3, $4, $5}'
    else
        echo "N/A"
    fi
}

# ログファイルのサイズを取得
get_log_size() {
    local log_file=$1
    if [ -f "$log_file" ]; then
        du -h "$log_file" | cut -f1
    else
        echo "N/A"
    fi
}

log_info "===================================================================================="
log_info "🔍 TechBookStore アプリケーション状態確認"
log_info "===================================================================================="

# Backend 状態確認
log_info "📦 Backend (Spring Boot) 状態:"
if [ -f "$BACKEND_PID_FILE" ]; then
    BACKEND_PID=$(cat "$BACKEND_PID_FILE")
    if kill -0 "$BACKEND_PID" 2>/dev/null; then
        log_success "   ✅ 実行中 (PID: $BACKEND_PID)"
        echo "   📊 メモリ使用量: $(get_memory_usage $BACKEND_PID)"
        echo "   🌐 ヘルス状態: $(check_backend_health)"
        echo "   📄 ログサイズ: $(get_log_size /tmp/techbookstore_backend.log)"
        
        # Backend エンドポイント詳細
        echo "   🔗 エンドポイント:"
        echo "      ├─ API:          http://localhost:8080/api/v1"
        echo "      ├─ Swagger UI:   http://localhost:8080/swagger-ui.html"
        echo "      ├─ H2 Console:   http://localhost:8080/h2-console"
        echo "      └─ Health:       http://localhost:8080/actuator/health"
    else
        log_error "   ❌ プロセス停止中 (PID: $BACKEND_PID は無効)"
    fi
else
    log_warn "   ⚠️  PIDファイルが見つかりません"
fi

echo ""

# Frontend 状態確認
log_info "🎨 Frontend (React) 状態:"
if [ -f "$FRONTEND_PID_FILE" ]; then
    FRONTEND_PID=$(cat "$FRONTEND_PID_FILE")
    if kill -0 "$FRONTEND_PID" 2>/dev/null; then
        log_success "   ✅ 実行中 (PID: $FRONTEND_PID)"
        echo "   📊 メモリ使用量: $(get_memory_usage $FRONTEND_PID)"
        echo "   🌐 ヘルス状態: $(check_frontend_health)"
        echo "   📄 ログサイズ: $(get_log_size /tmp/techbookstore_frontend.log)"
        
        # Frontend エンドポイント詳細
        echo "   🔗 エンドポイント:"
        echo "      └─ アプリケーション: http://localhost:3000"
    else
        log_error "   ❌ プロセス停止中 (PID: $FRONTEND_PID は無効)"
    fi
else
    log_warn "   ⚠️  PIDファイルが見つかりません"
fi

echo ""

# ポート使用状況
log_info "🔌 ポート使用状況:"
BACKEND_PORT=$(netstat -tlnp 2>/dev/null | grep ":8080" || echo "未使用")
FRONTEND_PORT=$(netstat -tlnp 2>/dev/null | grep ":3000" || echo "未使用")

if [ "$BACKEND_PORT" = "未使用" ]; then
    log_warn "   ⚠️  ポート 8080: 未使用"
else
    log_success "   ✅ ポート 8080: 使用中"
fi

if [ "$FRONTEND_PORT" = "未使用" ]; then
    log_warn "   ⚠️  ポート 3000: 未使用"
else
    log_success "   ✅ ポート 3000: 使用中"
fi

echo ""

# システムリソース情報
log_info "💻 システムリソース:"
echo "   📊 CPU使用率: $(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | sed 's/%us,//' || echo "N/A")"
echo "   🧠 メモリ使用率: $(free | grep Mem | awk '{printf "%.1f%%", $3/$2 * 100.0}' || echo "N/A")"
echo "   💾 ディスク使用率: $(df -h / | awk 'NR==2{print $5}' || echo "N/A")"

echo ""

# ログファイル情報
log_info "📜 ログファイル:"
if [ -f "/tmp/techbookstore_backend.log" ]; then
    BACKEND_LOG_LINES=$(wc -l < "/tmp/techbookstore_backend.log" 2>/dev/null || echo "0")
    echo "   📖 Backend:  /tmp/techbookstore_backend.log (${BACKEND_LOG_LINES} 行)"
else
    echo "   📖 Backend:  ログファイルなし"
fi

if [ -f "/tmp/techbookstore_frontend.log" ]; then
    FRONTEND_LOG_LINES=$(wc -l < "/tmp/techbookstore_frontend.log" 2>/dev/null || echo "0")
    echo "   📱 Frontend: /tmp/techbookstore_frontend.log (${FRONTEND_LOG_LINES} 行)"
else
    echo "   📱 Frontend: ログファイルなし"
fi

echo ""

# 関連するプロセス一覧
log_info "🔄 関連プロセス:"
echo "   Java プロセス:"
ps aux | grep -E "(java|mvnw)" | grep -v grep | while read line; do
    echo "      $line"
done || echo "      なし"

echo "   Node.js プロセス:"
ps aux | grep -E "(node|npm)" | grep -v grep | while read line; do
    echo "      $line"
done || echo "      なし"

log_info "===================================================================================="

# 簡易的な全体ステータス
OVERALL_STATUS="🟢 正常"
if [ ! -f "$BACKEND_PID_FILE" ] || ! kill -0 "$(cat $BACKEND_PID_FILE)" 2>/dev/null; then
    OVERALL_STATUS="🔴 Backend停止中"
elif [ ! -f "$FRONTEND_PID_FILE" ] || ! kill -0 "$(cat $FRONTEND_PID_FILE)" 2>/dev/null; then
    OVERALL_STATUS="🟡 Frontend停止中"
elif ! curl -s "http://localhost:8080/actuator/health" > /dev/null 2>&1; then
    OVERALL_STATUS="🟡 Backend応答なし"
elif ! curl -s "http://localhost:3000" > /dev/null 2>&1; then
    OVERALL_STATUS="🟡 Frontend応答なし"
fi

log_info "📊 全体ステータス: $OVERALL_STATUS"
log_info "===================================================================================="
