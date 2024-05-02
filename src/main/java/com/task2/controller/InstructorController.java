package com.task2.controller;

import com.task2.exception.ValidationException;
import com.task2.model.dto.InstructorDto;
import com.task2.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor")
public class InstructorController {

    private final InstructorService instructorService;

    /**
     * Retrieves all instructors with pagination support.
     *
     * @param pageable Contains pagination information.
     * @return A paginated list of instructors as InstructorDto.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Page<InstructorDto> getAllInstructors(Pageable pageable) {
        return instructorService.getAllInstructors(pageable);
    }

    /**
     * Creates a new instructor based on the provided InstructorDto.
     *
     * @param instructorDto DTO containing instructor details.
     * @param bindingResult Contains validation results.
     * @throws ValidationException If validation errors occur.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createInstructor(@Valid @RequestBody InstructorDto instructorDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getFieldErrors());
        }
        instructorService.createInstructor(instructorDto);
    }

    /**
     * Updates an existing instructor identified by their ID with the provided InstructorDto.
     *
     * @param id The ID of the instructor to update.
     * @param instructorDto DTO containing updated instructor details.
     * @param bindingResult Contains validation results.
     * @throws ValidationException If validation errors occur.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateInstructor(@PathVariable Long id, @Valid @RequestBody InstructorDto instructorDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getFieldErrors());
        }
        instructorService.updateInstructor(id, instructorDto);
    }

    /**
     * Deletes an instructor by their ID.
     *
     * @param id The ID of the instructor to be deleted.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstructor(@PathVariable Long id) {
        instructorService.deleteInstructor(id);
    }
}
