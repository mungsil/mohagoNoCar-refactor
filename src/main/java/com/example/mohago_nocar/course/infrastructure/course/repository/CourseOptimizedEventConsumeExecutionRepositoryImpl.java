package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEventConsumeExecution;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventConsumeExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CourseOptimizedEventConsumeExecutionRepositoryImpl implements CourseOptimizedEventConsumeExecutionRepository {

    private final CourseOptimizedEventConsumeExecutionJpaRepository jpaRepository;

    @Override
    public CourseOptimizedEventConsumeExecution save(CourseOptimizedEventConsumeExecution execution) {
        return jpaRepository.save(execution);
    }

}
