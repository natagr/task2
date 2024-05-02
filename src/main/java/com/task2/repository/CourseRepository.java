package com.task2.repository;

import com.task2.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, QuerydslPredicateExecutor<Course> {

    @EntityGraph(attributePaths = {"instructor"})
    Optional<Course> findCourseWithInstructorById(Long courseId);

    Course findByCode(String value);

    List<Course> findByInstructorId(Long id);
}