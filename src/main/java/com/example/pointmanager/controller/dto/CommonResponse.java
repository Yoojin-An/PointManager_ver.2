package com.example.pointmanager.controller.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Data
public class CommonResponse<T> {

    private Optional<T> data = Optional.empty();
    private Optional<String> errorMessage = Optional.empty();

    public CommonResponse(Optional<T> data, Optional<String> errorMessage) {
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> CommonResponse<T> of(T data) {
        return new CommonResponse<>(Optional.of(data), Optional.empty());
    }

    public static <T> CommonResponse<T> of(Optional<String> errorMessage) {
        return new CommonResponse<>(Optional.empty(), errorMessage);
    }
}
