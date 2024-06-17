package com.example.pointmanager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointTest {
    @Test
    public void 포인트_충전에_성공한다() {
        // given
        long userId = 1L;
        long defaultAmount = 0L;
        long chargeAmount = 100000L;

        // when
        Point point = new Point(userId, defaultAmount);
        long amount = point.charge(chargeAmount).getAmount();

        // then
        assertThat(amount).isEqualTo(defaultAmount + chargeAmount);
    }

    @Test
    public void 충전할_포인트가_0이하면_충전에_실패한다() {
        // given
        long userId = 1L;
        long defaultAmount = 0L;
        long chargeAmount = -50;
        Point point = new Point(userId, defaultAmount);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            point.charge(chargeAmount);
        });
    }

    @Test
    public void 포인트_사용에_성공한다() {
        // given
        long userId = 1L;
        long defaultAmount = 100000L;
        long useAmount = 100L;
        Point point = new Point(userId, defaultAmount);

        // when
        long amount = point.use(useAmount).getAmount();

        // then
        assertThat(amount).isEqualTo(defaultAmount - useAmount);
    }

    @Test
    public void 사용할_포인트가_사용가능한_포인트보다_크면_사용에_실패한다() {
        // given
        long userId = 1L;
        long defaultAmount = 0L;
        long useAmount = 50;
        Point point = new Point(userId, defaultAmount);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            point.use(useAmount);
        });
    }
}
