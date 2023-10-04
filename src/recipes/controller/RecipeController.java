package recipes.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import recipes.dto.RecipeDto;
import recipes.exception.NotFoundException;
import recipes.service.RecipeService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/recipe")
@Validated
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> postRecipe(@Valid @RequestBody RecipeDto recipeDto, Authentication authentication) {
        if (recipeDto == null) {
            return ResponseEntity.badRequest().build();
        }
        String email = authentication.getName();
        log.info("POST recipe - '{}'", recipeDto.getName());
        return new ResponseEntity<>(Map.of("id", recipeService.saveRecipe(recipeDto, email)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipe(@PathVariable("id") Long id) throws NotFoundException {
        if (!recipeService.existsById(id)) {
            log.info("Recipe {} not found ", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Get Recipe {} ", id);
        return ResponseEntity.ok().body(recipeService.getRecipeById(id));
    }
    @PreAuthorize("hasRole('AUTHOR')")
    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> updateRecipeById(@PathVariable("id") Long id, @Valid @RequestBody RecipeDto recipe)
            throws NotFoundException {

        log.info("Put: Update Recipe {} ", id);
        recipeService.updateRecipe(id, recipe);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeDto>> findAllFilteredRecipes(@RequestParam(value = "name", required = false) String name,
                                                                  @RequestParam(value = "category", required = false) String category) {
        if ((name != null && category != null) || (name == null && category == null)) {
            log.error("Search should be performed only by 1 parameter.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Recipes are filtered by name or category.");
        return ResponseEntity.ok().body(recipeService.getAllRecipes(name, category));
    }
    @PreAuthorize("hasRole('AUTHOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipeById(@PathVariable("id") @NotNull Long id) throws NotFoundException {
        recipeService.deleteRecipeById(id);
        log.info("Recipe {} deleted ", id);
        return ResponseEntity.noContent().build();
    }
}


