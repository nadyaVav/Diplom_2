package site.nomoreparties.stellarburgers;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;
import static site.nomoreparties.stellarburgers.config.ErrorMessages.INGREDIENT_IDS_MUST_BE_PROVIDED;
import static site.nomoreparties.stellarburgers.config.ErrorMessages.SHOULD_BE_AUTHORISED;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.model.Order;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.steps.IngredientStep;
import site.nomoreparties.stellarburgers.steps.OrderSteps;
import site.nomoreparties.stellarburgers.steps.UserSteps;

@Feature("[POST /api/order] Create order")
public class CreateOrderTest extends AbstractTest{

  private OrderSteps orderSteps = new OrderSteps();
  private UserSteps userSteps = new UserSteps();
  private IngredientStep ingredientStep = new IngredientStep();
  private User user;
  private Order order;

  @Before
  public void setUp() {
    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    Faker faker = new Faker(new Locale("en-GB"));
    FakeValuesService fakeValuesService = new FakeValuesService(
      new Locale("en-GB"), new RandomService());

    user = new User();
    user.setEmail(fakeValuesService.bothify("????##@mail.ru"));
    user.setPassword(randomAlphabetic(10));
    user.setName(faker.name().firstName());

    order = new Order();
  }

  @After
  public void tearDown() {
    if(user.getAccessToken() != null) {
      userSteps.deleteUser(user);
    }
  }

  @Test
  @DisplayName("[200] [POST /api/order] Create order with ingredients for authorized user. Correct request")
  public void shouldReturn200ToCreateOrderWithIngredientsAndAuthorization() {

    String accessToken = userSteps
      .createUser(user)
      .then()
      .extract().body().path("accessToken");

    user.setAccessToken(accessToken);

    List<String> ingredientsList = ingredientStep
      .getListOfIngredients()
      .then()
      .extract().body().path("data._id");

    Collections.shuffle(ingredientsList);

    order.setIngredients(List.of(ingredientsList.get(0), ingredientsList.get(1), ingredientsList.get(2)));

    orderSteps
      .createOrder(order, accessToken)
      .then()
      .statusCode(equalTo(SC_OK))
      .body("success", is(true))
      .body("name", notNullValue())
      .body("order.number", notNullValue())
      .body("order.ingredients", iterableWithSize(3))
      .body("order.status", equalTo("done"));
  }


  @Test
  @DisplayName("[400] [POST /api/order] Create order without ingredients for authorized user. Incorrect request")
  public void shouldReturn400ToCreateOrderWithoutIngredientsAndAuthorization() {

    String accessToken = userSteps
      .createUser(user)
      .then()
      .extract().body().path("accessToken");

    user.setAccessToken(accessToken);

    orderSteps
      .createOrder(order, accessToken)
      .then()
      .statusCode(equalTo(SC_BAD_REQUEST))
      .body("success", is(false))
      .body("message", is(INGREDIENT_IDS_MUST_BE_PROVIDED));
  }


  @Test
  @DisplayName("[401] [POST /api/order] Create order with ingredients for unauthorized user")
  public void shouldReturn401ToCreateOrderWithIngredientsAndUnauthorization() {

    List<String> ingredientsList = ingredientStep
      .getListOfIngredients()
      .then()
      .extract().body().path("data._id");

    Collections.shuffle(ingredientsList);

    order.setIngredients(List.of(ingredientsList.get(0), ingredientsList.get(1)));

    orderSteps
      .createOrder(order, null)
      .then()
      .statusCode(equalTo(SC_UNAUTHORIZED))
      .body("success", is(false))
      .body("message", equalTo(SHOULD_BE_AUTHORISED));
  }

  @Test
  @DisplayName("[500] [POST /api/order] Create order with ingredients with incorrect hash")
  public void shouldReturn500ToCreateOrderWithIngredientsIncorrectHash() {

    String accessToken = userSteps
      .createUser(user)
      .then()
      .extract().body().path("accessToken");

    user.setAccessToken(accessToken);

    List<String> ingredientsList = ingredientStep
      .getListOfIngredients()
      .then()
      .extract().body().path("data._id");

    order.setIngredients(List.of(ingredientsList.get(0) + "test", ingredientsList.get(1).toUpperCase()));

    orderSteps
      .createOrder(order, null)
      .then()
      .statusCode(equalTo(SC_INTERNAL_SERVER_ERROR));
  }


}