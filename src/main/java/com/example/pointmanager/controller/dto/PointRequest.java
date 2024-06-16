package com.example.pointmanager.controller.dto;

import lombok.Getter;

@Getter
public class PointRequest {
    private long amount;

    public PointRequest(long amount) {
        this.amount = amount;
    }
}
