package recipes.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import recipes.model.User;
import recipes.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    User expectedUser;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);

        expectedUser = User.builder()
                .email("mockingbird@gmail.com")
                .password(passwordEncoder.encode("tokillamockingbird"))
                .role("").build();
    }

    @ParameterizedTest(name = "by username")
    @Order(1)
    @DisplayName("TC1.1 - Loading of the authorized users.")
    @ValueSource(strings = "mockingbird@gmail.com")
    void shouldLoadUserDetail(String email) {

        when(userRepository.findById(email)).thenReturn(Optional.of(expectedUser));
        final UserDetails userDetails = userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertThat(userDetails.isAccountNonLocked()).isEqualTo(true);
        assertThat(userDetails.getUsername()).isEqualTo(expectedUser.getEmail());
    }

    @ParameterizedTest(name = "by username")
    @Order(2)
    @DisplayName("TC1.2 - Such username hasn't been found.")
    @ValueSource(strings = "mockingbird@gmail.com")
    void givenUsername_whenNotFound_thenExceptionThrown(String email) {

        when(userRepository.existsById(email)).thenReturn(false);

        UsernameNotFoundException notFoundEx = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(email));

        assertEquals(notFoundEx.getMessage(), "User not found: " + email);
    }
}

