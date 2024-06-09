package com.example.pointmanager.controller;

import com.example.pointmanager.controller.dto.PointsRequest;
import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.application.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointsService pointsService;
    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    /**
     * 특정 유저의 포인트 조회
     */
    @GetMapping("{id}")
    public ResponseEntity<Points> findPoints(
            @PathVariable long id
    ) {
        Points points = pointsService.findPoints(id);
//        logger.info(String.format("id %d번 유저가 잔고(%dp)를 조회했습니다.", id, points.getAmount()));
        return ResponseEntity.ok(points);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역 조회
     */
    @GetMapping("{id}/history")
    public ResponseEntity<List<PointsHistory>> findHistory(
            @PathVariable long id
    ) {
        List<PointsHistory> pointsHistory = pointsService.findPointsHistory(id);
//        logger.info(String.format("id %d번 유저가 포인트 충전/사용 내역을 조회했습니다. :: %s", id, pointsHistory.toString()));
        return ResponseEntity.ok(pointsHistory);
    }

    /**
     * 특정 유저의 포인트 충전
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<Points> chargePoints(
            @PathVariable long id,
            @RequestBody PointsRequest pointsRequest
    ) throws InterruptedException {
        Points points = pointsService.chargePoints(id, pointsRequest.getAmount());
//        logger.info(String.format("id %d번 유저가 %d포인트를 충전했습니다.", id, pointsRequest.getAmount()));
        return ResponseEntity.ok(points);
    }

    /**
     * 특정 유저의 포인트 사용
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<Points> usePoints(
            @PathVariable long id,
            @RequestBody PointsRequest pointsRequest
    ) throws InterruptedException {
        Points points = pointsService.usePoints(id, pointsRequest.getAmount());
//        logger.info(String.format("id %d번 유저가 %d포인트를 사용했습니다.", id, pointsRequest.getAmount()));
        return ResponseEntity.ok(points);
    }

//    public PointsDto(Points points) {
//
//    }
}