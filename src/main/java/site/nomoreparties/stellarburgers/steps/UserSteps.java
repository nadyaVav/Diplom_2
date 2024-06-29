package site.nomoreparties.stellarburgers.steps;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import site.nomoreparties.stellarburgers.model.User;

public class UserSteps {

  private static final String CREATE_USER = "/api/auth/register";
  private static final String DELETE_USER = "/api/auth/user";
  private static final String UPDATE_USER = "/api/auth/user";
  private static final String LOGIN_USER = "/api/auth/login";

  @Step("Send POST request to /api/auth/register for creating user")
  public Response createUser(User user) {
    return given()
      .body(user)
      .when()
      .post(CREATE_USER);
  }

  @Step("Send DELETE request to /api/auth/user for deleting user")
  public Response deleteUser(User user) {
    return given()
      .header("Authorization", user.getAccessToken())
      .when()
      .delete(DELETE_USER);
  }

  @Step("Send POST request to /api/auth/login to log in")
  public Response loginUser(User user) {
    return given()
      .body(user)
      .when()
      .post(LOGIN_USER);
  }

  @Step("Send PATCH request to /api/auth/user")
  public Response updateUser(User user) {

    RequestSpecification request = given();

    if (user.getAccessToken() != null) {
      request.header("Authorization", user.getAccessToken());
    }

    return  request
      .body(user)
      .when()
      .patch(UPDATE_USER);
  }

}
