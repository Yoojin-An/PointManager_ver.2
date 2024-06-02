package com.example.pointmanager.infrastucture;

import com.example.pointmanager.domain.point.Points;
import com.example.pointmanager.domain.point.PointsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.Optional;

@Repository
public class PointsRepositoryImplement implements PointsRepository {
    @Override
    @Transactional
    public Optional<Points> findPoints(long userId) {

    }
    @Override
    @Transactional
    void save(Points points) {

    }
}
