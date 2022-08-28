import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;

public class RestAssuredClient {
    public RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://stellarburgers.nomoreparties.site/")
                .build();
    }
}
