package site.nomoreparties.stellarburgers.steps;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Step;
import io.restassured.response.Response;

public class IngredientStep {

  private static final String GET_INGREDIENT = "/api/ingredients";

  @Step("Send GET request to /api/ingredients for getting ingredients")
  public Response getListOfIngredients() {
    return given()
      .when()
      .get(GET_INGREDIENT);
  }
}
