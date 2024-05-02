package com.task2.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.task2.model.dto.InstructorDto;
import com.task2.service.InstructorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
public class InstructorControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private InstructorService instructorService;

    private MockMvc mockMvc;
    private InstructorDto instructorDto;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        instructorDto = new InstructorDto();
        instructorDto.setFirstName("John");
        instructorDto.setLastName("Doe");
        instructorDto.setEmail("instructor89@gmail.com");
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetAllInstructors() throws Exception {
        PageImpl<InstructorDto> instructorDtos = new PageImpl<>(Arrays.asList(new InstructorDto(), new InstructorDto()));
        when(instructorService.getAllInstructors(any(PageRequest.class))).thenReturn(instructorDtos);

        mockMvc.perform(get("/api/instructor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(instructorService, times(1)).getAllInstructors(any(PageRequest.class));
    }

    @Test
    public void testCreateInstructor() throws Exception {
        ArgumentCaptor<InstructorDto> captor = ArgumentCaptor.forClass(InstructorDto.class);

        mockMvc.perform(post("/api/instructor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(instructorDto)))
                .andExpect(status().isCreated());

        verify(instructorService, times(1)).createInstructor(captor.capture());

        InstructorDto capturedDto = captor.getValue();
        assertEquals(instructorDto.getFirstName(), capturedDto.getFirstName());
        assertEquals(instructorDto.getLastName(), capturedDto.getLastName());
        assertEquals(instructorDto.getEmail(), capturedDto.getEmail());
    }

    @Test
    public void testUpdateInstructor() throws Exception {
        Long instructorId = 1L;

        mockMvc.perform(put("/api/instructor/{id}", instructorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(instructorDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<InstructorDto> captor = ArgumentCaptor.forClass(InstructorDto.class);
        verify(instructorService).updateInstructor(eq(instructorId), captor.capture());

        InstructorDto capturedDto = captor.getValue();
        assertEquals(instructorDto.getFirstName(), capturedDto.getFirstName());
        assertEquals(instructorDto.getLastName(), capturedDto.getLastName());
        assertEquals(instructorDto.getEmail(), capturedDto.getEmail());
    }

    @Test
    public void testDeleteInstructor() throws Exception {
        Long instructorId = 1L;

        mockMvc.perform(delete("/api/instructor/{id}", instructorId))
                .andExpect(status().isNoContent());

        verify(instructorService).deleteInstructor(instructorId);
    }
}