package com.example.pointmanager.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointRequest {
    private long amount;

    public PointRequest(long amount) {
        this.amount = amount;
    }
}
