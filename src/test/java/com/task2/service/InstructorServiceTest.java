package com.task2.service;

import com.task2.model.Instructor;
import com.task2.model.dto.InstructorDto;
import com.task2.repository.CourseRepository;
import com.task2.repository.InstructorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
public class InstructorServiceTest {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;


    @BeforeEach
    @Transactional
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllInstructors() {
        courseRepository.deleteAll();
        instructorRepository.deleteAll();

        Instructor instructor1 = new Instructor();
        instructor1.setFirstName("John");
        instructor1.setLastName("Doe");
        instructor1.setEmail("john@example.com");
        instructor1 = instructorRepository.save(instructor1);

        Instructor instructor2 = new Instructor();
        instructor2.setFirstName("Jane");
        instructor2.setLastName("Smith");
        instructor2.setEmail("jane@example.com");
        instructor2 = instructorRepository.save(instructor2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<InstructorDto> instructorDtos = instructorService.getAllInstructors(pageable);

        assertThat(instructorDtos).isNotNull();
        assertThat(instructorDtos.getTotalElements()).isEqualTo(2);
        assertThat(instructorDtos.getContent())
                .extracting(InstructorDto::getFirstName, InstructorDto::getLastName, InstructorDto::getEmail)
                .containsExactlyInAnyOrder(
                        tuple("John", "Doe", "john@example.com"),
                        tuple("Jane", "Smith", "jane@example.com")
                );
    }

    @Test
    public void testCreateInstructor() {
        InstructorDto instructorDto = new InstructorDto();
        instructorDto.setFirstName("John");
        instructorDto.setLastName("Doe");
        instructorDto.setEmail("johndtounique@example.com");

        instructorService.createInstructor(instructorDto);

        Instructor instructor = instructorRepository.findByEmail("johndtounique@example.com");
        assertThat(instructor).isNotNull();
        assertThat(instructor.getFirstName()).isEqualTo("John");
        assertThat(instructor.getLastName()).isEqualTo("Doe");
        assertThat(instructor.getEmail()).isEqualTo("johndtounique@example.com");
    }

    @Test
    public void testUpdateInstructor() {
        Instructor existingInstructor = new Instructor();
        existingInstructor.setFirstName("John");
        existingInstructor.setLastName("Doe");
        existingInstructor.setEmail("existjane@gmail.com");
        existingInstructor = instructorRepository.save(existingInstructor);

        InstructorDto updatedInstructorDto = new InstructorDto();
        updatedInstructorDto.setFirstName("John");
        updatedInstructorDto.setLastName("Doe");
        updatedInstructorDto.setEmail("existjaneupdate@gmail.com");

        instructorService.updateInstructor(existingInstructor.getId(), updatedInstructorDto);

        Instructor updatedInstructor = instructorRepository.findById(existingInstructor.getId()).orElseThrow();
        assertThat(updatedInstructor.getFirstName()).isEqualTo("John");
        assertThat(updatedInstructor.getLastName()).isEqualTo("Doe");
        assertThat(updatedInstructor.getEmail()).isEqualTo("existjaneupdate@gmail.com");
    }

    @Test
    public void testDeleteInstructor() {
        Instructor instructorToDelete = new Instructor();
        instructorToDelete.setFirstName("John");
        instructorToDelete.setLastName("Doe");
        instructorToDelete.setEmail("johntodelete@gmail.com");
        instructorToDelete = instructorRepository.save(instructorToDelete);

        instructorService.deleteInstructor(instructorToDelete.getId());

        assertThat(instructorRepository.findById(instructorToDelete.getId())).isEmpty();
    }
}