package util;

import org.example.User;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserUtil {

    public static final String EMAIL = "test-data12144@yandex.ru";
    public static final String PASSWORD = "12345678";
    public static final String NAME = "Ivana1235";

    static {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
    }

    public static Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    public static String login(String email, String password) {
        String token =  given()
                .header("Content-type", "application/json")
                .body(String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password))
                .when()
                .post("/api/auth/login")
                .then()
                .extract()
                .path("accessToken");
        return token == null ? null : token.substring(7);
    }


    public static void delete(String accessToken) {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202)
                .body("success", equalTo(true));
        }
    }