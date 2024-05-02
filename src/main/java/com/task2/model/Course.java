package com.task2.model;

import com.task2.model.base.AbstractIdentifiable;
import com.task2.model.constant.Department;
import com.task2.util.DepartmentConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course extends AbstractIdentifiable {

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "credits")
    private Integer credits;

    @ToString.Include
    @Convert(converter = DepartmentConverter.class)
    @Column(nullable = false)
    @NotNull
    private Set<Department> departments =  new LinkedHashSet<>();

    @Column(name = "departments", updatable = false, insertable = false)
    private String departmentString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;
}
