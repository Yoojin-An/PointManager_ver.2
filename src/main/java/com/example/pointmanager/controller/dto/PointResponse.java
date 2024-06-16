package com.example.pointmanager.controller.dto;

import com.example.pointmanager.domain.Point;
import lombok.Getter;

 @Getter
 public class PointResponse {
    private long id;
    private long amount;

    public PointResponse(Point point) {
        id = point.getId();
        amount = point.getAmount();
    }
}

