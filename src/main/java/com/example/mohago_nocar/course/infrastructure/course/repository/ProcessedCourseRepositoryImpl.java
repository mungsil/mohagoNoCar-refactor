package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.model.course.ProcessedCourse;
import com.example.mohago_nocar.course.domain.repository.ProcessedCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ProcessedCourseRepositoryImpl implements ProcessedCourseRepository {

    private final ProcessedCourseJpaRepository jpaRepository;

    @Override
    @Transactional
    public ProcessedCourse save(ProcessedCourse handleHistory) {
        return jpaRepository.save(handleHistory);
    }

}
