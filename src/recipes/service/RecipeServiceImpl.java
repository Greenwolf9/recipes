package recipes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recipes.dto.RecipeDto;
import recipes.dto.RecipeMapper;
import recipes.exception.NotFoundException;
import recipes.model.Recipe;
import recipes.model.User;
import recipes.repository.RecipeRepository;
import recipes.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository repository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Long saveRecipe(RecipeDto recipeDto, String email) {
        final User user = userRepository.findById(email).orElseThrow();
        user.setRole("AUTHOR");
        Recipe recipe = RecipeMapper.convertDtoToRecipe(recipeDto);
        recipe.setAuthor(user);
        repository.save(recipe);
        return recipe.getId();
    }

    @Override
    public void updateRecipe(Long id, RecipeDto recipe) throws NotFoundException {
        final Recipe recipeToBeUpdated = repository.findById(id).orElseThrow(() -> new NotFoundException("Recipe not found"));
        if (recipe.getName() != null) {
            recipeToBeUpdated.setName(recipe.getName());
        }
        if (recipe.getCategory() != null) {
            recipeToBeUpdated.setCategory(recipe.getCategory());
        }
        if (recipe.getDescription() != null) {
            recipeToBeUpdated.setDescription(recipe.getDescription());
        }
        if (recipe.getIngredients() != null) {
            recipeToBeUpdated.setIngredients(recipe.getIngredients());
        }
        if (recipe.getDirections() != null) {
            recipeToBeUpdated.setDirections(recipe.getDirections());
        }
        recipeToBeUpdated.setDate(LocalDateTime.now());
        repository.save(recipeToBeUpdated);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public RecipeDto getRecipeById(Long id) throws NotFoundException {
        final Recipe recipe = repository.findById(id).orElseThrow(() -> new NotFoundException("Recipe not found"));
        return RecipeMapper.convertRecipeToDto(recipe);
    }

    @Override
    public void deleteRecipeById(Long id) throws NotFoundException {
        if (existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException("Recipe not found");
        }
    }

    @Override
    public List<RecipeDto> getAllRecipes(String name, String category) {
        return name != null ?
                repository.getAllByNameContainingIgnoreCaseOrderByDateDesc(name)
                        .stream()
                        .map(RecipeMapper::convertRecipeToDto)
                        .collect(Collectors.toList())
                :
                repository.getAllByCategoryIgnoreCaseOrderByDateDesc(category)
                        .stream()
                        .map(RecipeMapper::convertRecipeToDto)
                        .collect(Collectors.toList());
    }
}
