package recipes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @NotNull
    @NotBlank
    @Pattern(regexp = ".+@.+\\..+")
    @Email(message = "Email is invalid. Please check!")
    String email;
    @NotBlank
    @Size(min = 8, message = "Passwords must be at least 8 characters")
    @Column(name = "user_password")
    String password;
    @JsonIgnore
    @Column(name = "user_role")
    String role;
}
