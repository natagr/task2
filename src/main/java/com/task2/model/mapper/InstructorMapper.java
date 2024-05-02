package com.task2.model.mapper;

import com.task2.model.Course;
import com.task2.model.Instructor;
import com.task2.model.dto.CourseDto;
import com.task2.model.dto.InstructorDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstructorMapper {
    Instructor toEntity(InstructorDto instructorDto);

    InstructorDto toDto(Instructor instructor);
}