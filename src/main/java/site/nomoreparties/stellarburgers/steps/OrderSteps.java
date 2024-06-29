package site.nomoreparties.stellarburgers.steps;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import site.nomoreparties.stellarburgers.model.Order;

public class OrderSteps {

  private static final String CREATE_ORDER = "/api/orders";
  private static final String GET_ORDERS_BY_USER = "/api/orders";

  @Step("Send POST request to /api/order for creating order")
  public Response createOrder(Order order, String token) {
    RequestSpecification request = given();

    if (token != null) {
      request.header("Authorization", token);
    }

    return request
      .body(order)
      .when()
      .post(CREATE_ORDER);
  }

  @Step("Send GET request to /api/order for getting orders for user")
  public Response getListOfOrdersByUser(String token) {

    RequestSpecification request = given();

    if (token != null) {
      request.header("Authorization", token);
    }

    return request
      .when()
      .get(GET_ORDERS_BY_USER);
  }

}
