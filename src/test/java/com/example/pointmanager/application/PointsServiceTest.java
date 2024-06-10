package com.example.pointmanager.application;

import com.example.pointmanager.domain.Points;
import com.example.pointmanager.domain.PointsHistory;
import com.example.pointmanager.repository.PointsHistoryRepository;
import com.example.pointmanager.repository.PointsRepository;
import com.example.pointmanager.exception.InvalidPointsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("optimistic")
public class PointsServiceTest {
    private final PointsService pointsService;
    private final PointsRepository pointsRepository;
    private final PointsHistoryRepository pointsHistoryRepository;
    @Autowired
    public PointsServiceTest(PointsService pointsService,
                             PointsRepository pointsRepository,
                             PointsHistoryRepository pointsHistoryRepository) {
        this.pointsService = pointsService;
        this.pointsRepository = pointsRepository;
        this.pointsHistoryRepository = pointsHistoryRepository;
    }

    @BeforeEach
    public void before() {
        // 1번 유저의 잔고에 3번 충전, 1번 사용 결과 10000 포인트가 있는 상황을 가정
        pointsRepository.save(new Points(1, 10000));
        pointsHistoryRepository.saveAndFlush(PointsHistory.of(1, 10000, PointsHistory.TransactionType.CHARGE));
        pointsHistoryRepository.saveAndFlush(PointsHistory.of(1, 10000, PointsHistory.TransactionType.CHARGE));
        pointsHistoryRepository.saveAndFlush(PointsHistory.of(1, 10000, PointsHistory.TransactionType.CHARGE));
        pointsHistoryRepository.saveAndFlush(PointsHistory.of(1, 20000, PointsHistory.TransactionType.USE));
    }

    @AfterEach
    public void after() {
        pointsRepository.deleteAll();
        pointsHistoryRepository.deleteAll();
    }

    /**
     * 포인트 조회 테스트
     */
    @Test
    public void 포인트_조회에_성공한다() {
        // given: 포인트 이용 이력이 있는 아이디
        long userId = 1L;

        // when: 포인트 조회
        long points = pointsService.findPoints(userId).getAmount();

        // then: 조회한 값이 잔고와 일치하는지 검증
        assertEquals(10000, points);
    }

    @Test
    void 존재할_수_없는_아이디에_대한_포인트_조회는_실패한다() {
        // given: 유효하지 않은 아이디
        long userId = 0L;

        // when & then: 유효하지 않은 아이디로 포인트 조회한 결과
        // InvalidPointsException이 발생하고 예외 메세지가 예상한 바와 같음을 검증
        InvalidPointsException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.findPoints(userId);
        });
        assertEquals("userId가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 포인트_이용_내역이_없는_경우_조회에_실패한다() {
        // given: 포인트 이용 내역이 없는 아이디
        long userId = 2L;

        // when & then: 한 번도 포인트를 충전한 적이 없는 아이디로 포인트 조회한 결과
        // InvalidPointsException이 발생하고 예외 메세지가 예상한 바와 같음을 검증
        RuntimeException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.findPoints(userId);
        });
        assertEquals("userId가 존재하지 않습니다.", exception.getMessage());
    }

    /**
     * 포인트 이용 내역 조회 테스트
     */
    @Test
    public void 포인트_충전_또는_사용_이력이_있는_경우_포인트_내역_조회에_성공한다() {
        // given: 포인트 이용 이력이 있는 아이디
        long userId = 1L;

        // when: 3번 충전, 1번 사용의 이력이 있음을 가정하고 포인트 충전, 사용 이력 조회
        List<PointsHistory> pointsHistory = pointsService.findPointsHistory(userId);

        // then: 잔고, 충전/사용 횟수, 충전/사용 타입 검증
        assertEquals(10000, pointsHistory.get(0).getAmount());
        assertEquals(4, pointsHistory.size());
        assertEquals(PointsHistory.TransactionType.CHARGE, pointsHistory.get(2).getTransactionType());
    }

    @Test
    public void 포인트_충전_또는_사용_이력이_없는_경우_포인트_내역_조회에_실패한다() {
        // given: 포인트 이용 이력이 없는 아이디
        long userId = 4L;

        // when: 한 번도 포인트를 충전한 적이 없는 아이디로 포인트 내역 조회한 결과
        // InvalidPointsException이 발생하고 예외 메세지가 예상한 바와 같음을 검증
        InvalidPointsException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.findPointsHistory(userId);
        });

        assertEquals("userId가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 존재할_수_없는_아이디에_대한_포인트_내역_조회는_실패한다() {
        // given: 유효하지 않은 아이디
        long userId = -1L;

        // when & then: 유효하지 않은 아이디로 포인트 내역 조회한 결과
        // InvalidPointsException이 발생하고 예외 메세지가 예상한 바와 같음을 검증
        InvalidPointsException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.findPointsHistory(userId);
        });
        assertEquals("userId가 존재하지 않습니다.", exception.getMessage());
    }


    /**
     * 포인트 충전 테스트
     */
    @Test
    public void 포인트_충전에_성공한다() throws InterruptedException {
        // given: 포인트 이용 이력이 있는 아이디와 충전할 포인트
        long userId = 1L;
        long balance = 10000;
        long amountToCharge = 5000;

        // when: 포인트 충전
        long points = pointsService.chargePoints(userId, amountToCharge).getAmount();

        // then: 잔고에 충전할 금액을 더한 값이 충전 결과와 같은지 검증
        assertThat(points).isEqualTo(balance + amountToCharge);
    }

    @Test
    public void 충전할_포인트가_음수이거나_0이면_충전에_실패한다() {
        // given: 포인트 이용 이력이 있는 아이와 유효하지 않은 충전 포인트
        long userId = 1L;
        long balance = 10000L;
        long amountToCharge = -500L;

        // when: -500 포인트 충전으로 예외 발생
        InvalidPointsException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.chargePoints(userId, amountToCharge);
        });

        // then: InvalidPointsException 발생 메세지 및 예외 발생 전후 포인트 일치 상태 검증
        assertEquals("포인트 충전 불가: 유효하지 않은 값입니다.", exception.getMessage());
        assertEquals(balance, pointsService.findPoints(userId).getAmount());
    }

    @Test
    void 존재할_수_없는_아이디에_대한_포인트_충전은_실패한다() {
        // given: 유효하지 않은 아이디와 유효한 포인트
        long userId = -1L;
        long amount = 10000L;

        // when & then: 유효하지 않은 아이디로 포인트 조회한 결과
        // RuntimeException이 발생하고 예외 메세지가 예상한 바와 같음을 검증
        InvalidPointsException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.chargePoints(userId, amount);
        });
        assertEquals("userId가 존재하지 않습니다.", exception.getMessage());
    }

    /**
     * 포인트 사용 테스트
     */
    @Test
    public void 포인트_사용에_성공한다() throws InterruptedException {
        // given: 포인트 사용 이력이 있는 아이디와 유효한 포인트
        long balance = 10000;
        long amountToUse = 5000;

        // when: 포인트 사용
        long points = pointsService.usePoints(1, amountToUse).getAmount();

        // then: 잔고에 사용한 금액을 뺀 값이 사용 결과와 같은지 검증
        assertEquals(balance - amountToUse, points);
    }

    @Test
    void 포인트_사용시_잔고가_부족하면_InvalidPointsException을_던진다() {
        // given: 잔고가 부족한 상황
        long userId = 1L;
        long amountToUse = 50000L;

        // when & then: 초기 포인트 10000인 상황에서 50000 포인트 사용 시 InvalidPointsException 예외 발생 및 예외 메세지 검증
        RuntimeException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.usePoints(userId, amountToUse);
        });
        assertEquals("포인트 사용 불가: 잔고가 부족합니다.", exception.getMessage());
    }

    @Test
    void 사용할_포인트가_0이거나_음수이면_포인트_사용에_실패한다() {
        // given: 포인트 사용 이력이 있는 아이디와 유효하지 않은 포인트
        long userId = 1L;
        long amountToUse = -10000L;

        // when & then: 유효하지 않은 포인트 사용을 시도한 결과
        // InvalidPointsException이 발생하고 예외 메세지가 예상한 바와 같음을 검증
        InvalidPointsException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.usePoints(userId, amountToUse);
        });
        assertEquals("포인트 사용 불가: 유효하지 않은 값입니다.", exception.getMessage());
    }

    @Test
    void 존재할_수_없는_아이디에_대한_포인트_사용은_실패한다() {
        // given: 유효하지 않은 아이디와 유효한 포인트
        long inValidId = -1L;
        long amountToUse = 10000L;

        // when & then: 유효하지 않은 아이디의 포인트 사용을 시도한 결과
        InvalidPointsException exception = assertThrows(InvalidPointsException.class, () -> {
            pointsService.chargePoints(inValidId, amountToUse);
        });
        // InvalidPointsException 발생하고 예외 메세지가 예상한 바와 같음을 검증
        assertEquals("userId가 존재하지 않습니다.", exception.getMessage());
    }
}
