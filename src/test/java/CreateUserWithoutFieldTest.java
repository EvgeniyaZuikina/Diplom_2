import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CreateUserWithoutFieldTest {

    private final User user;
    public CreateUserWithoutFieldTest(User user) {
        this.user = user;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][] {
                {User.getRandomPasswordAndEmail()},
                {User.getRandomPasswordAndName()},
                {User.getRandomNameAndEmail()}
        };
    }

    @Test
    @DisplayName("Validation check on user registration")
    @Description("1. Registration without field name"
            + "\n"+ "2. Registration without field email"
            + "\n"+ "3. Registration without field password")
    public void userWithoutFieldIsNotCreateTest() {

        final String expectedMessage = "Email, password and name are required fields";
        UserClient userClient = new UserClient();
        ValidatableResponse response = userClient.createUser(user);
        String accessToken = response.extract().path("accessToken");
        String actualMessage = response.extract().path("message");
        int actualStatus = response.extract().statusCode();
        boolean isUserReg = response.extract().path("success");

        if (isUserReg) { userClient.delete(accessToken);
        }

        assertEquals("Status code should be 403", SC_FORBIDDEN, actualStatus);
        assertFalse ("User shouldn't register", isUserReg);
        assertEquals("Incorrect error message", expectedMessage, actualMessage);
    }
}
