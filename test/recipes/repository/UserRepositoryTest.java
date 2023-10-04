package recipes.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import recipes.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Test
    @Order(1)
    @Rollback(value = false)
    @DisplayName("TC1.1 - The user has been successfully saved in DB.")
    void shouldSaveValidUserInDb() {
        final User user = userRepository.save(new User("davidcopperfield@gmail.com",
                passwordEncoder.encode("davidcopperfield"),
                ""));
        log.info("----The user is being saved----");
        assertNotNull(user);
    }

    @Test
    @Order(2)
    @DisplayName("TC2.1 - The registered user has been successfully found in DB.")
    void whenSaved_thenFindsUserByEmail() {
        final User user = userRepository.getById("davidcopperfield@gmail.com");

        Assertions.assertTrue(passwordEncoder.matches("davidcopperfield", user.getPassword()),
                "Bcrypt hash and a raw password don't match.");

        assertThat(user).hasFieldOrProperty("email");
        assertThat(user).hasFieldOrProperty("password");
        assertThat(user).hasFieldOrProperty("role");
    }

    @Test
    @Order(3)
    @DisplayName("TC2.2 - The unregistered user hasn't been found in DB.")
    void shouldNotFoundUnregisteredUserInDB() {
        final Optional<User> user = userRepository.findById("mockingbird@gmail.com");
        assertTrue(user.isEmpty());
    }
}