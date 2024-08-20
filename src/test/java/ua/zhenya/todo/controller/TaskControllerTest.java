package ua.zhenya.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.motherObjects.TestEntityFactory;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.security.expression.CustomSecurityExpression;
import ua.zhenya.todo.service.TaskService;

import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @MockBean
    private CustomSecurityExpression customSecurityExpression;

    private JwtUserDetails jwtUserDetails;

    @BeforeEach
    void setUp() {
        jwtUserDetails = new JwtUserDetails(
                1,
                "testName",
                "testUser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(jwtUserDetails, null, jwtUserDetails.getAuthorities()));
    }

    @Test
    void findAll_shouldReturnPageOfTasks() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        User user = TestEntityFactory.createDefaultUser();
        BaseProject baseProject = TestEntityFactory.createDefaultProject(user);
        Task task = TestEntityFactory.createDefaultTask(user, baseProject);
        TaskResponse taskResponse = TestEntityFactory.createTaskResponse(task);

        Page<Task> tasksPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);

        Mockito.when(taskService.findAll(eq(jwtUserDetails.getId()), any(Pageable.class)))
                .thenReturn(tasksPage);

        Mockito.when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void findById_shouldReturnTaskResponse() throws Exception {
        Integer taskId = 1;
        User user = TestEntityFactory.createDefaultUser();
        BaseProject baseProject = TestEntityFactory.createDefaultProject(user);
        Task task = TestEntityFactory.createDefaultTask(user, baseProject);
        task.setId(taskId);

        TaskResponse taskResponse = TestEntityFactory.createTaskResponse(task);

        Mockito.when(taskService.findById(taskId)).thenReturn(task);
        Mockito.when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        Mockito.when(customSecurityExpression.canAccessTask(taskId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value(task.getName()));
    }

    @Test
    void delete_ShouldDeleteTaskAndReturnNoContent() throws Exception {
        Integer taskId = 1;
        User user = TestEntityFactory.createDefaultUser();
        BaseProject baseProject = TestEntityFactory.createDefaultProject(user);
        Task task = TestEntityFactory.createDefaultTask(user, baseProject);
        task.setId(taskId);

        Mockito.when(taskService.findById(taskId)).thenReturn(task);
        Mockito.when(customSecurityExpression.canModifyTask(taskId)).thenReturn(true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).delete(taskId);
    }

    @Test
    void complete_ShouldReturnOkIfUserCanModifyTask() throws Exception {
        Integer taskId = 1;
        User user = TestEntityFactory.createDefaultUser();
        user.setId(1);
        BaseProject baseProject = TestEntityFactory.createDefaultProject(user);
        Task task = TestEntityFactory.createDefaultTask(user, baseProject);
        task.setId(taskId);

        TaskResponse taskResponse = TestEntityFactory.createTaskResponse(task);

        Mockito.when(taskService.findById(taskId)).thenReturn(task);
        Mockito.when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        Mockito.when(customSecurityExpression.canModifyTask(taskId)).thenReturn(true);
        Mockito.when(taskService.complete(taskId)).thenReturn(task);

        mockMvc.perform(patch("/api/v1/tasks/{id}/complete", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId));
    }

    @Test
    @WithMockUser
    void shouldCompleteTask() throws Exception {
        Integer taskId = 1;
        User user = TestEntityFactory.createDefaultUser();
        user.setId(1);
        BaseProject baseProject = TestEntityFactory.createDefaultProject(user);
        Task task = TestEntityFactory.createDefaultTask(user, baseProject);
        task.setId(taskId);

        TaskResponse taskResponse = TestEntityFactory.createTaskResponse(task);

        when(taskService.complete(taskId)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);
        when(customSecurityExpression.canModifyTask(taskId)).thenReturn(true);

        mockMvc.perform(patch("/api/v1/tasks/{id}/complete", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").exists());

        verify(taskService).complete(taskId);
        verify(taskMapper).toResponse(task);
    }
}