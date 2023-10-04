
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import recipes.RecipesApplication;


@SpringBootTest(classes = RecipesApplication.class)
@AutoConfigureMockMvc
public class RecipesApplicationTest {

    @Test
    public void main() {
        RecipesApplication.main(new String[]{});
    }
}