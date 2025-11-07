import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.UserUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static util.UserUtil.EMAIL;
import static util.UserUtil.NAME;
import static util.UserUtil.PASSWORD;


public class ChangeUserDataTest {

    public String accessToken;

    @BeforeEach
    public void setUp() {

        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        accessToken = UserUtil.login(EMAIL, PASSWORD);
        tearDown();
    }

    @Test  //проверить, неправильно
    @Description ("Проверка обновления данных пользователя с авторизацией")
    @DisplayName("Обновление данных с авторизацией")
    @Step("Проверяем обновление данных пользователя с авторизацией")
    public void patchUserAuthTest(){
        // given
        User user = new User(EMAIL, PASSWORD, NAME);
        UserUtil.create(user);
        accessToken = UserUtil.login(EMAIL, PASSWORD);

        // when
        User updatedUser = new User("new_" + EMAIL, PASSWORD, "NewName");

        // then
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(updatedUser)
                .when()
                .patch("/api/auth/user");
                response.then()
                .log().body()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo("new_" + EMAIL))
                .body("user.name", equalTo("NewName"))
                .body("user", notNullValue());
    }
    @Test  //проверить, неправильно
    @Description ("Проверка обновления данных пользователя без авторизации")
    @DisplayName("Обновление данных без авторизации")
    @Step("Проверяем обновление данных пользователя без авторизации")
    public void patchUserWithoutAuthTest(){
        User user = new User(EMAIL, PASSWORD, NAME);
        UserUtil.create(user);
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch("/api/auth/user");
        response.then()
                .log().body()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));

    }

    @AfterEach
    @Step("Удаляем тестового пользователя после теста")
    public void tearDown() {
        if (accessToken != null) {
            UserUtil.delete(accessToken);
        }
    }
}