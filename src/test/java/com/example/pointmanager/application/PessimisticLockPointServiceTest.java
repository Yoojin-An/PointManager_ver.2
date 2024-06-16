package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.repository.PointsHistoryRepository;
import com.example.pointmanager.repository.PointsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 비관 락을 사용한 동시성 테스트
@SpringBootTest
public class PessimisticLockPointsServiceTest {

    @Autowired
    private PessimisticLockPointsService pessimisticLockPointsService;

    @Autowired
    private PointsRepository pointsRepository;

    @Autowired
    private PointsHistoryRepository pointsHistoryRepository;

    @AfterEach
    public void after() {
        pointsRepository.deleteAll();
        pointsHistoryRepository.deleteAll();
    }
    @Test
    void 동시에_100번의_충전에_성공한다() throws InterruptedException {
        pointsRepository.save(new Points(1, 0));
        int threadCount = 100;
        int chargeAmount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        var expectedAmount = threadCount * chargeAmount

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticLockPointsService.chargePoints(1L, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Points points = pointsRepository.findPointsByUserId(1L).orElseThrow();
        assertEquals(expectedAmount, points.getAmount());
    }
}
