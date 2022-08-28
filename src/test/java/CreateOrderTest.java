import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {
    private UserClient userClient;
    private User user;
    private OrderClient ordersClient;
    private IngredientsClient ingredientsClient;
    String accessToken;

    @Before
    public void startUp() {
        user = User.getRandomFullRegInfo();
        userClient = new UserClient();
        ordersClient = new OrderClient();
        ingredientsClient = new IngredientsClient();
    }
    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Create order by authorized user")
    @Description("Checkout: status code=200")
    public void authUserCanCreateOrderTest (){
        userClient.createUser(user);
        ValidatableResponse login = userClient.loginUser(new User(user.email, user.password, user.name));
        accessToken = login.extract().path("accessToken");
        ValidatableResponse allIngredients = ingredientsClient.getIngredients();
        List<String> ingredientsForBurger = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            ingredientsForBurger.add(allIngredients.extract().path("data["+ i + "]._id"));
        }

        ValidatableResponse response = ordersClient.createOrder(accessToken, ingredientsForBurger);

        int actualStatusCode = response.extract().statusCode();
        boolean isOrderCreated = response.extract().path("success");
        int orderNum = response.extract().path("order.number");

        assertEquals("Status code should be 200",  SC_OK, actualStatusCode);
        assertTrue ("Order should be created", isOrderCreated);
        assertNotNull("Order number should be not null", orderNum);

    }

    @Test
    @DisplayName("Non-authorized user can create order")
    @Description("Checkout: status code=200")
    public void nonAuthUserCanNotCreateOrderTest (){

        ValidatableResponse allIngredients = ingredientsClient.getIngredients();
        List<String> ingredientsForBurger = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            ingredientsForBurger.add(allIngredients.extract().path("data["+ i + "]._id"));
        }

        ValidatableResponse response = ordersClient.createOrderNonAuthUser(ingredientsForBurger);
        int actualStatusCode = response.extract().statusCode();
        boolean isOrderCreated = response.extract().path("success");

        assertEquals("Status code should be 200",  SC_OK, actualStatusCode);
        assertTrue ("Order should be created", isOrderCreated);
    }

    @Test
    @DisplayName("Authorized user can't create order without ingredients")
    @Description("Checkout: status code=400")
    public void authUserCanNotCreateOrderWithoutIngredientsTest (){
        userClient.createUser(user);
        ValidatableResponse login = userClient.loginUser(new User(user.email, user.password, user.name));
        accessToken = login.extract().path("accessToken");

        ValidatableResponse response = ordersClient.createOrderWithoutIngredients(accessToken);
        int actualStatusCode = response.extract().statusCode();
        boolean isOrderCreated = response.extract().path("success");

        assertEquals("Status code should be 400",  SC_BAD_REQUEST, actualStatusCode);
        assertFalse ("Order shouldn't be created", isOrderCreated);
    }

    @Test
    @DisplayName("Authorized user can not create order with incorrect ingredients hash")
    @Description("Checkout: status code=500")
    public void userCanNotCreateOrderWithIncorrectHashTest (){
        userClient.createUser(user);
        ValidatableResponse login = userClient.loginUser(new User(user.email, user.password, user.name));
        accessToken = login.extract().path("accessToken");
        ValidatableResponse allIngredients = ingredientsClient.getIngredients();
        List<String> ingredientsForBurger = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            ingredientsForBurger.add(allIngredients.extract().path("data["+ i + "]._id")+"111");
        }

        ValidatableResponse response = ordersClient.createOrder(accessToken, ingredientsForBurger);
        int actualStatusCode = response.extract().statusCode();
        assertEquals("Status code should be 500",  SC_INTERNAL_SERVER_ERROR, actualStatusCode);
    }
}
