package com.example.pointmanager.repository;

import com.example.pointmanager.domain.PointHistory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PointHistoryRepositoryTest {

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @AfterEach
    void after() {
        pointHistoryRepository.deleteAllInBatch();
    }

    @Test
    void PointHistoryRepository가_제대로_연결되었다() {
        // given
        long userId = 1L;
        long amount = 10000L;
        PointHistory pointHistory = PointHistory.of(userId, amount, PointHistory.TransactionType.USE);

        // when
        PointHistory result = pointHistoryRepository.save(pointHistory);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAmount()).isEqualTo(amount);
    }
    @Test
    void findPointHistoryByUserId_메서드로_pointHistory_데이터를_찾아올_수_있다() {
        // given
        long userId = 1L;
        long amount = 10000L;
        PointHistory pointHistory = PointHistory.of(userId, amount, PointHistory.TransactionType.USE);
        pointHistoryRepository.save(pointHistory);

        // when
        List<PointHistory> result = pointHistoryRepository.findAllPointHistoriesByUserId(userId);

        // then
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getAmount()).isEqualTo(amount);
    }

    @Test
    void findAllPointHistoriesByUserId_메서드는_pointHistory_데이터가_없으면_빈_리스트를_내려준다() {
        // given
        long userId = 1L;

        // when
        List<PointHistory> result = pointHistoryRepository.findAllPointHistoriesByUserId(userId);

        // then
        assertThat(result).isEqualTo(new ArrayList<>());
    }

    @Test
    void findAllPointHistoriesByUserIdWithPessimisticLock_메서드로_points_데이터를_찾아올_수_있다() {
        // given
        long userId = 1L;
        long amount = 10000L;
        PointHistory pointHistory = PointHistory.of(userId, amount, PointHistory.TransactionType.USE);
        pointHistoryRepository.save(pointHistory);

        // when
        List<PointHistory> result = pointHistoryRepository.findAllPointHistoriesByUserIdWithPessimisticLock(userId);

        // then
        assertThat(result.get(0).getId()).isEqualTo(userId);
        assertThat(result.get(0).getAmount()).isEqualTo(amount);
    }

    @Test
    void findAllPointHistoriesByUserIdWithPessimisticLock_메서드는_pointsHistory_데이터가_없으면_빈_리스트를_내려준다() {
        // given
        long userId = 1L;

        // when
        List<PointHistory> result = pointHistoryRepository.findAllPointHistoriesByUserIdWithPessimisticLock(userId);

        // then
        assertThat(result).isEqualTo(new ArrayList<>());
    }
}
