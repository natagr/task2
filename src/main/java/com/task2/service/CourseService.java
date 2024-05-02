package com.task2.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.task2.model.Course;
import com.task2.model.Instructor;
import com.task2.model.QCourse;
import com.task2.model.dto.CourseDto;
import com.task2.model.dto.CourseFilterRequest;
import com.task2.model.dto.CourseForm;
import com.task2.model.mapper.CourseMapper;
import com.task2.repository.CourseRepository;
import com.task2.repository.InstructorRepository;
import com.task2.util.DepartmentConverter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CourseMapper courseMapper;
    private final JPAQueryFactory queryFactory;
    private final DepartmentConverter departmentConverter;

    /**
     * Creates a new course with the specified details, associating it with an instructor.
     *
     * @param courseDto Data transfer object containing course details and instructor ID.
     * Throws EntityNotFoundException if the instructor ID does not exist.
     */
    @Transactional
    public void createCourse(CourseDto courseDto) {
        Instructor instructor = instructorRepository.findById(courseDto.getInstructorId())
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found with ID: " + courseDto.getInstructorId()));
        Course course = courseMapper.toEntity(courseDto);
        course.setInstructor(instructor);
        courseRepository.save(course);
    }

    /**
     * Retrieves a course by its ID and maps it to a CourseForm.
     *
     * @param id The ID of the course to retrieve.
     * @return CourseForm containing detailed information about the course.
     * Throws EntityNotFoundException if the course is not found.
     */
    @Transactional(readOnly = true)
    public CourseForm getCourseById(Long id) {
        Course course = courseRepository.findCourseWithInstructorById(id).orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));
        return courseMapper.toForm(course);

    }

    /**
     * Updates an existing course with new details provided in the courseDto.
     *
     * @param id The ID of the course to update.
     * @param courseDto Data transfer object containing new course details.
     * Throws EntityNotFoundException if the course or instructor ID does not exist.
     */
    @Transactional
    public void updateCourse(Long id, CourseDto courseDto) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));
        Instructor instructor = instructorRepository.findById(courseDto.getInstructorId()).orElseThrow(() -> new EntityNotFoundException("Instructor not found with ID: " + courseDto.getInstructorId()));
        course.setName(courseDto.getName());
        course.setCode(courseDto.getCode());
        course.setCredits(courseDto.getCredits());
        course.setDepartments(courseDto.getDepartments());
        course.setInstructor(instructor);
        course.setDescription(courseDto.getDescription());
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id The ID of the course to delete.
     * Throws EntityNotFoundException if the course is not found.
     */
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with ID: " + id);
        }
        courseRepository.deleteById(id);
    }

    /**
     * Retrieves a paginated list of courses filtered by specified criteria.
     *
     * @param filterRequest Contains filtering criteria such as instructor ID, credits, and departments.
     * @return Page of CourseDto with courses matching the filter criteria.
     */
    @Transactional(readOnly = true)
    public Page<CourseDto> getCourses(CourseFilterRequest filterRequest) {
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        QCourse course = QCourse.course;
        BooleanExpression where = getFilterPredicate(filterRequest, course);

        List<CourseDto> courseDtos = queryFactory.selectFrom(course)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(courseMapper::toDto)
                .toList();

        long total = queryFactory.select(course.count())
                .from(course)
                .where(where)
                .stream()
                .count();
        return new PageImpl<>(courseDtos, pageable, total);
    }

    /**
     * Constructs a BooleanExpression that represents the filtering criteria for querying courses.
     *
     * @param filterRequest Contains criteria such as instructor ID, credits, and departments to filter courses.
     * @param course A QCourse instance used to build the query predicates.
     * @return BooleanExpression that can be used as a predicate in a query to filter courses based on specified criteria.
     */
    private BooleanExpression getFilterPredicate(CourseFilterRequest filterRequest, QCourse course) {
        BooleanExpression where = null;

        if (filterRequest.getInstructorId() != null) {
            where = course.instructor.id.eq(filterRequest.getInstructorId());
        }
        if (filterRequest.getCredits() != null) {
            where = (where == null ? course.credits.eq(filterRequest.getCredits()) : where.and(course.credits.eq(filterRequest.getCredits())));
        }
        if (filterRequest.getDepartments() != null && !filterRequest.getDepartments().isEmpty()) {
            List<BooleanExpression> departmentPredicates = filterRequest.getDepartments().stream()
                    .map(Enum::name)
                    .map(course.departmentString::contains)
                    .toList();

            BooleanExpression combinedDeptPredicate = departmentPredicates.stream()
                    .reduce(BooleanExpression::and)
                    .orElse(null);

            where = (where == null ? combinedDeptPredicate : where.and(combinedDeptPredicate));
        }
        return where;
    }

    /**
     * Generates a CSV report of courses that match the given filter criteria.
     *
     * @param filterRequest Filtering criteria for selecting courses.
     * @return A byte array representing the CSV file.
     * Uses a loop to fetch all pages of course data and write them to a CSV file.
     */
    @SneakyThrows
    @Transactional(readOnly = true)
    public byte[] generateCourseReport(CourseFilterRequest filterRequest) {
        int currentPage = 0;
        Page<CourseDto> courses;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "Name",
                "Instructor id",
                "Department",
                "Description",
                "Credits",
                "Course Code"));
        do {
            filterRequest.setPage(currentPage);
            courses = getCourses(filterRequest);

            for (CourseDto course : courses) {
                csvPrinter.printRecord(
                        course.getName(),
                        course.getInstructorId(),
                        departmentConverter.convertToDatabaseColumn(course.getDepartments()),
                        course.getDescription(),
                        course.getCredits(),
                        course.getCode());
            }
            currentPage++;
        } while (currentPage < courses.getTotalPages());

        csvPrinter.flush();
        csvPrinter.close();

        return out.toByteArray();
    }
}



