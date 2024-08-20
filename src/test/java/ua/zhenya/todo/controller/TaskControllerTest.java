package ua.zhenya.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.zhenya.todo.config.TestConfig;
import ua.zhenya.todo.dto.task.TaskResponse;
import ua.zhenya.todo.mappers.TaskMapper;
import ua.zhenya.todo.model.Task;
import ua.zhenya.todo.model.User;
import ua.zhenya.todo.motherObjects.TestEntityFactory;
import ua.zhenya.todo.project.BaseProject;
import ua.zhenya.todo.security.JwtUserDetails;
import ua.zhenya.todo.security.expression.CustomSecurityExpression;
import ua.zhenya.todo.service.TaskService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(TestConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

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
    @WithMockUser(username = "testUser")
    void findAll_shouldReturnPageOfTasks() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        User user = TestEntityFactory.createDefaultUser();
        BaseProject baseProject = TestEntityFactory.createDefaultProject(user);
        Task task = TestEntityFactory.createDefaultTask(user, baseProject);
        TaskResponse taskResponse = TestEntityFactory.createTaskResponse(task);

        // Мокируем возвращаемое значение taskService.findAll для возврата Page<Task>
        Page<Task> tasksPage = new PageImpl<>(Collections.singletonList(task), pageable, 1);

        Mockito.when(taskService.findAll(eq(jwtUserDetails.getId()), any(Pageable.class)))
                .thenReturn(tasksPage);

        // Настраиваем маппер для преобразования Task в TaskResponse
        Mockito.when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void findById_shouldReturnTaskResponse() throws Exception {
        // Создаём тестовые данные
        Integer taskId = 1;
        User user = TestEntityFactory.createDefaultUser();
        BaseProject baseProject = TestEntityFactory.createDefaultProject(user);
        Task task = TestEntityFactory.createDefaultTask(user, baseProject);
        task.setId(taskId); // Убедитесь, что ID задачи задан

        TaskResponse taskResponse = TestEntityFactory.createTaskResponse(task);

        // Настраиваем моки для taskService и taskMapper
        Mockito.when(taskService.findById(taskId)).thenReturn(task);
        Mockito.when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        // Настраиваем мок для customSecurityExpression
        CustomSecurityExpression customSecurityExpression = Mockito.mock(CustomSecurityExpression.class);
        Mockito.when(customSecurityExpression.canAccessTask(taskId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value(task.getName()));
    }
}