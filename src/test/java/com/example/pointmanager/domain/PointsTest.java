package com.example.pointmanager.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointsTest {
    @Test
    public void 포인트_충전에_성공한다() {
        // given
        long balance = 0;
        Points points = new Points(1, balance);

        // when
        long amountToCharge = 100000;
        long TotalBalance = points.charge(100000).getAmount();

        // then
        assertEquals(balance + amountToCharge, TotalBalance);
    }

    @Test
    public void 음수값의_포인트_입력으로_충전에_실패한다() {
        // given
        Points points = new Points(1, 0);

        // when & then
        long invalidAmountToCharge = -50;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            points.charge(invalidAmountToCharge);
        });
        assertEquals("포인트 충전 불가: 유효하지 않은 값입니다.", exception.getMessage());
    }

    @Test
    public void 포인트_사용에_성공한다() {
        // given
        long balance = 100000;
        Points points = new Points(1, balance);

        // when
        long amountToUse = 100;
        long totalBalance = points.use(amountToUse).getAmount();

        // then
        assertEquals(balance - amountToUse, totalBalance);
    }

    @Test
    public void 잔고_부족으로_포인트_사용에_실패한다() {
        // given
        Points points = new Points(1, 0);

        // when & then
        long invalidAmountToUse = 50;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            points.use(invalidAmountToUse);
        });
        assertEquals("포인트 사용 불가: 잔고가 부족합니다.", exception.getMessage());
    }
}
