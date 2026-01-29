package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEventConsume;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventConsumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CourseOptimizedEventConsumeRepositoryImpl implements CourseOptimizedEventConsumeRepository {

    private final CourseOptimizedEventConsumeJpaRepository jpaRepository;

    @Override
    public CourseOptimizedEventConsume save(CourseOptimizedEventConsume execution) {
        return jpaRepository.save(execution);
    }

}
