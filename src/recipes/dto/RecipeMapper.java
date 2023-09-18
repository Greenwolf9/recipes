package recipes.dto;

import recipes.model.Recipe;

public class RecipeMapper {

    public static RecipeDto convertRecipeToDto(Recipe recipe) {
        return new RecipeDto(recipe.getName(),
                recipe.getCategory(),
                String.valueOf(recipe.getDate()),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getDirections());
    }

    public static Recipe convertDtoToRecipe(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeDto.getName());
        recipe.setCategory(recipeDto.getCategory());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setIngredients(recipeDto.getIngredients());
        recipe.setDirections(recipeDto.getDirections());
        return recipe;
    }
}
