package com.task2.service;

import com.task2.model.dto.CourseDto;
import com.task2.model.dto.UploadFileResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class FileParserServiceTest {

    @MockBean
    private CourseService courseService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testProcessJsonFile() throws Exception {
        String jsonContent = "[{\"name\":\"course7\",\"code\":\"CS101\",\"description\":\"something\",\"credits\":55,\"departments\":[\"COMPUTER_SCIENCE\",\"MATHEMATICS\"],\"instructorId\":1},{\"name\":\"course7\",\"code\":\"CS102\",\"description\":\"something\",\"credits\":50,\"departments\":[\"COMPUTER_SCIENCE\"],\"instructorId\":1},{\"name\":\"course7\",\"code\":\"CS103\",\"description\":\"something\",\"credits\":50,\"departments\":[\"MATHEMATICS\"],\"instructorId\":1}]";
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", jsonContent.getBytes());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/course/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UploadFileResponse uploadFileResponse = objectMapper.readValue(responseBody, UploadFileResponse.class);
        assertThat(uploadFileResponse.getSuccessfulRecords()).isEqualTo(3);
        assertThat(uploadFileResponse.getFailedRecords()).isEqualTo(0);

        verify(courseService, times(3)).createCourse(any(CourseDto.class));
    }

    @Test
    void testProcessJsonFileWithInvalidObject() throws Exception {
        String jsonContent = "[{\"name\":\"course7\",\"code\":\"CS101\",\"description\":\"something\",\"credits\":55,\"departments\":[\"COMPUTER_SCIENCE\",\"MATHEMATICS\"],\"instructorId\":\"\"},{\"name\":\"course7\",\"code\":\"CS102\",\"description\":\"something\",\"departments\":[\"COMPUTER_SCIENCE\"],\"instructorId\":1},{\"name\":\"course7\",\"code\":\"CS103\",\"description\":\"something\",\"credits\":50,\"departments\":[\"MATHEMATICS\"],\"instructorId\":1}]";
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", jsonContent.getBytes());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/course/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UploadFileResponse uploadFileResponse = objectMapper.readValue(responseBody, UploadFileResponse.class);
        assertThat(uploadFileResponse.getSuccessfulRecords()).isEqualTo(2);
        assertThat(uploadFileResponse.getFailedRecords()).isEqualTo(1);

        verify(courseService, times(2)).createCourse(any(CourseDto.class));
    }

    @Test
    void testProcessJsonFileWithEmptyStringInsteadOfNumber() throws Exception {
        String jsonContent = "[{\"name\":\"course7\",\"code\":\"CS101\",\"description\":\"something\",\"credits\":55,\"departments\":[\"COMPUTER_SCIENCE\",\"MATHEMATICS\"],\"instructorId\":1},{\"name\":\"course7\",\"code\":\"CS102\",\"description\":\"something\",\"credits\":\"invalid\",\"departments\":[\"COMPUTER_SCIENCE\"],\"instructorId\":1},{\"name\":\"course7\",\"code\":\"CS103\",\"description\":\"something\",\"credits\":50,\"departments\":[\"MATHEMATICS\"],\"instructorId\":1}]";
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", jsonContent.getBytes());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/course/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UploadFileResponse uploadFileResponse = objectMapper.readValue(responseBody, UploadFileResponse.class);
        assertThat(uploadFileResponse.getSuccessfulRecords()).isEqualTo(2);
        assertThat(uploadFileResponse.getFailedRecords()).isEqualTo(1);

        verify(courseService, times(2)).createCourse(any(CourseDto.class));
    }
}