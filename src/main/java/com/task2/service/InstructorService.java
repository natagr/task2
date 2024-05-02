package com.task2.service;

import com.task2.exception.InstructorHasCoursesException;
import com.task2.model.Course;
import com.task2.model.Instructor;
import com.task2.model.dto.InstructorDto;
import com.task2.model.mapper.InstructorMapper;
import com.task2.repository.CourseRepository;
import com.task2.repository.InstructorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final InstructorMapper instructorMapper;

    /**
     * Retrieves all instructors as a paginated list of InstructorDto.
     *
     * @param pageable Pagination information including page size, page number, and sort order.
     * @return A paginated list of instructors converted to InstructorDto objects.
     */
    @Transactional(readOnly = true)
    public Page<InstructorDto> getAllInstructors(Pageable pageable) {
        return instructorRepository.findAll(pageable)
                .map(instructorMapper::toDto);
    }

    /**
     * Creates a new instructor from the provided InstructorDto.
     *
     * @param instructorDto Data transfer object containing the instructor details.
     * Converts DTO to entity, saves the new instructor to the repository.
     */
    @Transactional
    public void createInstructor(InstructorDto instructorDto) {
        Instructor instructor = instructorMapper.toEntity(instructorDto);
        instructorRepository.save(instructor);
    }

    /**
     * Updates an existing instructor identified by ID with the data provided in InstructorDto.
     *
     * @param id The ID of the instructor to update.
     * @param instructorDto Data transfer object containing the updated details.
     * @throws EntityNotFoundException if no instructor is found with the provided ID.
     */
    @Transactional
    public void updateInstructor(Long id, InstructorDto instructorDto) {
        Instructor instructor = instructorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Instructor not found with ID: " + id));
        instructor.setFirstName(instructorDto.getFirstName());
        instructor.setLastName(instructorDto.getLastName());
        instructor.setEmail(instructorDto.getEmail());
    }

    /**
     * Deletes an instructor from the repository by their ID.
     *
     * @param id The ID of the instructor to be deleted.
     * @throws EntityNotFoundException if no instructor is found with the provided ID.
     */
    @Transactional
    public void deleteInstructor(Long id) {
        if (!instructorRepository.existsById(id)) {
            throw new EntityNotFoundException("Instructor not found with ID: " + id);
        }

        List<Course> courses = courseRepository.findByInstructorId(id);
        if (!courses.isEmpty()) {
            throw new InstructorHasCoursesException("Cannot delete instructor with ID: " + id + " as there are courses associated with them.");
        }

        instructorRepository.deleteById(id);
    }
}
