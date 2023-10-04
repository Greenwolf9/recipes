package recipes.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import recipes.model.Recipe;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeRepositoryTest {

    @Autowired
    RecipeRepository recipeRepository;

    private Recipe iceCream;

    @BeforeAll
    void setUp() {

        iceCream = Recipe.builder()
                .name("Ice-Cream")
                .category("dessert")
                .description("---")
                .directions(List.of("---", "---", "---"))
                .ingredients(List.of("---", "---", "---"))
                .build();
        recipeRepository.save(iceCream);
    }

    @Test
    @Rollback(value = false)
    @DisplayName("TC1 - The recipe has been successfully saved in DB.")
    @Order(1)
    void whenSaved_thenFindsRecipeInDB() {
        Recipe tea = Recipe.builder()
                .name("Fresh Mint Tea")
                .category("beverage")
                .description("---")
                .directions(List.of("---", "---", "---"))
                .ingredients(List.of("---", "---", "---"))
                .build();

        recipeRepository.save(tea);

        final Recipe recipe = recipeRepository.getById(tea.getId());

        assertNotNull(recipe);
        assertNotNull(recipe.getId());
        assertAll(
                ()-> assertThat(recipe).hasFieldOrProperty("id"),
                ()-> assertThat(recipe).hasFieldOrProperty("name"),
                ()-> assertThat(recipe).hasFieldOrProperty("category"),
                ()-> assertThat(recipe).hasFieldOrProperty("date"),
                ()-> assertThat(recipe).hasFieldOrProperty("ingredients"),
                ()-> assertThat(recipe).hasFieldOrProperty("directions")
        );
    }

    @Test
    @DisplayName("TC2 - The recipe has been successfully found by id")
    @Order(2)
    void shouldGetRecipeById() {
        final Recipe recipe = recipeRepository.getById(1L);

        assertThat(recipe).isNotNull();
        assertThat(recipe.getId()).isEqualTo(1L);
    }

    @Test
    @Rollback(value = false)
    @DisplayName("TC3 - The recipe has been successfully updated in DB.")
    @Order(3)
    void shouldUpdateRecipeInDb() {
        Recipe recipe = recipeRepository.getById(1L);
        recipe.setName("Fresh Chamomile Tea");

        Recipe updatedRecipe = recipeRepository.save(recipe);

        assertEquals("Fresh Chamomile Tea", updatedRecipe.getName());
        assertEquals(recipe.getId(), updatedRecipe.getId());
    }

    @ParameterizedTest
    @DisplayName("TC4 - All recipes are successfully found by a category.")
    @Order(4)
    @ValueSource(strings = {"dessert", "beverage"})
    void findsAllRecipesByCategory(String param) {
        List<Recipe> listOfRecipes = recipeRepository.getAllByCategoryIgnoreCaseOrderByDateDesc(param);

        Assertions.assertEquals(1, listOfRecipes.size());
    }

    @ParameterizedTest
    @DisplayName("TC5 - All recipes are successfully found by a name.")
    @Order(5)
    @ValueSource(strings = {"Fresh Chamomile Tea"})
    void findsAllRecipesByName(String param) {
        List<Recipe> listOfRecipes = recipeRepository.getAllByNameContainingIgnoreCaseOrderByDateDesc(param);

        Assertions.assertEquals(1, listOfRecipes.size());
    }

    @Test
    @Order(6)
    @DisplayName("TC6 - The recipe is successfully deleted.")
    void whenDeleted_thenRecipeNotFoundById() {

        recipeRepository.deleteById(1L);
        final Optional<Recipe> recipe = recipeRepository.findById(1L);

        Assertions.assertFalse(recipe.isPresent());
    }


}