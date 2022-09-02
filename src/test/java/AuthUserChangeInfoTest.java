import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class AuthUserChangeInfoTest {

    private static User user;
    private UserClient userClient;
    String accessToken;

    @Before
    public void startUp() {
        user = User.getRandomFullRegInfo();
        userClient = new UserClient();
        userClient.createUser(user);
        ValidatableResponse login = userClient.loginUser(new User(user.email, user.password, user.name));
        accessToken = login.extract().path("accessToken");

    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Checkout: authorized user can change email and successfully login with it")
    @Description("Checkout: status code=200")
    public void userChangeEmailTest() {
        String newEmail = User.getRandomEmail().email.toLowerCase();
        ValidatableResponse response = userClient.changeUserInfo(accessToken, new User(newEmail, user.password, user.name));
        int actualStatusCode = response.extract().statusCode();
        boolean isUserEmailChanged = response.extract().path("success");
        ValidatableResponse loginWithNewEmail = userClient.loginUser(new User(newEmail, user.password, user.name));
        int actualStatusCodeForLoginWithNewEmail = loginWithNewEmail.extract().statusCode();

        assertEquals("Status-code should be200", SC_OK, actualStatusCode);
        assertTrue("Email should be changed", isUserEmailChanged);
        assertEquals("Status code when user login should be 200", SC_OK, actualStatusCodeForLoginWithNewEmail);
    }

    @Test
    @DisplayName("Checkout: authorized user can change the password and successfully login with it")
    @Description("Checkout: status code=200")
    public void userChangePasswordTest() {
        String newPassword = User.getRandomPassword().password;
        ValidatableResponse response = userClient.changeUserInfo(accessToken, new User(user.email, newPassword, user.name));
        int actualStatusCode = response.extract().statusCode();
        boolean isUserPasswordChanged = response.extract().path("success");
        ValidatableResponse loginWithNewPass = userClient.loginUser(new User(user.email, newPassword, user.name));
        int actualStatusCodeForLoginWithNewPass = loginWithNewPass.extract().statusCode();

        assertEquals("The status code when changing the password should be 200", SC_OK, actualStatusCode);
        assertTrue("Password should be changed", isUserPasswordChanged);
        assertEquals("The status code when user login with new password should be 200", SC_OK, actualStatusCodeForLoginWithNewPass);
    }

    @Test
    @DisplayName("Checkout: authorized user can change name")
    @Description("Checkout: status code=200")
    public void userChangeNameTest() {
        String oldName = user.name;
        String newName = User.getRandomName().name.toLowerCase();
        ValidatableResponse response = userClient.changeUserInfo(accessToken, new User(user.email, user.password, newName));
        int actualStatusCode = response.extract().statusCode();
        boolean isUserNameChanged = response.extract().path("success");
        HashMap<String, String> userWithNewName = userClient.getUserInfo(accessToken).extract().path("user");

        assertEquals("Status code should be 200", SC_OK, actualStatusCode);
        assertTrue("Name should be changed", isUserNameChanged);
        assertNotEquals("New name should be different from the previous one", oldName, userWithNewName.get("name").toLowerCase());
    }
}
