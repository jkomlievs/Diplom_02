package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderMethods {

    private static final String INGREDIENTS = "/api/ingredients";
    private static final String ORDERS = "/api/orders";
    public static final String BASE_URI = "https://stellarburgers.education-services.ru";

    @Step("Получить список ингредиентов")
    public Response getIngredients() {
        return given()
                .when()
                .get(INGREDIENTS);
    }

    @Step("Создать заказ (авторизация не требуется)")
    public Response createOrder(Order order) {
        return given()
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(ORDERS);
    }

    @Step("Создать заказ с авторизацией")
    public Response createOrderWithAuth(Order order, String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(ORDERS);
    }

    @Step("Получить заказы авторизованного пользователя")
    public Response getOrdersOfAuthorizedUser(String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(ORDERS);
    }

    @Step("Получить заказы неавторизованного пользователя")
    public Response getOrdersOfUnauthorizedUser() {
        return given()
                .when()
                .get(ORDERS);
    }
}