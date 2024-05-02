package com.task2.model.dto;

import com.task2.model.annotation.UniqueCode;
import com.task2.model.constant.Department;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class CourseDto {

    @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
    private String name;

    @UniqueCode
    private String code;

    private String description;

    @Max(value = 100, message = "No more than 100 credits")
    private Integer credits;

    @NotNull
    private Set<Department> departments = new LinkedHashSet<>();

    @NotNull
    private Long instructorId;
}
