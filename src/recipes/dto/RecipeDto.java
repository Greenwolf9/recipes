package recipes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    @NotBlank(message = "Name field can't be empty.")
    @JsonProperty(value = "name")
    String name;
    @NotBlank(message = "Category field can't be empty.")
    @JsonProperty(value = "category")
    String category;
    String date;
    @NotBlank(message = "Description field can't be empty.")
    @JsonProperty(value = "description")
    String description;
    @Size(min = 1, message = "List of ingredients should contain at least 1 item")
    @NotNull
    @JsonProperty(value = "ingredients")
    List<String> ingredients;
    @NotNull
    @Size(min = 1, message = "List of directions should contain at least 1 step")
    @JsonProperty(value = "directions")
    List<String> directions;
}
