package recipes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import recipes.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> getAllByCategoryIgnoreCaseOrderByDateDesc(String category);

    List<Recipe> getAllByNameContainingIgnoreCaseOrderByDateDesc(String name);
}
