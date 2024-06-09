package com.example.pointmanager.repository;

import com.example.pointmanager.domain.Points;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PointsRepositoryTest {

    @Autowired
    PointsRepository pointsRepository;

    @Test
    void PointsRepository가_제대로_연결되었다() {
        // given
        Points points = new Points(1L, 10000L);

        // when
        Points result = pointsRepository.save(points);

        // then
        assertEquals(1L, result.getUserId());
        assertEquals(points.getAmount(), result.getAmount());
    }
    @Test
    void findPointsByUserId_메서드로_points_데이터를_찾아올_수_있다() {
        // given
        Points points = new Points(1L, 10000L);
        pointsRepository.save(points);

        // when
        Optional<Points> result = pointsRepository.findPointsByUserId(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        assertEquals(10000L, result.get().getAmount());
    }

    @Test
    void findPointsByUserId_메서드는_points_데이터가_없으면_빈_리스트를_내려준다() {
        // given - None

        // when
        Optional<Points> result = pointsRepository.findPointsByUserId(1L);

        // then
        assertFalse(result.isPresent());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void findPointsByUserIdWithOptimisticLock_메서드로_points_데이터를_찾아올_수_있다() {
        // given
        Points points = new Points(1L, 10000L);
        pointsRepository.save(points);

        // when
        Optional<Points> result = pointsRepository.findPointsByUserIdWithOptimisticLock(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        assertEquals(10000L, result.get().getAmount());
    }

    @Test
    void findPointsByUserIdWithOptimisticLock_메서드는_points_데이터가_없으면_빈_리스트를_내려준다() {
        // given - None

        // when
        Optional<Points> result = pointsRepository.findPointsByUserIdWithOptimisticLock(1L);

        // then
        assertFalse(result.isPresent());
        assertEquals(Optional.empty(), result);
    }

    @Test
    void findPointsByUserIdWithPessimisticLock_메서드로_points_데이터를_찾아올_수_있다() {
        // given
        Points points = new Points(1L, 10000L);
        pointsRepository.save(points);

        // when
        Optional<Points> result = pointsRepository.findPointsByUserIdWithPessimisticLock(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        assertEquals(10000L, result.get().getAmount());
    }

    @Test
    void findPointsByUserIdWithPessimisticLock_메서드는_points_데이터가_없으면_빈_리스트를_내려준다() {
        // given - None

        // when
        Optional<Points> result = pointsRepository.findPointsByUserIdWithPessimisticLock(1L);

        // then
        assertFalse(result.isPresent());
        assertEquals(Optional.empty(), result);
    }
}
