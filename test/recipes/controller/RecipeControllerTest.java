package recipes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import recipes.config.SecurityConfiguration;
import recipes.dto.RecipeDto;
import recipes.exception.NotFoundException;
import recipes.service.RecipeService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecipeController.class)
@Import(SecurityConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeControllerTest {

    @MockBean
    RecipeService recipeService;

    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private final RecipeDto recipeDto = new RecipeDto(
            "cream",
            "Bakery",
            "--", "--",
            List.of("---", "-", "-"),
            List.of("---", "-", "-")
    );

    private final RecipeDto recipeEmptyName = new RecipeDto(
            "",
            "Beverage",
            "--", "--",
            List.of("---", "-", "-"),
            List.of("---", "-", "-")
    );


    @Test
    @Order(1)
    @WithMockUser
    @DisplayName("TC1.1 - 200 OK. The recipe has been successfully posted.")
    void shouldPostRecipe_whenValid_then200OK() throws Exception {
        this.mvc.perform(post("/api/recipe/new")
                        .content(mapper.writeValueAsString(recipeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @Order(5)
    @WithMockUser
    @DisplayName("TC1.2 - 400 BadRequest. The recipe with an invalid input.")
    void shouldNotPostRecipe_whenInvalid_then400() throws Exception {
        this.mvc.perform(post("/api/recipe/new")
                        .content(mapper.writeValueAsString(new RecipeDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    @DisplayName("TC1.3 - 401 Unauthorized. The user is unauthorized.")
    void shouldNotPostRecipe_whenUnauthorizedAccess_then401() throws Exception {
        this.mvc.perform(post("/api/recipe/new")
                        .content(mapper.writeValueAsString(recipeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @WithMockUser
    @DisplayName("TC1.4 - 400 BadRequest. The recipe with an invalid name.")
    void shouldNotPostRecipe_whenInvalidName_then400() throws Exception {

        this.mvc.perform(post("/api/recipe/new")
                        .content(mapper.writeValueAsString(recipeEmptyName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    @WithMockUser
    @DisplayName("TC2.1 - 200 OK. Get the recipe by id.")
    void shouldGetRecipeById_whenFound_then200OK() throws Exception {
        Long id = 1L;
        when(recipeService.existsById(id)).thenReturn(true);
        when(recipeService.getRecipeById(id)).thenReturn(recipeDto);

        this.mvc.perform(get("/api/recipe/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(recipeDto.getName()))
                .andExpect(jsonPath("$.category").value(recipeDto.getCategory()))
                .andExpect(jsonPath("$.date").value(recipeDto.getDate()))
                .andExpect(jsonPath("$.description").value(recipeDto.getDescription()))
                .andExpect(jsonPath("$.ingredients").isNotEmpty())
                .andExpect(jsonPath("$.directions").isNotEmpty())
        ;
    }

    @Test
    @Order(8)
    @WithMockUser
    @DisplayName("TC2.2 - 404 Not Found. The recipe hasn't been found by id.")
    void shouldNotGetRecipeById_whenNotFound_thenThrownException() throws Exception {
        Long id = 1L;

        when(recipeService.existsById(id)).thenReturn(false);
        when(recipeService.getRecipeById(id)).thenThrow(NotFoundException.class);

        this.mvc.perform(get("/api/recipe/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "AUTHOR")
    @DisplayName("TC3.1 - 204 OK. The recipe has been successfully updated by author.")
    void shouldUpdateRecipeById_whenAuthor_then200OK() throws Exception {
        Long id = 1L;

        this.mvc.perform(put("/api/recipe/{id}", id)
                        .content(mapper.writeValueAsString(recipeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(9)
    @WithMockUser
    @DisplayName("TC3.3 - 403 Forbidden. The recipe isn't updated by a non-author.")
    void shouldNotUpdateRecipeById_whenNotAuthor_then403Forbidden() throws Exception {

        Long id = 1L;

        this.mvc.perform(put("/api/recipe/{id}", id)
                        .content(mapper.writeValueAsString(recipeDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "AUTHOR")
    @DisplayName("TC3.4 - 400 Bad Request. A new recipe has an invalid field.")
    void shouldNotUpdateRecipeById_whenInvalidUpdName_then400() throws Exception {
        Long id = 1L;

        RecipeDto recipeUpdate = recipeDto;
        recipeUpdate.setName("");

        this.mvc.perform(put("/api/recipe/{id}", id)
                        .content(mapper.writeValueAsString(recipeUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @WithMockUser
    @DisplayName("TC4.1 - 200 OK. The list of filtered recipes has been shown.")
    void shouldFindAllFilteredRecipes_whenValidParam_then200OK() throws Exception {
        String text = "cream";

        when(recipeService.getAllRecipes(anyString(), eq(null))).thenReturn(List.of(recipeDto));

        this.mvc.perform(get("/api/recipe/search").param("name", text))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(11)
    @WithMockUser
    @DisplayName("TC4.2 - 400 Bad Request. There is no request params.")
    void shouldNotFindFilteredRecipes_whenNoParam_then400() throws Exception {

        this.mvc.perform(get("/api/recipe/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    @WithMockUser
    @DisplayName("TC4.3 - 400 Bad Request. The recipes can be filtered only by 1 param.")
    void shouldNotFindFilteredRecipes_whenTwoParam_then400() throws Exception {

        String name = "cream";
        String category = "Bakery";
        when(recipeService.getAllRecipes(anyString(), anyString())).thenReturn(List.of(recipeDto));

        this.mvc.perform(get("/api/recipe/search")
                        .param("name", name)
                        .param("category", category))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "AUTHOR")
    @Order(13)
    @DisplayName("TC5.1 - 200 OK. The recipe has been successfully deleted by an author.")
    void shouldDeleteRecipeById_whenAuthor_then200OK() throws Exception {
        Long id = 1L;

        this.mvc.perform(delete("/api/recipe/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @Order(14)
    @DisplayName("TC5.2 - 403 Forbidden. The recipe isn't deleted by a non-author.")
    void shouldNotDeleteRecipeById_whenNonAuthor_then403() throws Exception {
        Long id = 1L;

        this.mvc.perform(delete("/api/recipe/{id}", id))
                .andExpect(status().isForbidden());
    }
}