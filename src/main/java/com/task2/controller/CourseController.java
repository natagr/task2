package com.task2.controller;

import com.task2.exception.ValidationException;
import com.task2.model.dto.CourseDto;
import com.task2.model.dto.CourseFilterRequest;
import com.task2.model.dto.CourseForm;
import com.task2.model.dto.UploadFileResponse;
import com.task2.service.CourseService;
import com.task2.service.FileParserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

    private final CourseService courseService;
    private final FileParserService fileParserService;

    /**
     * Creates a new course with the data provided in the CourseDto.
     *
     * @param courseDto Data transfer object containing the course details.
     * @param bindingResult Binding result to handle validation errors.
     * @throws ValidationException if validation fails.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCourse(@Valid @RequestBody CourseDto courseDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getFieldErrors());
        }
        courseService.createCourse(courseDto);
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param id The unique identifier of the course.
     * @return CourseForm The detailed form of the course.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CourseForm getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    /**
     * Updates an existing course identified by ID with the new data provided.
     *
     * @param id The ID of the course to update.
     * @param courseDto Data transfer object containing the updated course details.
     * @param bindingResult Binding result to handle validation errors.
     * @throws ValidationException if validation fails.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDto courseDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getFieldErrors());
        }
        courseService.updateCourse(id, courseDto);
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id The ID of the course to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    /**
     * Retrieves a list of courses based on the filtering criteria.
     *
     * @param courseFilterRequest Request containing filtering criteria.
     * @return Page<CourseDto> Paginated result of courses.
     */
    @PostMapping("/_list")
    public Page<CourseDto> getCourses(@RequestBody CourseFilterRequest courseFilterRequest) {
        return courseService.getCourses(courseFilterRequest);
    }

    /**
     * Generates a report based on the course filtering criteria, returns a file.
     *
     * @param filterRequest Request containing filtering criteria.
     * @return ResponseEntity<byte[]> Response containing the file as a byte array.
     */
    @PostMapping("/_report")
    public ResponseEntity<byte[]> generateReport(@RequestBody CourseFilterRequest filterRequest) {
        byte[] data = courseService.generateCourseReport(filterRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "courses-report.csv");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().headers(headers).body(data);
    }

    /**
     * Handles the upload of a JSON file and processes it to extract course data.
     *
     * @param file The JSON file to process.
     * @return UploadFileResponse The result of the file processing.
     * @throws IOException if file processing fails.
     */
    @PostMapping("/upload")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return fileParserService.processJsonFile(file);
    }
}

