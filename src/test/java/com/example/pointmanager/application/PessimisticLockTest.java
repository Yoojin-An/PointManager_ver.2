package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 비관 락을 사용한 동시성 테스트
@SpringBootTest
public class PessimisticLockTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @AfterEach
    public void after() {
        pointRepository.deleteAll();
        pointHistoryRepository.deleteAll();
    }
    @Test
    void 동시에_100번의_충전에_성공한다() throws InterruptedException {
        pointRepository.save(new Point(1, 0));
        int threadCount = 100;
        int chargeAmount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        var expectedAmount = threadCount * chargeAmount

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(1L, chargeAmount, false);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Point point = pointRepository.findPointsByUserId(1L).orElseThrow();
        assertEquals(expectedAmount, point.getAmount());
    }
}
