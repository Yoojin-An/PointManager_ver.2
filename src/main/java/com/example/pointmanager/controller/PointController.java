package com.example.pointmanager.controller;

import com.example.pointmanager.application.PointService;
import com.example.pointmanager.controller.dto.CommonResponse;
import com.example.pointmanager.controller.dto.PointHistoryResponse;
import com.example.pointmanager.controller.dto.PointRequest;
import com.example.pointmanager.controller.dto.PointResponse;
import com.example.pointmanager.domain.Point;
import com.example.pointmanager.domain.PointHistory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/point")
//@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    /**
     * 특정 유저의 포인트 조회
     */
    @GetMapping("{id}")
    public CommonResponse<PointResponse> findPoints(
            @PathVariable long id
    ) {
        try {
            Point point = pointService.findPoint(id);
            PointResponse pointResponse = new PointResponse(point);
            logger.info(String.format("id %d번 유저가 잔고(%dp)를 조회했습니다.", id, point.getAmount()));
            return CommonResponse.of(pointResponse);
        } catch (Exception e) {
            return CommonResponse.of(Optional.of(e.getMessage()));
        }
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역 조회
     */
    @GetMapping("{id}/history")
    public CommonResponse<List<PointHistoryResponse>> findHistory(
            @PathVariable long id
    ) {
        try {
            List<PointHistory> pointHistory = pointService.findPointHistory(id);
            List<PointHistoryResponse> pointHistoryResponse = pointHistory.stream()
                    .map(o -> new PointHistoryResponse(o))
                    .collect(Collectors.toList());
        logger.info(String.format("id %d번 유저가 포인트 충전/사용 내역을 조회했습니다. :: %s", id, pointHistory.toString()));
            return CommonResponse.of(pointHistoryResponse);
        } catch (Exception e) {
            return CommonResponse.of(Optional.of(e.getMessage()));
        }
    }

    /**
     * 특정 유저의 포인트 충전
     */
    @PatchMapping("{id}/charge")
    public CommonResponse<PointResponse> chargePoint(
            @PathVariable long id,
            @RequestBody PointRequest pointRequest) {
        try {
            Point point = pointService.chargePoint(id, pointRequest.getAmount(), true);
            PointResponse pointResponse = new PointResponse(point);
        logger.info(String.format("id %d번 유저가 %d포인트를 충전했습니다.", id, pointRequest.getAmount()));
            return CommonResponse.of(pointResponse);
        } catch (Exception e) {
            return CommonResponse.of(Optional.of(e.getMessage()));
        }
    }

    /**
     * 특정 유저의 포인트 사용
     */
    @PatchMapping("{id}/use")
    public CommonResponse<PointResponse> usePoint(
            @PathVariable long id,
            @RequestBody PointRequest pointRequest) {
        try {
            Point point = pointService.usePoint(id, pointRequest.getAmount(), true);
            PointResponse pointResponse = new PointResponse(point);
        logger.info(String.format("id %d번 유저가 %d포인트를 사용했습니다.", id, pointRequest.getAmount()));
            return CommonResponse.of(pointResponse);
        } catch (Exception e) {
            return CommonResponse.of(Optional.of(e.getMessage()));
        }
    }
}
