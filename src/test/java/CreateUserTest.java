import org.example.User;
import util.UserUtil;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static util.UserUtil.EMAIL;
import static util.UserUtil.PASSWORD;
import static util.UserUtil.NAME;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    private String accessToken;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        accessToken = UserUtil.login(EMAIL, PASSWORD);
        if (accessToken != null) {
            UserUtil.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Создание пользователя и проверка тела ответа")
    @Description("Проверяем, что при успешном создании приходит корректный ответ")
    @Step("Создаем нового пользователя и проверяем структуру JSON")
    public void createUniqueUserTest() {
        User user = new User(EMAIL, PASSWORD, NAME);
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
        response.then()
                .log().body()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(EMAIL))
                .body("user.name", equalTo(NAME))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        // сохраняем токен для последующего удаления пользователя
        accessToken = response.jsonPath().getString("accessToken").replace("Bearer ", "");
    }

    @Test
    @DisplayName("Создание зарегистрированного пользователя")
    @Description("Проверка, что можно создать только уникального пользователя")
    @Step("Проверяем, что пользователь не создается")
    public void createExistingUserTest() {
        User user = new User(EMAIL, PASSWORD, NAME);
        UserUtil.create(user);
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
        response.then()
                .log().body()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без заполнения одного из обязательных полей")
    @Description("Невалидное создание пользователя")
    @Step("Создаем пользователя только с двумя полями")
    public void createInvalidUserTest() {
        User user = new User(EMAIL, PASSWORD, null);
        UserUtil.create(user);
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
        response.then()
                .log().body()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @AfterEach
    @Step("Удаляем тестового пользователя после теста")
    public void tearDown() {
        if (accessToken != null) {
            UserUtil.delete(accessToken);
        }
    }
}
