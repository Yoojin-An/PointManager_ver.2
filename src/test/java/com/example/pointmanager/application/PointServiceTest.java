package com.example.pointmanager.application;

import com.example.pointmanager.domain.Point;
import com.example.pointmanager.domain.PointHistory;
import com.example.pointmanager.repository.PointHistoryRepository;
import com.example.pointmanager.repository.PointRepository;
import com.example.pointmanager.exception.InvalidPointException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PointServiceTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    void before() {
        // 초기 값으로 10000 포인트를 가진 1번 유저가 10000 포인트 3번 충전, 30000 포인트 1번 사용 결과 잔고에 10000 포인트가 있는 상황을 가정
        pointRepository.save(new Point(1L, 10000L));
        pointHistoryRepository.saveAndFlush(PointHistory.of(1L, 10000L, PointHistory.TransactionType.CHARGE));
        pointHistoryRepository.saveAndFlush(PointHistory.of(1L, 10000L, PointHistory.TransactionType.CHARGE));
        pointHistoryRepository.saveAndFlush(PointHistory.of(1L, 10000L, PointHistory.TransactionType.CHARGE));
        pointHistoryRepository.saveAndFlush(PointHistory.of(1L, 30000L, PointHistory.TransactionType.USE));
    }

    @AfterEach
    void after() {
        pointRepository.deleteAllInBatch();
        pointHistoryRepository.deleteAllInBatch();
    }


    @DisplayName("포인트 조회 테스트")
    @Nested
    class FindPointTest {
        @Test
        void 포인트_조회에_성공한다() {
            // given
            long userId = 1L;
            long expectedAmount = 10000L;

            // when
            long point = pointService.findPoint(userId).getAmount();

            // then
            assertThat(point).isEqualTo(expectedAmount);
        }

        @Test
        void 유효하지_않은_아이디에_대한_포인트_조회는_실패한다() {
            // given
            long userId = 0L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.findPoint(userId);
            });
        }

        @Test
        void 포인트_이용_내역이_없는_경우_조회에_실패한다() {
            // given
            long userId = 2L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.findPoint(userId);
            });
        }
    }


    @DisplayName("포인트 이용 내역 조회 테스트")
    @Nested
    class FindPointHistoriesTest {
        @Test
        void 포인트_이용_내역_조회에_성공한다() {
            // given
            long userId = 1L;
            int consumeCount = 4;
            long firstChargeAmount = 10000L;
            PointHistory.TransactionType thirdTransactionType = PointHistory.TransactionType.CHARGE;

            // when
            List<PointHistory> pointHistory = pointService.findPointHistories(userId);

            // then
            assertThat(pointHistory.size()).isEqualTo(consumeCount);
            assertThat(pointHistory.get(0).getAmount()).isEqualTo(firstChargeAmount);
            assertThat(pointHistory.get(2).getTransactionType()).isEqualTo(thirdTransactionType);
        }

        @Test
        void 유효하지_않은_아이디에_대한_포인트_내역_조회는_실패한다() {
            // given
            long userId = -1L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.findPointHistories(userId);
            });
        }

        @Test
        void 포인트_이용_내역이_없는_경우_내역_조회에_실패한다() {
            // given
            long userId = 4L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.findPointHistories(userId);
            });
        }
    }


    @DisplayName("포인트 충전 테스트")
    @Nested
    class ChargePointTest {
        @Test
        void 신규_유저에_대한_포인트_충전에_성공한다() {
            // given
            long userId = 2L;
            long chargeAmount = 10000L;

            // when
            long point = pointService.chargePoint(userId, chargeAmount, true).getAmount();

            // then
            assertThat(point).isEqualTo(chargeAmount);
        }

        @Test
        void 기존_유저에_대한_포인트_충전에_성공한다() {
            // given
            long userId = 1L;
            long amount = 10000L;
            long chargeAmount = 5000L;

            // when
            long point = pointService.chargePoint(userId, chargeAmount, true).getAmount();

            // then
            assertThat(point).isEqualTo(amount + chargeAmount);
        }

        @Test
        void 충전할_포인트가_0_이하면_충전에_실패한다() {
            // given
            long userId = 1L;
            long chargeAmount = -500L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.chargePoint(userId, chargeAmount, true);
            });
        }

        @Test
        void 유효하지_않은_아이디에_대한_포인트_충전은_실패한다() {
            // given
            long userId = -1L;
            long chargeAmount = 10000L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.chargePoint(userId, chargeAmount, true);
            });
        }
    }


    @DisplayName("포인트 사용 테스트")
    @Nested
    class PointUseTest {
        @Test
        void 포인트_사용에_성공한다() {
            // given
            long userId = 1L;
            long amount = 10000L;
            long useAmount = 5000L;

            // when
            long point = pointService.usePoint(userId, useAmount, true).getAmount();

            // then
            assertThat(point).isEqualTo(amount - useAmount);
        }

        @Test
        void 사용할_포인트가_가용_포인트보다_클_경우_포인트_사용에_실패한다() {
            // given
            long userId = 1L;
            long useAmount = 50000L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.usePoint(userId, useAmount, true);
            });
        }

        @Test
        void 사용할_포인트가_0이하면_포인트_사용에_실패한다() {
            // given
            long userId = 1L;
            long useAmount = -10000L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.usePoint(userId, useAmount, true);
            });
        }

        @Test
        void 유효하지_않은_아이디에_대한_포인트_사용은_실패한다() {
            // given
            long userId = -1L;
            long useAmount = 10000L;

            // when & then
            assertThrows(InvalidPointException.class, () -> {
                pointService.chargePoint(userId, useAmount, true);
            });
        }
    }
}
