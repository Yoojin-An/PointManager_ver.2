package com.example.pointmanager.domain.points;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
public class PointsHistory{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "amount_of_points")
    private long amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "update_millis")
    private long updateMillis;

    @Version
    private long version;

    public enum TransactionType {
        CHARGE,
        USE
    }

    public static PointsHistory of(long userId,
                                   long amount,
                                   TransactionType transactionType) {
        PointsHistory pointsHistory = new PointsHistory();
        pointsHistory.id = null;
        pointsHistory.userId = userId;
        pointsHistory.amount = amount;
        pointsHistory.transactionType = transactionType;
        pointsHistory.updateMillis = System.currentTimeMillis();
        return pointsHistory;
    }
}
