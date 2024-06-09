package com.example.pointmanager.controller;

import com.example.pointmanager.controller.dto.PointsRequest;
import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.application.PointsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;
    private static final Logger logger = LoggerFactory.getLogger(PointsController.class);

    /**
     * 특정 유저의 포인트 조회
     */
    @GetMapping("{id}")
    public ResponseEntity<PointsDto> findPoints(
            @PathVariable long id
    ) {
        Points points = pointsService.findPoints(id);
        PointsDto pointsDto = new PointsDto(points);
//        logger.info(String.format("id %d번 유저가 잔고(%dp)를 조회했습니다.", id, points.getAmount()));
        return ResponseEntity.ok(pointsDto);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역 조회
     */
    @GetMapping("{id}/history")
    public ResponseEntity<List<PointsHistoryDto>> findHistory(
            @PathVariable long id
    ) {
        List<PointsHistory> pointsHistory = pointsService.findPointsHistory(id);
        List<PointsHistoryDto> pointsHistoryDto = pointsHistory.stream()
                .map(o -> new PointsHistoryDto(o))
                .collect(Collectors.toList());
//        logger.info(String.format("id %d번 유저가 포인트 충전/사용 내역을 조회했습니다. :: %s", id, pointsHistory.toString()));
        return ResponseEntity.ok(pointsHistoryDto);
    }

    /**
     * 특정 유저의 포인트 충전
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<PointsDto> chargePoints(
            @PathVariable long id,
            @RequestBody PointsRequest pointsRequest
    ) throws InterruptedException {
        Points points = pointsService.chargePoints(id, pointsRequest.getAmount());
        PointsDto pointsDto = new PointsDto(points);
//        logger.info(String.format("id %d번 유저가 %d포인트를 충전했습니다.", id, pointsRequest.getAmount()));
        return ResponseEntity.ok(pointsDto);
    }

    /**
     * 특정 유저의 포인트 사용
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<PointsDto> usePoints(
            @PathVariable long id,
            @RequestBody PointsRequest pointsRequest
    ) throws InterruptedException {
        Points points = pointsService.usePoints(id, pointsRequest.getAmount());
        PointsDto pointsDto = new PointsDto(points);
//        logger.info(String.format("id %d번 유저가 %d포인트를 사용했습니다.", id, pointsRequest.getAmount()));
        return ResponseEntity.ok(pointsDto);
    }
    
    @Getter
    static class PointsDto {
        private long userId;
        private long amount;

        public PointsDto(Points points) {
            userId = points.getUserId();
            amount = points.getAmount();
        }

    }
    
    @Getter
    static class PointsHistoryDto {
        private long userId;
        private long amount;
        private PointsHistory.TransactionType transactionType;

        public PointsHistoryDto(PointsHistory pointsHistory) {
            userId = pointsHistory.getUserId();
            amount = pointsHistory.getAmount();
            transactionType = pointsHistory.getTransactionType();
        }
    }
}
