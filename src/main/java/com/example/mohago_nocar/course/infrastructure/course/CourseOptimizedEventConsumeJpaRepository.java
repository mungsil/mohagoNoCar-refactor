package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEventConsume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOptimizedEventConsumeJpaRepository extends JpaRepository<CourseOptimizedEventConsume, Long> {
}
