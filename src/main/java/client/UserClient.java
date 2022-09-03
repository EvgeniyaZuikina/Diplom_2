package client;

import model.User;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssuredClient {
    private static final String REG_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String USER_INFO_URL = "/api/auth/user";

    @Step("Registration new user")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user).log().all()
                .when()
                .post(REG_URL)
                .then().log().all();
    }

    @Step("Login")
    public ValidatableResponse loginUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user).log().all()
                .when()
                .post(LOGIN_URL)
                .then().log().all();
    }

    @Step("Get user info")
    public ValidatableResponse getUserInfo(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken.replace("Bearer ", "")).log().all()
                .when()
                .get(USER_INFO_URL)
                .then().log().all();
    }

    @Step("Change user info with token")
    public ValidatableResponse changeUserInfo(String accessToken, User user) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken.replace("Bearer ", "")).log().all()
                .body(user)
                .when()
                .patch(USER_INFO_URL)
                .then().log().all();
    }

    @Step("Change user info without token")
    public ValidatableResponse changeUserInfoNoToken(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user).log().all()
                .when()
                .patch(USER_INFO_URL)
                .then().log().all();
    }

    @Step("Delete user")
    public void delete(String accessToken) {
        if (accessToken == null) {
            return;
        }
        given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken.replace("Bearer ", "")).log().all()
                .when()
                .delete(USER_INFO_URL)
                .then().log().all();

    }
}