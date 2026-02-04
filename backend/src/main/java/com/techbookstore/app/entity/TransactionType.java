package com.techbookstore.app.entity;

/**
 * Transaction types for inventory operations
 */
public enum TransactionType {
    RECEIVE,    // 入庫
    SELL,       // 販売
    ADJUST,     // 調整
    TRANSFER,   // 移動
    RESERVE     // 予約
}