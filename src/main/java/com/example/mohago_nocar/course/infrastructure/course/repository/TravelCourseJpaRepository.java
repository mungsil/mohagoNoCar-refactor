package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface TravelCourseJpaRepository extends JpaRepository<TravelCourse, Long> {

}
