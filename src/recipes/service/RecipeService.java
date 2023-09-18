package recipes.service;

import recipes.dto.RecipeDto;
import recipes.exception.NotFoundException;

import java.util.List;

public interface RecipeService {

    Long saveRecipe(RecipeDto recipeDto, String email);

    void updateRecipe(Long id, RecipeDto recipe) throws NotFoundException;

    boolean existsById(Long id);

    RecipeDto getRecipeById(Long id) throws NotFoundException;

    void deleteRecipeById(Long id) throws NotFoundException;

    List<RecipeDto> getAllRecipes(String name, String category);
}
