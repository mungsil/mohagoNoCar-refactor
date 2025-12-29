package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.domain.model.course.ProcessedCourse;

public interface ProcessedCourseRepository {
    ProcessedCourse save(ProcessedCourse handleHistory);
}
