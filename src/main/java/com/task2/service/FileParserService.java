package com.task2.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task2.model.dto.CourseDto;
import com.task2.model.dto.UploadFileResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileParserService {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final CourseService courseService;

    /**
     * Processes a JSON file containing course data, attempting to create course records for each valid entry.
     *
     * @param file The multipart file containing JSON data to be parsed.
     * @return UploadFileResponse containing counts of successful and failed course creations.
     * @throws IOException If there is an issue reading from the file.
     */
    public UploadFileResponse processJsonFile(MultipartFile file) throws IOException {
        Integer successful = 0;
        Integer failed = 0;
        try (InputStream inputStream = file.getInputStream();
             JsonParser jsonParser = objectMapper.getFactory().createParser(inputStream)) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    JsonNode node = objectMapper.readTree(jsonParser);
                    try {
                        CourseDto courseDto = objectMapper.treeToValue(node, CourseDto.class);
                        Set<ConstraintViolation<CourseDto>> violations = validator.validate(courseDto);
                        if (violations.isEmpty()) {
                            courseService.createCourse(courseDto);
                            successful++;
                        } else {
                            failed++;
                        }
                    } catch (Exception e) {
                        failed++;
                    }
                }
            }
        }
        return new UploadFileResponse(successful, failed);
    }
}
