package com.task2.model.dto;

import com.task2.model.annotation.UniqueEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorDto {

    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    private String firstName;

    @Size(min = 2, max = 20, message = "last name must be between 2 and 20 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @UniqueEmail
    private String email;
}
