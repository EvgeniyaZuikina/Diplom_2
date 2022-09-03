import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class IncorrectLoginTest {

    private final User user;
    public IncorrectLoginTest(User user) {
        this.user = user;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][] {
                {User.getRandomEmail()},
                {User.getRandomPassword()},
                {User.getRandomPasswordAndEmail()},
        };
    }

    @Test
    @DisplayName("Checkout: User can't login with email only, with password only or with non-register email+password")
    @Description("Checkout: status code=401")
    public void oneFieldOrUnregisteredDataIsNotAllowed() {
        final String expectedMessage = "email or password are incorrect";
        ValidatableResponse login = new UserClient().loginUser(user);
        int actualStatusCode = login.extract().statusCode();
        boolean isUserLogin = login.extract().path("success");
        String actualMessage = login.extract().path("message");

        assertEquals("Status code should be 401", SC_UNAUTHORIZED, actualStatusCode);
        assertFalse ("User shouldn't login", isUserLogin);
        assertEquals("Incorrect error message", expectedMessage, actualMessage);
    }
}
