package com.example.pointmanager.controller.dto;

import com.example.pointmanager.domain.PointHistory;
import lombok.Getter;

@Getter
public class PointHistoryResponse {
    private long userId;
    private long amount;
    private PointHistory.TransactionType transactionType;

    public PointHistoryResponse(PointHistory pointHistory) {
        userId = pointHistory.getUserId();
        amount = pointHistory.getAmount();
        transactionType = pointHistory.getTransactionType();
    }
}