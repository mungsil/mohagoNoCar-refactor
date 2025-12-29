package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.model.course.ProcessedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedCourseJpaRepository extends JpaRepository<ProcessedCourse, Long> {

}
