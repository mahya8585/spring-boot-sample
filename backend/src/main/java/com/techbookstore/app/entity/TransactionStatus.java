package com.techbookstore.app.entity;

/**
 * Transaction status for approval workflow
 */
public enum TransactionStatus {
    PENDING,    // 承認待ち
    APPROVED,   // 承認済み
    REJECTED,   // 却下
    CANCELLED   // 取消
}