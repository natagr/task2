package com.task2.model.dto;

import com.task2.model.constant.Department;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CourseFilterRequest {

    private Long instructorId;
    private Integer credits;
    private Set<Department> departments;
    private int page = 0;
    private int size = 20;
}