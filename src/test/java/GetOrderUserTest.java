import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Order;
import org.example.OrderMethods;
import org.example.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.UserUtil;
import java.util.List;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static util.UserUtil.BASE_URI;
import static util.UserUtil.EMAIL;
import static util.UserUtil.NAME;
import static util.UserUtil.PASSWORD;


public class GetOrderUserTest {

    private OrderMethods orderMethods;
    private String accessToken;

    @BeforeEach
    public void setUp() {

        RestAssured.baseURI = BASE_URI;
        orderMethods = new OrderMethods();
        User user = new User(EMAIL, PASSWORD, NAME);
        UserUtil.create(user);
        accessToken = UserUtil.login(EMAIL, PASSWORD);
    }

    @Test
    @DisplayName("Получение заказов конкретным пользователем")
    @Description("Проверяем, что авторизованный пользователь может получить список заказов")
    @Step("Получаем список заказов")
    public void getOrdersAuthorizedUserTest() {
        List<String> hashes = orderMethods.getIngredients()
                .jsonPath()
                .getList("data._id", String.class);
        Order order = new Order(hashes);
        orderMethods.createOrderWithAuth(order, accessToken);
        Response response = orderMethods.getOrdersOfAuthorizedUser(accessToken);
        response.then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", not(empty()))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Проверка получения списка заказов без авторизации")
    @Description("Проверяем, что при запросе GET /api/orders без токена возвращается 401 и сообщение об ошибке")
    @Step("Проверяем получение ошибки")
    public void getOrdersUnauthorizedUserTest() {

        Response response = orderMethods.getOrdersOfUnauthorizedUser();

        response.then()
                .log().all()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @AfterEach
    @Step("Удаление тестового пользователя после выполнения тестов")
    public void tearDown() {
        if (accessToken != null) {
            UserUtil.delete(accessToken);
        }
    }
}