package com.task2.controller;

import static com.task2.model.constant.Department.COMPUTER_SCIENCE;
import static com.task2.model.constant.Department.MATHEMATICS;
import static com.task2.model.constant.Department.ELECTRONICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task2.model.dto.CourseDto;
import com.task2.model.dto.CourseFilterRequest;
import com.task2.model.dto.CourseForm;
import com.task2.model.dto.UploadFileResponse;
import com.task2.service.CourseService;
import com.task2.service.FileParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
public class CourseControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private CourseDto courseDto;

    @MockBean
    private CourseService courseService;

    @MockBean
    private FileParserService fileParserService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        courseDto = new CourseDto();
        courseDto.setName("course7");
        courseDto.setCode("CS1099");
        courseDto.setDepartments(Stream.of(COMPUTER_SCIENCE, MATHEMATICS, ELECTRONICS).collect(Collectors.toSet()));
        courseDto.setInstructorId(1L);
        courseDto.setCredits(30);
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateCourse() throws Exception {

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(courseDto)))
                .andExpect(status().isCreated());

        verify(courseService, times(1)).createCourse(any(CourseDto.class));
    }

    @Test
    public void testGetCourseById() throws Exception {
        Long courseId = 1L;
        CourseForm courseForm = new CourseForm();
        when(courseService.getCourseById(courseId)).thenReturn(courseForm);

        mockMvc.perform(get("/api/course/{id}", courseId))
                .andExpect(status().isOk());

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    public void testUpdateCourse() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(put("/api/course/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(courseDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<CourseDto> captor = ArgumentCaptor.forClass(CourseDto.class);
        verify(courseService).updateCourse(eq(courseId), captor.capture());

        CourseDto capturedDto = captor.getValue();
        assertEquals(courseDto.getName(), capturedDto.getName());
        assertEquals(courseDto.getDescription(), capturedDto.getDescription());
    }

    @Test
    public void testDeleteCourse() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(delete("/api/course/{id}", courseId))
                .andExpect(status().isNoContent());

        verify(courseService).deleteCourse(courseId);
    }

    @Test
    public void testGetCourses() throws Exception {
        PageImpl<CourseDto> courseDtos = new PageImpl<>(Arrays.asList(new CourseDto(), new CourseDto()));
        when(courseService.getCourses(any(CourseFilterRequest.class))).thenReturn(courseDtos);

        mockMvc.perform(post("/api/course/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new CourseFilterRequest())))
                .andExpect(status().isOk());

        verify(courseService).getCourses(any(CourseFilterRequest.class));
    }

    @Test
    public void testGenerateReport() throws Exception {
        byte[] reportData = "report data".getBytes();
        when(courseService.generateCourseReport(any(CourseFilterRequest.class))).thenReturn(reportData);

        mockMvc.perform(post("/api/course/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new CourseFilterRequest())))
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"courses-report.csv\""))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andReturn().getResponse().getContentAsByteArray();

        verify(courseService).generateCourseReport(any(CourseFilterRequest.class));
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "courses.json", "application/json", "[]".getBytes());
        UploadFileResponse response = new UploadFileResponse();
        when(fileParserService.processJsonFile(any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/course/upload")
                        .file(file))
                .andExpect(status().isOk());

        verify(fileParserService, times(1)).processJsonFile(any(MultipartFile.class));
    }
}