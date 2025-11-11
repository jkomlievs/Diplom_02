import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.User;
import org.example.UserLogin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.UserUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static util.UserUtil.BASE_URI;
import static util.UserUtil.EMAIL;
import static util.UserUtil.NAME;
import static util.UserUtil.PASSWORD;

public class LoginUserTest {

    public String accessToken;

    @BeforeEach
    public void setUp() {

        RestAssured.baseURI = BASE_URI;
        accessToken = UserUtil.login(EMAIL, PASSWORD);
        if (accessToken != null) {
            UserUtil.delete(accessToken);
        }
    }

    @Test
    @Step("Создаем пользователя")
    @DisplayName("Создание пользователя для проверки авторизации")
    @Description("Создаем пользователя для авторизации в системе")
    public void loginUserGet201Test() {
        User user = new User(EMAIL, PASSWORD, NAME);
        UserUtil.create(user);
        UserLogin loginData = new UserLogin(EMAIL, PASSWORD);

        accessToken = given()
                .header("Content-type", "application/json")
                .body(loginData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(EMAIL))
                .body("user.name", equalTo(NAME))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .extract()
                .path("accessToken")
                .toString()
                .substring(7);
    }

    @Test
    @Step("Проверка авторизации пользователя")
    @DisplayName("Авторизация существующего пользователя")
    @Description("Проверяем, что пользователь может авторизоваться")
    public void loginExistingUserTest() {
        User user = new User(EMAIL, PASSWORD, NAME);
        UserUtil.create(user);
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
        response.then()
                .log().body()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(EMAIL))
                .body("user.name", equalTo(NAME));

        accessToken = response.jsonPath().getString("accessToken").replace("Bearer ", "");
    }

    @Test
    @Step("Проверка авторизации несуществуюшего пользователя")
    @DisplayName("Авторизация с неверным логином и паролем")
    @Description("Проверяем, что несуществующий пользователь не может авторизоваться")
    public void loginInvalidUserDataTest() {
        User user = new User(EMAIL, PASSWORD, NAME);
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
        response.then()
                .log().body()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

    }

    @AfterEach
    @Step("Удаляем тестового пользователя после теста")
    public void tearDown() {
        if (accessToken != null) {
            UserUtil.delete(accessToken);
        }
    }
}
