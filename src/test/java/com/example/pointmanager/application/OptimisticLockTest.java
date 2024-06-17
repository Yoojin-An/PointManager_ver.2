package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 낙관 락을 사용한 동시성 테스트
@SpringBootTest
//@SpringBootTest(classes = PointManagerApplication.class)
public class OptimisticLockPointFacadeTest {
    private final OptimisticLockPointFacade optimisticLockPointsFacade;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    @Autowired
    public OptimisticLockPointFacadeTest(OptimisticLockPointFacade optimisticLockPointsFacade,
                                         PointRepository pointRepository,
                                         PointHistoryRepository pointHistoryRepository) {
        this.optimisticLockPointsFacade = optimisticLockPointsFacade;
        this.pointRepository = pointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    @AfterEach
    public void after() {
        pointRepository.deleteAll();
        pointHistoryRepository.deleteAll();
    }

    @Test
    void 동시에_100번의_충전에_성공한다() throws InterruptedException {
        pointRepository.save(new Point(1, 0));
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockPointsFacade.chargePoints(1L, 1L);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Point point = pointRepository.findPointsByUserIdWithOptimisticLock(1L).orElseThrow();
        System.out.println(point.getVersion());

       assertEquals(100, point.getAmount());
    }
}
