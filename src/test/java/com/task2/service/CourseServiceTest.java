package com.task2.service;

import static com.task2.model.constant.Department.COMPUTER_SCIENCE;
import static com.task2.model.constant.Department.MATHEMATICS;
import static com.task2.model.constant.Department.ELECTRONICS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.task2.model.Course;
import com.task2.model.Instructor;
import com.task2.model.constant.Department;
import com.task2.model.dto.CourseDto;
import com.task2.model.dto.CourseFilterRequest;
import com.task2.model.dto.CourseForm;
import com.task2.repository.InstructorRepository;
import com.task2.repository.CourseRepository;

import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    private Instructor instructor;
    private Course course;
    private Course course2;
    private CourseDto courseDto;

    @BeforeEach
    @Transactional
    void setup() {
        MockitoAnnotations.openMocks(this);

        instructor = new Instructor();
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructor.setEmail("instructor0@gmail.com");
        instructor = instructorRepository.save(instructor);

        course = new Course();
        course.setName("course7");
        course.setCode("CS1089");
        course.setDepartments(Stream.of(COMPUTER_SCIENCE, MATHEMATICS, ELECTRONICS).collect(Collectors.toSet()));
        course.setInstructor(instructor);
        course.setCredits(30);
        course = courseRepository.save(course);

        course2 = new Course();
        course2.setName("course1");
        course2.setCode("CS1099");
        course2.setDepartments(Stream.of(COMPUTER_SCIENCE).collect(Collectors.toSet()));
        course2.setDescription("Description here");
        course2.setInstructor(instructor);
        course2.setCredits(3);
        course2 = courseRepository.save(course);

        courseDto = new CourseDto();
        courseDto.setName("course7");
        courseDto.setCode("CS1055");
        courseDto.setDepartments(Stream.of(COMPUTER_SCIENCE, MATHEMATICS, ELECTRONICS).collect(Collectors.toSet()));
        courseDto.setInstructorId(instructor.getId());
        courseDto.setCredits(30);
    }

    @Test
    @Transactional
    void createCourseTest() {
        courseService.createCourse(courseDto);
        Course course = courseRepository.findByInstructorId(instructor.getId()).get(0);
        assertNotNull(course);
        assertEquals("course7", course.getName());
        assertEquals("CS1089", course.getCode());
        assertEquals(Stream.of(COMPUTER_SCIENCE, Department.MATHEMATICS, Department.ELECTRONICS).collect(Collectors.toSet()), course.getDepartments());
        assertEquals(30, course.getCredits());
        assertEquals(instructor, course.getInstructor());
    }

    @Test
    @Transactional
    void updateCourseTest() {
        courseService.updateCourse(course.getId(), courseDto);
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertNotNull(updatedCourse);
        assertEquals("course7", updatedCourse.getName());
        assertEquals("CS1055", updatedCourse.getCode());
        assertEquals(Stream.of(COMPUTER_SCIENCE, Department.MATHEMATICS, Department.ELECTRONICS).collect(Collectors.toSet()), course.getDepartments());
        assertEquals(30, updatedCourse.getCredits());
        assertEquals(instructor, updatedCourse.getInstructor());
    }

    @Test
    @Transactional
    void getCourseByIdTest() {
        CourseForm courseForm = courseService.getCourseById(course.getId());
        assertNotNull(courseForm);
        assertEquals("course7", courseForm.getName());
        assertEquals("CS1089", courseForm.getCode());
        assertEquals(Stream.of(COMPUTER_SCIENCE, Department.MATHEMATICS, Department.ELECTRONICS).collect(Collectors.toSet()), course.getDepartments());
        assertEquals(30, courseForm.getCredits());
        assertEquals(instructor, courseForm.getInstructor());
    }

    @Test
    @Transactional
    void deleteCourseTest() {
        courseService.deleteCourse(course.getId());
        Optional<Course> deletedCourse = courseRepository.findById(course.getId());
        assertTrue(deletedCourse.isEmpty());
    }

    @Test
    void getCoursesTest() {
        CourseFilterRequest filterRequest = new CourseFilterRequest();
        filterRequest.setInstructorId(1L);
        filterRequest.setCredits(3);
        filterRequest.setDepartments(Set.of(COMPUTER_SCIENCE, MATHEMATICS));
        Page<CourseDto> result = courseService.getCourses(filterRequest);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void generateCourseReport() throws IOException {
        CourseFilterRequest filterRequest = new CourseFilterRequest();
        filterRequest.setSize(10);
        filterRequest.setDepartments(Set.of(COMPUTER_SCIENCE));
        byte[] reportBytes = courseService.generateCourseReport(filterRequest);
        List<String> csvLines = readCsvLines(reportBytes);
        assertThat(csvLines).isNotEmpty();
        assertThat(csvLines.get(0)).isEqualTo("Name,Instructor id,Department,Description,Credits,Course Code");

        for (int i = 1; i < csvLines.size(); i++) {
            String[] fields = csvLines.get(i).split(",");
            assertThat(fields).hasSizeGreaterThanOrEqualTo(6);
            assertThat(fields[0]).as("Name").matches("^\\s*$|^\\S+.*");
            assertThat(fields[1]).as("Instructor ID").matches("^\\s*$|^\\S+.*");
            assertThat(fields[2]).containsAnyOf("COMPUTER_SCIENCE", "MATHEMATICS", "ELECTRONICS");
            assertThat(fields[3]).matches("^\\s*$|^\\S+.*");
            assertThat(fields[4]).as("Credits").matches("^\\s*$|^\\S+.*");
            assertThat(fields[5]).as("Course Code").matches("^\\s*$|^\\S+.*");
        }
    }

    private List<String> readCsvLines(byte[] bytes) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }
}