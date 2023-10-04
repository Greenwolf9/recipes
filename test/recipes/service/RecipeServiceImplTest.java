package recipes.service;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import recipes.dto.RecipeDto;
import recipes.dto.RecipeMapper;
import recipes.exception.NotFoundException;
import recipes.model.Recipe;
import recipes.model.User;
import recipes.repository.RecipeRepository;
import recipes.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RecipeRepository recipeRepository;
    RecipeServiceImpl recipeService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User author;

    private Recipe recipe;

    @BeforeAll
    public void init() {
        MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository, userRepository);

        author = User.builder()
                .email("mockingbird@gmail.com")
                .password(passwordEncoder.encode("tokillamockingbird"))
                .role("").build();

        recipe = Recipe.builder()
                .name("cream")
                .category("Bakery")
                .description("--")
                .ingredients(List.of("---", "-", "-"))
                .directions(List.of("---", "-", "-"))
                .author(author)
                .build();
    }

    @Test
    @DisplayName("TC1.1 - The recipe has been successfully saved.")
    @Order(1)
    void saveRecipe_whenRecipeValid_thenSaved() {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(author));

        recipeService.saveRecipe(RecipeMapper.convertRecipeToDto(recipe), author.getEmail());

        assertThat(author.getRole()).isEqualTo("AUTHOR");
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    @DisplayName("TC2.1 - The recipe has been found by id and retrieved.")
    @Order(2)
    void shouldRetrieveRecipeById() throws NotFoundException {
        Long id = 1L;
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        RecipeDto actualRecipe = recipeService.getRecipeById(id);

        assertThat(actualRecipe).isEqualTo(RecipeMapper.convertRecipeToDto(recipe));
    }

    @Test
    @Order(3)
    @DisplayName("TC2.2 - The recipe hasn't been found by id.")
    void shouldNotRetrieveRecipeById_whenNotFound_thenThrownException() {
        Long id = 1L;
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> recipeService.getRecipeById(id)
        );

        assertThat(notFoundException.getMessage()).isEqualTo("Recipe not found");
    }

    @Test
    @Order(4)
    @DisplayName("TC3.1 - The list of filtered recipes has been successfully retrieved")
    void shouldRetrieveListFilteredRecipes() {
        String name = "cream";
        when(recipeRepository.getAllByNameContainingIgnoreCaseOrderByDateDesc(name)).thenReturn(List.of(recipe));

        List<RecipeDto> filteredByName = recipeService.getAllRecipes(name, null);

        assertThat(filteredByName.get(0)).isEqualTo(RecipeMapper.convertRecipeToDto(recipe));
    }

    @Test
    @Order(5)
    @DisplayName("TC4.1 - The recipe has been successfully updated by id")
    void shouldUpdateRecipeById_whenFound_thenUpdated() throws NotFoundException {
        Long id = 1L;

        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
        Recipe recipeUpd = recipe;
        recipeUpd.setName("dessert");

        recipeService.updateRecipe(id, RecipeMapper.convertRecipeToDto(recipeUpd));

        verify(recipeRepository, times(1)).save(recipeUpd);
        assertThat(recipeUpd.getId()).isEqualTo(recipe.getId());
        assertThat(recipeUpd.getName()).isEqualTo("dessert");
    }

    @Test
    @Order(6)
    @DisplayName("TC5.1 - The recipe has been successfully deleted by id")
    void shouldDeleteRecipeById_whenFoundById_thenDeleted() throws NotFoundException {
        Long id = 1L;
        when(recipeRepository.existsById(id)).thenReturn(true);
        doNothing().when(recipeRepository).deleteById(id);

        recipeService.deleteRecipeById(id);
        verify(recipeRepository, times(1)).deleteById(id);
    }

    @Test
    @Order(7)
    @DisplayName("TC5.2 - The recipe hasn't been deleted by id. Not found by id.")
    void shouldNotDeleteRecipeById_whenNotFoundById_thenThrownException() {
        Long id = 1L;
        when(recipeRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> recipeService.deleteRecipeById(id));
    }

}