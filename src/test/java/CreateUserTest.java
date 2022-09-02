import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class CreateUserTest {
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
    @DisplayName("Checkout: user should be created")
    @Description("Checkout creating user")
    public void userCreatedTest (){
        ValidatableResponse response = userClient.createUser(user);
        int actualStatusCode = response.extract().statusCode();
        boolean isUserCreated = response.extract().path("success");
        accessToken = response.extract().path("accessToken");
        refreshToken = response.extract().path("refreshToken");

        assertEquals ("Status code should be 200", SC_OK, actualStatusCode);
        assertTrue ("User should be created", isUserCreated);
        assertNotNull("User data shouldn't be empty", response.extract().path("user"));
        assertNotNull("accessToken shouldn't be empty", accessToken);
        assertNotNull("refreshToken shouldn't be empty", refreshToken);
    }
    @Test
    @DisplayName("Checkout: Duplicate user shouldn't be created")
    @Description("Checkout: status code=403, message='User already exists'")
    public void userDuplicateTest (){
        final String expectedMessage = "User already exists";
        ValidatableResponse newUserResponse = userClient.createUser(user);
        accessToken = newUserResponse.extract().path("accessToken");

        ValidatableResponse doubleUserResponse = userClient.createUser(user);

        int actualStatusCode = doubleUserResponse.extract().statusCode();
        String actualMessage = doubleUserResponse.extract().path("message");
        boolean isUserCreated = doubleUserResponse.extract().path("success");

        assertEquals ("Status code should be 403", SC_FORBIDDEN, actualStatusCode);
        assertFalse ("Duplicate user shouldn't be created", isUserCreated);
        assertEquals("Incorrect error message when trying create duplicate user" , expectedMessage, actualMessage);
    }
}
