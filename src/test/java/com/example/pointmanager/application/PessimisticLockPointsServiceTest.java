package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.repository.PointsHistoryRepository;
import com.example.pointmanager.repository.PointsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @BeforeEach
    public void before() {
        // 1번 유저의 잔고에 10000 포인트가 있는 상황을 가정
        pointsRepository.saveAndFlush(new Points(1, 10000));
        pointsHistoryRepository.saveAndFlush(PointsHistory.of(1, 10000, PointsHistory.TransactionType.CHARGE));
    }

//    @AfterEach
//    public void after() {
//        pointsRepository.deleteAll();
//        pointsHistoryRepository.deleteAll();
//    }
    @Test
    void 동시에_100명의_유저가_충전에_성공한다() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pessimisticLockPointsService.chargePoints(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Points points = pointsRepository.findPointsByUserId(1L).orElseThrow();
        assertEquals(10100, points.getAmount());
        assertEquals(10100, points.getAmount());
    }
}
