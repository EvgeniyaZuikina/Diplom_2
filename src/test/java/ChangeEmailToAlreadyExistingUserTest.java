import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ChangeEmailToAlreadyExistingUserTest {

    private static User user;
    private static User secondUser;
    private UserClient userClient;
    String accessToken1;
    String accessToken2;

    @Before
    public void startUp() {
        user = User.getRandomFullRegInfo();
        secondUser = User.getRandomFullRegInfo();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken1);
        userClient.delete(accessToken2);
    }

    @Test
    @DisplayName("Checkout: user's email can't be changed to the existing one")
    @Description("Checkout: status code=403,  message='User with such email already exists'")
    public void cantChangeEmailToExistingUserTest() {
        final String expectedMessage = "User with such email already exists";
        ValidatableResponse regFirstUser = userClient.createUser(user);
        accessToken1 = regFirstUser.extract().path("accessToken");
        ValidatableResponse regSecondUser = userClient.createUser(secondUser);
        accessToken2 = regSecondUser.extract().path("accessToken");
        userClient.loginUser(new User(user.email, user.password, user.name));
        ValidatableResponse response = userClient.changeUserInfo(accessToken1, new User(secondUser.email, user.password, user.name));

        int actualStatusCode = response.extract().statusCode();
        boolean isUserInfoChanged = response.extract().path("success");
        String errorMessage = response.extract().path("message");

        assertEquals("Status code should be 403", actualStatusCode, SC_FORBIDDEN);
        assertFalse("email shouldn't be changed to the existing one", isUserInfoChanged);
        assertEquals("Incorrect error message when trying to change email to the existing one", expectedMessage, errorMessage);

    }
}
