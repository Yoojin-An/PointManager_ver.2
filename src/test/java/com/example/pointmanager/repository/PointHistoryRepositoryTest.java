package com.example.pointmanager.repository;

import com.example.pointmanager.domain.PointsHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PointsHistoryRepositoryTest {

    @Autowired
    PointsHistoryRepository pointsHistoryRepository;

    @Test
    void PointsHistoryRepository가_제대로_연결되었다() {
        // given
        PointsHistory pointsHistory = PointsHistory.of(1, 10000L, PointsHistory.TransactionType.USE);

        // when
        PointsHistory result = pointsHistoryRepository.save(pointsHistory);

        // then
        assertThat(result.get(0).getUserId()).isEqualTo(1);
        assertThat(result.get(0).getAmount()).isEqualTo(pointsHistory.getAmount());
    }
    @Test
    void findPointsHistoryByUserId_메서드로_pointsHistory_데이터를_찾아올_수_있다() {
        // given
        PointsHistory pointsHistory = PointsHistory.of(1, 10000L, PointsHistory.TransactionType.USE);
        pointsHistoryRepository.save(pointsHistory);

        // when
        List<PointsHistory> result = pointsHistoryRepository.findPointsHistoryByUserId(1L);

        // then
        assertEquals(1L, result.get(0).getUserId());
        assertEquals(10000L, result.get(0).getAmount());
    }

    @Test
    void findPointsHistoryByUserId_메서드는_pointsHistory_데이터가_없으면_빈_리스트를_내려준다() {
        // given - None

        // when
        List<PointsHistory> result = pointsHistoryRepository.findPointsHistoryByUserId(1L);

        // then
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void findPointsHistoryByUserIdWithPessimisticLock_메서드로_points_데이터를_찾아올_수_있다() {
        // given
        PointsHistory pointsHistory = PointsHistory.of(1, 10000L, PointsHistory.TransactionType.USE);
        pointsHistoryRepository.save(pointsHistory);

        // when
        List<PointsHistory> result = pointsHistoryRepository.findPointsHistoryByUserIdWithPessimisticLock(1L);

        // then
        assertEquals(1L, result.get(0).getUserId());
        assertEquals(10000L, result.get(0).getAmount());
    }

    @Test
    void findPointsHistoryByUserIdWithPessimisticLock_메서드는_pointsHistory_데이터가_없으면_빈_리스트를_내려준다() {
        // given - None

        // when
        List<PointsHistory> result = pointsHistoryRepository.findPointsHistoryByUserIdWithPessimisticLock(1L);

        // then
        assertEquals(new ArrayList<>(), result);
    }
}
