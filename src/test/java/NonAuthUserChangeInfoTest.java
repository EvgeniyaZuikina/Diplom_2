import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class NonAuthUserChangeInfoTest {
    private static User user;
    private UserClient userClient;
    String accessToken;

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
    @DisplayName("Checkout: Non-authorized user can't change email")
    @Description("Checkout: status code=401")
    public void notChangeEmailTest (){
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        String newEmail = User.getRandomEmail().email.toLowerCase();

        ValidatableResponse changeEmail = userClient.changeUserInfoNoToken(new User(newEmail, user.password, user.name));
        int actualStatusCode = changeEmail.extract().statusCode();
        boolean isUserEmailChanged = changeEmail.extract().path("success");

        assertEquals("Status code should be 401", SC_UNAUTHORIZED, actualStatusCode);
        assertFalse ("Email shouldn't be changed", isUserEmailChanged);
    }

    @Test
    @DisplayName("Checkout: Non-authorized user can't change password")
    @Description("Checkout: status code=401")
    public void notChangePasswordTest (){
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        String newPassword= User.getRandomPassword().password;

        ValidatableResponse changePass = userClient.changeUserInfoNoToken(new User(user.email, newPassword, user.name));
        int actualStatusCode = changePass.extract().statusCode();
        boolean isUserPasswordChanged = changePass.extract().path("success");

        assertEquals("Status code should be 401",  SC_UNAUTHORIZED, actualStatusCode);
        assertFalse ("Password shouldn't be changed", isUserPasswordChanged);
    }

    @Test
    @DisplayName("Checkout: Non-authorized user can't change name")
    @Description("Checkout: status code=401")
    public void notChangeNameTest (){
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        String newName = User.getRandomName().name;

        ValidatableResponse changeName = userClient.changeUserInfoNoToken(new User(user.email, user.password, newName));
        int actualStatusCode = changeName.extract().statusCode();
        boolean isUserNameChanged = changeName.extract().path("success");

        assertEquals("Status code should be 401",  SC_UNAUTHORIZED, actualStatusCode);
        assertFalse ("Name shouldn't be changed", isUserNameChanged);
    }
}
