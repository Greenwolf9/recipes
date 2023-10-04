package recipes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import recipes.config.SecurityConfiguration;
import recipes.model.User;

import recipes.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @MockBean
    UserServiceImpl userService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final User user = new User("mockingbird@gmail.com", "mockingbird", "");
    private final User userInvalidEmail = new User("mockingbirdgmail.com", "mockingbird", "");
    private final User userNotUniqueEmail = new User("mockingbird@gmail.com", "mockingbird", "");

    @Test
    @Order(1)
    @DisplayName(value = "TC1 - 200 OK. The user has been successfully registered.")
    void shouldRegisterUser() throws Exception {

        mvc.perform(post("/api/register").content(mapper.writeValueAsString(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName(value = "TC2 - 400 BadRequest. The user with an invalid email.")
    void shouldNotRegisterUserWithInvalidEmail() throws Exception {

        mvc.perform(post("/api/register").content(mapper.writeValueAsString(userInvalidEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName(value = "TC3 - 400 BadRequest. The user with a non-unique email.")
    void shouldNotRegisterUserWithNotUniqueEmail() throws Exception {
        when(userService.existByEmail("mockingbird@gmail.com")).thenReturn(true);

        mvc.perform(post("/api/register").content(mapper.writeValueAsString(userNotUniqueEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}