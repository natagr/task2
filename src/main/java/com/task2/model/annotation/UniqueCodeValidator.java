package com.task2.model.annotation;

import com.task2.model.Course;
import com.task2.repository.CourseRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public class UniqueCodeValidator implements ConstraintValidator<UniqueCode, String> {

    private final CourseRepository courseRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        Long courseId = null;
        if (method.equalsIgnoreCase("PUT") && path.matches("/api/course/\\d+")) {
            courseId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
        }

        Course existingCourse = courseRepository.findByCode(value);

        if (existingCourse == null) {
            return true;
        } else if (courseId != null && existingCourse.getId().equals(courseId)) {
            return true;
        } else {
            return false;
        }
    }
}

