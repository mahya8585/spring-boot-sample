#!/bin/bash

# TechBookStore - アプリケーション停止スクリプト
# Backend と Frontend を停止します

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

# プロセスID格納用ファイル
BACKEND_PID_FILE="/tmp/techbookstore_backend.pid"
FRONTEND_PID_FILE="/tmp/techbookstore_frontend.pid"

log_info "TechBookStore アプリケーションを停止中..."

STOPPED_PROCESSES=0

# Backend プロセスを停止
if [ -f "$BACKEND_PID_FILE" ]; then
    BACKEND_PID=$(cat "$BACKEND_PID_FILE")
    if kill -0 "$BACKEND_PID" 2>/dev/null; then
        log_info "Backend を停止中 (PID: $BACKEND_PID)..."
        kill -TERM "$BACKEND_PID" 2>/dev/null
        
        # Graceful shutdown を待つ
        for i in {1..10}; do
            if ! kill -0 "$BACKEND_PID" 2>/dev/null; then
                log_info "Backend が正常に停止しました"
                STOPPED_PROCESSES=$((STOPPED_PROCESSES + 1))
                break
            fi
            sleep 1
        done
        
        # まだ動いている場合は強制終了
        if kill -0 "$BACKEND_PID" 2>/dev/null; then
            log_warn "Backend を強制終了中..."
            kill -KILL "$BACKEND_PID" 2>/dev/null
            STOPPED_PROCESSES=$((STOPPED_PROCESSES + 1))
            log_info "Backend が強制終了されました"
        fi
    else
        log_warn "Backend プロセス (PID: $BACKEND_PID) は既に停止しています"
    fi
    rm -f "$BACKEND_PID_FILE"
else
    log_warn "Backend PIDファイルが見つかりません"
fi

# Frontend プロセスを停止
if [ -f "$FRONTEND_PID_FILE" ]; then
    FRONTEND_PID=$(cat "$FRONTEND_PID_FILE")
    if kill -0 "$FRONTEND_PID" 2>/dev/null; then
        log_info "Frontend を停止中 (PID: $FRONTEND_PID)..."
        kill -TERM "$FRONTEND_PID" 2>/dev/null
        
        # プロセスツリー全体を停止
        pkill -P "$FRONTEND_PID" 2>/dev/null || true
        
        # Graceful shutdown を待つ
        for i in {1..10}; do
            if ! kill -0 "$FRONTEND_PID" 2>/dev/null; then
                log_info "Frontend が正常に停止しました"
                STOPPED_PROCESSES=$((STOPPED_PROCESSES + 1))
                break
            fi
            sleep 1
        done
        
        # まだ動いている場合は強制終了
        if kill -0 "$FRONTEND_PID" 2>/dev/null; then
            log_warn "Frontend を強制終了中..."
            kill -KILL "$FRONTEND_PID" 2>/dev/null
            STOPPED_PROCESSES=$((STOPPED_PROCESSES + 1))
            log_info "Frontend が強制終了されました"
        fi
    else
        log_warn "Frontend プロセス (PID: $FRONTEND_PID) は既に停止しています"
    fi
    rm -f "$FRONTEND_PID_FILE"
else
    log_warn "Frontend PIDファイルが見つかりません"
fi

# Node.js プロセスのクリーンアップ
log_info "残留する Node.js プロセスをクリーンアップ中..."
REACT_PROCESSES=$(pgrep -f "react-scripts start" 2>/dev/null || true)
if [ -n "$REACT_PROCESSES" ]; then
    echo "$REACT_PROCESSES" | xargs kill -TERM 2>/dev/null || true
    sleep 2
    echo "$REACT_PROCESSES" | xargs kill -KILL 2>/dev/null || true
    log_info "React プロセスをクリーンアップしました"
fi

NODE_PROCESSES=$(pgrep -f "node.*react-scripts" 2>/dev/null || true)
if [ -n "$NODE_PROCESSES" ]; then
    echo "$NODE_PROCESSES" | xargs kill -TERM 2>/dev/null || true
    sleep 2
    echo "$NODE_PROCESSES" | xargs kill -KILL 2>/dev/null || true
    log_info "Node.js プロセスをクリーンアップしました"
fi

# Maven プロセスのクリーンアップ
MAVEN_PROCESSES=$(pgrep -f "mvnw spring-boot:run" 2>/dev/null || true)
if [ -n "$MAVEN_PROCESSES" ]; then
    echo "$MAVEN_PROCESSES" | xargs kill -TERM 2>/dev/null || true
    sleep 2
    echo "$MAVEN_PROCESSES" | xargs kill -KILL 2>/dev/null || true
    log_info "Maven プロセスをクリーンアップしました"
fi

# ポートの使用状況を確認
BACKEND_PORT_CHECK=$(netstat -tlnp 2>/dev/null | grep ":8080" || true)
FRONTEND_PORT_CHECK=$(netstat -tlnp 2>/dev/null | grep ":3000" || true)

if [ -n "$BACKEND_PORT_CHECK" ]; then
    log_warn "ポート 8080 がまだ使用されています:"
    echo "$BACKEND_PORT_CHECK"
fi

if [ -n "$FRONTEND_PORT_CHECK" ]; then
    log_warn "ポート 3000 がまだ使用されています:"
    echo "$FRONTEND_PORT_CHECK"
fi

# 完了メッセージ
log_info "=========================================="
if [ $STOPPED_PROCESSES -gt 0 ]; then
    log_info "✅ TechBookStore アプリケーションが停止されました"
    log_info "   停止したプロセス数: $STOPPED_PROCESSES"
else
    log_info "ℹ️  停止対象のプロセスが見つかりませんでした"
fi
log_info "=========================================="

# ログファイルのクリーンアップ（オプション）
if [ "$1" = "--clean-logs" ]; then
    log_info "ログファイルをクリーンアップ中..."
    rm -f "/tmp/techbookstore_backend.log"
    rm -f "/tmp/techbookstore_frontend.log"
    log_info "ログファイルが削除されました"
fi
