import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class GetUserOrderInfoTest {
    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private IngredientsClient ingredientsClient;
    String accessToken;

    @Before
    public void startUp() {
        user = User.getRandomFullRegInfo();
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredientsClient = new IngredientsClient ();
    }
    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Checkout: authorized user's orders could be received")
    @Description("Checkout: status code=200")
    public void authUserOrdersInfoCanBeReceivedTest (){
        userClient.createUser(user);
        ValidatableResponse login = userClient.loginUser(new User(user.email, user.password, user.name));
        accessToken = login.extract().path("accessToken");
        ValidatableResponse allIngredients = ingredientsClient.getIngredients();
        List<String> ingredientsForBurger = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            ingredientsForBurger.add(allIngredients.extract().path("data["+ i + "]._id"));
        }

        orderClient.createOrder(accessToken, ingredientsForBurger);
        ValidatableResponse response = orderClient.getAuthUserOrders(accessToken);
        int actualStatusCode = response.extract().statusCode();
        boolean isInfoGot = response.extract().path("success");
        List<Map<String, Object>> authUserOrders = response.extract().path("orders");

        assertEquals("Status code should be 200",  SC_OK, actualStatusCode);
        assertTrue ("Information about the user's orders must be received", isInfoGot);
        assertNotNull("User's orders list shouldn't be empty", authUserOrders);
    }

    @Test
    @DisplayName("Checkout: unauthorized user's orders couldn't be received")
    @Description("Checkout: status code=401")
    public void nonAuthUserOrdersInfoCanNotBeReceivedTest (){
        ValidatableResponse response = orderClient.getOrdersNonAuthorizedUser();
        int actualStatusCode = response.extract().statusCode();
        boolean isInfoGot = response.extract().path("success");

        assertEquals("Status code should be 401", SC_UNAUTHORIZED, actualStatusCode);
        assertFalse ("Information about the unauthorized user's orders mustn't be received", isInfoGot);
    }
}
