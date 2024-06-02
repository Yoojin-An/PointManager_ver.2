package com.example.pointmanager.infrastucture;
import com.example.pointmanager.domain.point.PointsHistory;
import com.example.pointmanager.domain.point.PointsHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PointsHistoryRepositoryImplement implements PointsHistoryRepository {

    @Override
    @Transactional
    public List<Optional<PointsHistory>> findPointsHistory {

    }

    @Override
    @Transactional
    public void save(PointsHistory pointsHistory) {

    }
}
