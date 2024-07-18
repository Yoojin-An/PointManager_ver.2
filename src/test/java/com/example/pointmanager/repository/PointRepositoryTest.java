package com.example.pointmanager.repository;

import com.example.pointmanager.domain.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PointRepositoryTest {

    @Autowired
    PointRepository pointRepository;

    @AfterEach
    void after() {
        pointRepository.deleteAllInBatch();
    }

    @Test
    void PointsRepository가_제대로_연결되었다() {
        // given
        long userId = 1L;
        long amount = 10000L;
        Point point = new Point(userId, amount);

        // when
        Point result = pointRepository.save(point);

        // then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getAmount()).isEqualTo(amount);
    }

    @Test
    void findPointsById_메서드로_point_데이터를_찾아올_수_있다() {
        // given
        long userId = 1L;
        long amount = 10000L;
        Point point = new Point(userId, amount);
        pointRepository.save(point);

        // when
        Optional<Point> result = pointRepository.findPointById(1L);

        // then
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(userId);
        assertThat(result.get().getAmount()).isEqualTo(amount);
    }

    @Test
    void findPointById_메서드는_point_데이터가_없으면_빈_Optional_객체를_내려준다() {
        // given - None

        // when
        Optional<Point> result = pointRepository.findPointById(1L);

        // then
        assertFalse(result.isPresent());
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    void findPointByUserIdWithOptimisticLock_메서드로_point_데이터를_찾아올_수_있다() {
        // given
        long userId = 1L;
        long amount = 10000L;
        Point point = new Point(userId, amount);
        pointRepository.save(point);

        // when
        Optional<Point> result = pointRepository.findPointByUserIdWithOptimisticLock(1L);

        // then
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(userId);
        assertThat(result.get().getAmount()).isEqualTo(amount);
    }

    @Test
    void findPointByUserIdWithOptimisticLock_메서드는_point_데이터가_없으면_빈_Optional_객체를_내려준다() {
        // given - None

        // when
        Optional<Point> result = pointRepository.findPointByUserIdWithOptimisticLock(1L);

        // then
        assertFalse(result.isPresent());
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    void findPointByUserIdWithPessimisticLock_메서드로_point_데이터를_찾아올_수_있다() {
        // given
        long userId = 1L;
        long amount = 10000L;
        Point point = new Point(userId, amount);
        pointRepository.save(point);

        // when
        Optional<Point> result = pointRepository.findPointByUserIdWithPessimisticLock(1L);

        // then
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(userId);
        assertThat(result.get().getAmount()).isEqualTo(amount);
    }

    @Test
    void findPointsByUserIdWithPessimisticLock_메서드는_points_데이터가_없으면_빈_Optional_객체를_내려준다() {
        // given - None

        // when
        Optional<Point> result = pointRepository.findPointByUserIdWithPessimisticLock(1L);

        // then
        assertFalse(result.isPresent());
        assertThat(result).isEqualTo(Optional.empty());
    }
}
