package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {
    private final String ORDER_URL = "/api/orders";
    private final Map<String, List<String>> body = new HashMap<>();


    @Step ("Create order by authorized user")
    public ValidatableResponse createOrder (String accessToken, List<String> ingredients){
        body.put("ingredients", ingredients);
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(body).log().all()
                .when()
                .post(ORDER_URL)
                .then().log().all();
    }

    @Step ("Create order by non-authorized user")
    public ValidatableResponse createOrderNonAuthUser (List<String> ingredients){
        body.put("ingredients", ingredients);
        return given()
                .spec(getBaseSpec())
                .body(body).log().all()
                .when()
                .post(ORDER_URL)
                .then().log().all();
    }

    @Step ("Create order by authorized user without ingredients")
    public ValidatableResponse createOrderWithoutIngredients (String accessToken){
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .log().all()
                .when()
                .post(ORDER_URL)
                .then().log().all();
    }

    @Step ("Get authorized user's orders")
    public ValidatableResponse getAuthUserOrders (String accessToken){
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .log().all()
                .when()
                .get(ORDER_URL)
                .then().log().all();
    }

    @Step ("Get non-authorized user's orders")
    public ValidatableResponse getOrdersNonAuthorizedUser (){
        return given()
                .spec(getBaseSpec()).log().all()
                .when()
                .get(ORDER_URL)
                .then().log().all();
    }
}
