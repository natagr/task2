package com.task2.model.mapper;

import com.task2.model.Course;
import com.task2.model.dto.CourseDto;
import com.task2.model.dto.CourseForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toEntity(CourseDto courseDto);

    @Mapping(source = "course.instructor.id", target = "instructorId")
    CourseDto toDto(Course course);

    CourseForm toForm(Course course);
}
