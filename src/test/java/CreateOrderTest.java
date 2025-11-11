import org.example.OrderMethods;
import org.example.Order;
import org.example.User;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import util.UserUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest {

    private OrderMethods orderMethods;
    private String accessToken;
    private static List<String> allIngredients;

    @BeforeAll
    public static void initIngredients() {
        OrderMethods orderMethods = new OrderMethods();
        Response response = orderMethods.getIngredients();
        response.then().statusCode(200);
        allIngredients = response.jsonPath().getList("data._id", String.class);
    }

    @BeforeEach
    @Step("Создаем пользователя для создания заказа")
    public void setUp() {
        orderMethods = new OrderMethods();

        User user = new User(UserUtil.EMAIL, UserUtil.PASSWORD, UserUtil.NAME);
        UserUtil.create(user);
        accessToken = UserUtil.login(UserUtil.EMAIL, UserUtil.PASSWORD)
                .replace("Bearer ", "");
    }

    private static Stream<List<String>> orderData() {
        return Stream.of(
                allIngredients.subList(0, 1),     // 1 ингредиент
                allIngredients.subList(0, 2),     // 2 ингредиента
                allIngredients.subList(0, 3),     // 3 ингредиента
                new ArrayList<>()                 // без ингредиентов
        );
    }

    @ParameterizedTest(name = "Создание заказа с количеством ингредиентов: {0}")
    @MethodSource("orderData")
    @Description("Проверка создания заказа с разным количеством ингредиентов")
    @DisplayName("Параметризованный тест: создание заказов с разными наборами ингредиентов")
    @Step("Проверяем создание заказа с параметризацией")
    public void createOrderParameterized(List<String> ingredients) {

        Order order = new Order(ingredients);
        Response response = orderMethods.createOrderWithAuth(order, accessToken);

        if (ingredients.isEmpty()) {

            response.then()
                    .log().all()
                    .statusCode(400)
                    .body("success", equalTo(false))
                    .body("message", equalTo("Ingredient ids must be provided"));
        } else {

            response.then()
                    .log().all()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("order.number", notNullValue())
                    .body("name", notNullValue());
        }
    }

    @ParameterizedTest
    @MethodSource("orderData")
    @Description("Проверка создания заказа неавторизованным пользователем")
    @DisplayName("Создание заказа неавторизованным пользователем")
    @Step("Проверяем, что для заказа надо авторизоваться")
    public void createOrderWithoutAuth(List<String> ingredients) {

        Order order = new Order(ingredients);
        Response response = orderMethods.createOrder(order);

        if (ingredients.isEmpty()) {
            response.then()
                    .log().all()
                    .statusCode(400)
                    .body("success", equalTo(false))
                    .body("message", equalTo("Ingredient ids must be provided"));
        } else {
            response.then()
                    .log().all()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("name", notNullValue())
                    .body("order.number", notNullValue());
        }
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    @Description("Проверка, что при передаче невалидного ингредиента возвращается 500")
    @Step("Проверяем получение ошибки")
    public void createOrderWithInvalidHash() {

        List<String> invalidIngredients = List.of("invalid_hash_124");

        Order invalidOrder = new Order(invalidIngredients);

        Response response = orderMethods.createOrderWithAuth(invalidOrder, accessToken);

        response.then()
                .log().all()
                .statusCode(500);
    }

    @AfterEach
    @Step("Удаление тестового пользователя после теста")
    public void tearDown() {
        if (accessToken != null) {
            UserUtil.delete(accessToken);
        }
    }
}




