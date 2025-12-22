package com.example.mohago_nocar.course.infrastructure.spot;

import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelSpotJpaRepository extends JpaRepository<TravelSpot, Integer> {
    List<TravelSpot> findByCourseId(@NotNull Long courseId);
}
