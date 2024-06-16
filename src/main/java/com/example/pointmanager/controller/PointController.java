package com.example.pointmanager.controller;

import com.example.pointmanager.controller.dto.PointsHistoryResponse;
import com.example.pointmanager.controller.dto.PointsRequest;
import com.example.pointmanager.controller.dto.PointsResponse;
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

import javax.swing.text.html.Option;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/points")
//@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;

    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(PointsController.class);

    /**
     * 유저 등록
     */
    @PostMapping("{id}/enroll")
    public ResponseEntity<PointsResponse> enrollUser(
            @PathVariable long id
    ) {
        Points points = pointsService.enrollUser(id);
        PointsResponse pointsResponse = new PointsResponse(points);
        logger.info(String.format("id %d번 유저를 등록했습니다.", id, points.getAmount()));
        return ResponseEntity.ok(pointsResponse);
    }

    /**
     * 특정 유저의 포인트 조회
     */
    @GetMapping("{id}")
    public ResponseEntity<PointsResponse> findPoints(
            @PathVariable long id
    ) {
        Points points = pointsService.findPoints(id);
        PointsResponse pointsResponse = new PointsResponse(points);
        logger.info(String.format("id %d번 유저가 잔고(%dp)를 조회했습니다.", id, points.getAmount()));
        return CommonResponse.of(pointsResponse);
        return ResponseEntity.ok(pointsResponse);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역 조회
     */
    @GetMapping("{id}/history")
    public ResponseEntity<List<PointsHistoryResponse>> findHistory(
            @PathVariable long id
    ) {
        List<PointsHistory> pointsHistory = pointsService.findPointsHistory(id);
        List<PointsHistoryResponse> pointsHistoryResponse = pointsHistory.stream()
                .map(o -> new PointsHistoryResponse(o))
                .collect(Collectors.toList());
//        logger.info(String.format("id %d번 유저가 포인트 충전/사용 내역을 조회했습니다. :: %s", id, pointsHistory.toString()));
        return ResponseEntity.ok(pointsHistoryResponse);
    }

    /**
     * 특정 유저의 포인트 충전
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<PointsResponse> chargePoints(
            @PathVariable long id,
            @RequestBody PointsRequest pointsRequest
    ) throws InterruptedException {
        Points points = pointsService.chargePoints(id, pointsRequest.getAmount());
        PointsResponse pointsResponse = new PointsResponse(points);
//        logger.info(String.format("id %d번 유저가 %d포인트를 충전했습니다.", id, pointsRequest.getAmount()));
        return ResponseEntity.ok(pointsResponse);
    }

    /**
     * 특정 유저의 포인트 사용
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<PointsResponse> usePoints(
            @PathVariable long id,
            @RequestBody PointsRequest pointsRequest
    ) throws InterruptedException {
        Points points = pointsService.usePoints(id, pointsRequest.getAmount());
        PointsResponse pointsResponse = new PointsResponse(points);
//        logger.info(String.format("id %d번 유저가 %d포인트를 사용했습니다.", id, pointsRequest.getAmount()));
        return ResponseEntity.ok(pointsResponse);
    }
}
