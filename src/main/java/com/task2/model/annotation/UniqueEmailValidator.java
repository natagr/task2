package com.task2.model.annotation;

import com.task2.model.Instructor;
import com.task2.repository.InstructorRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final InstructorRepository instructorRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String method = request.getMethod();
        String path = request.getRequestURI();
        Long instructorId = null;
        if (method.equalsIgnoreCase("PUT") && path.matches("/api/instructor/\\d+")) {
            instructorId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
        }

        Instructor existingInstructor = instructorRepository.findByEmail(value);

        if (existingInstructor == null) {
            return true;
        } else return instructorId != null && existingInstructor.getId().equals(instructorId);
    }
}