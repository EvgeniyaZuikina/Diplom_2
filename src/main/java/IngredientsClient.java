import static io.restassured.RestAssured.given;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

public class IngredientsClient extends RestAssuredClient {
    private static final String INGREDIENTS_URL = "/api/ingredients";

    @Step("Get ingredients data")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_URL)
                .then().log().all();
    }
}
