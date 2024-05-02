package com.task2.model.dto;

import com.task2.model.Instructor;
import com.task2.model.constant.Department;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CourseForm {

    private String name;
    private String code;
    private String description;
    private Integer credits;
    private List<Department> departments = new ArrayList<>();
    private Instructor instructor;
}
