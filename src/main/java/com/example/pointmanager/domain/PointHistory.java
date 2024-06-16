package com.example.pointmanager.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "point_histories")
public class PointHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "amount")
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

    public static PointHistory of(long userId,
                                  long amount,
                                  TransactionType transactionType) {
        PointHistory pointHistory = new PointHistory();
        pointHistory.id = null;
        pointHistory.userId = userId;
        pointHistory.amount = amount;
        pointHistory.transactionType = transactionType;
        pointHistory.updateMillis = System.currentTimeMillis();
        return pointHistory;
    }

    @Override
    public String toString() {
        return "PointsHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", updateMillis=" + updateMillis +
                ", version=" + version +
                '}';
    }
}
