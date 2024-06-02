package com.example.pointmanager.domain.points;

import com.example.pointmanager.exception.InvalidPointsException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Points {

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "amount_of_points")
    private long amount;

    public Points charge(long amountToCharge) {
        if (amountToCharge < 0) throw new InvalidPointsException("포인트 충전 불가: 유효하지 않은 값입니다.");
        long balance = this.amount + amountToCharge;
        return new Points(this.userId, balance);
    }

    public Points use(long amountToUse) {
        long balance = this.amount - amountToUse;
        if (balance < 0) throw new InvalidPointsException("포인트 사용 불가: 잔고가 부족합니다.");
        return new Points(this.userId, balance);
    }
}
