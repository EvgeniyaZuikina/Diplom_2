import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class UserLoginTest {
    private UserClient userClient;

    private User user;
    String accessToken;
    String refreshToken;

    @Before
    public void startUp() {
        user = User.getRandomFullRegInfo();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Checkout: existing user can login")
    @Description("Checkout: status code=200, user token is not empty")

    public void userSuccessLogin() {
        userClient.createUser(user);
        ValidatableResponse login = userClient.loginUser(new User(user.email, user.password, user.name));

        boolean isUserLogin = login.extract().path("success");
        int actualStatusCode = login.extract().statusCode();
        accessToken = login.extract().path("accessToken");
        refreshToken = login.extract().path("refreshToken");

        assertEquals("Status code should be 200", SC_OK, actualStatusCode);
        assertTrue ("User must login", isUserLogin);

        assertNotNull("User data shouldn't be empty", login.extract().path("user"));
        assertNotNull("accessToken shouldn't be empty", accessToken);
        assertNotNull("refreshToken shouldn't be empty", refreshToken);
    }
}
