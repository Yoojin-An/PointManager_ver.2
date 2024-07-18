package com.example.pointmanager.domain;

import com.example.pointmanager.exception.InvalidPointException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "points")
public class Point {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "amount")
    private long amount;

    @Version
    private long version;

    public Point(long id, long amount) {
        this.id = id;
        this.amount = amount;
    }
    public Point charge(long chargeAmount) {
        if (chargeAmount <= 0) throw new InvalidPointException("포인트 충전 불가: 유효하지 않은 값입니다.");
        long amount = this.amount + chargeAmount;
        return new Point(this.id, amount);
    }

    public Point use(long useAmount) {
        if (useAmount <= 0) throw new InvalidPointException("포인트 사용 불가: 유효하지 않은 값입니다.");
        long amount = this.amount - useAmount;
        if (amount < 0) throw new InvalidPointException("포인트 사용 불가: 잔고가 부족합니다.");
        return new Point(this.id, amount);
    }
}
