package recipes.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;
    @Column(name = "recipe_name")
    String name;
    @Column(name = "category")
    String category;
    @Column(name = "recipe_date")
    LocalDateTime date = LocalDateTime.now();
    @Column(name = "description")
    String description;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient", nullable = false)
    List<String> ingredients = new ArrayList<>();

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "directions", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "direction", nullable = false)
    List<String> directions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_email")
    User author;

}


