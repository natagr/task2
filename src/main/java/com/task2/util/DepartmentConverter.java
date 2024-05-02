package com.task2.util;

import com.task2.model.constant.Department;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
@Component
public class DepartmentConverter implements AttributeConverter<Set<Department>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Department> attribute) {
        return attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<Department> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(","))
                .filter(StringUtils::isNotEmpty)
                .map(Department::valueOf)
                .collect(Collectors.toSet());
    }
}