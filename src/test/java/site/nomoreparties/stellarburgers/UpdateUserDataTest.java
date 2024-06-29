package site.nomoreparties.stellarburgers;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static site.nomoreparties.stellarburgers.config.ErrorMessages.SHOULD_BE_AUTHORISED;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.steps.UserSteps;

@Feature("[PATCH api/auth/user] Update user data")
public class UpdateUserDataTest extends AbstractTest{

  private UserSteps userSteps = new UserSteps();
  private User user;
  Faker faker = new Faker(new Locale("en-GB"));
  FakeValuesService fakeValuesService = new FakeValuesService(
    new Locale("en-GB"), new RandomService());

  @Before
  public void setUp() {
    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    user = new User();
    user.setEmail(fakeValuesService.bothify("????##@mail.ru"));
    user.setPassword(randomAlphabetic(10));
    user.setName(faker.name().firstName());
  }

  @After
  public void tearDown() {
    if(user.getAccessToken() != null) {
      userSteps.deleteUser(user);
    }
  }

  @Test
  @DisplayName("[200] [PATCH api/auth/user] Update user data. Correct request")
  public void shouldReturn200ToUpdateIfDataIsCorrect() {

    String accessToken = new UserSteps()
      .createUser(user)
      .then()
      .extract().body().path("accessToken");

    user.setAccessToken(accessToken);

    user.setEmail(fakeValuesService.bothify("????##@mail.ru"));
    user.setPassword(randomAlphabetic(10));
    user.setName(faker.name().firstName());

    userSteps
      .updateUser(user)
      .then()
      .statusCode(equalTo(SC_OK))
      .body("success", is(true))
      .body("user.email", is(user.getEmail()))
      .body("user.name", is(user.getName()));
  }

  @Test
  @DisplayName("[401] [PATCH api/auth/user] Update user data without authorization")
  public void shouldReturn401ToUpdateIfLoginPasswordNotCorrect() {

    String accessToken = userSteps
      .createUser(user)
      .then()
      .extract().body().path("accessToken");

    userSteps
      .updateUser(user)
      .then()
      .statusCode(equalTo(SC_UNAUTHORIZED))
      .body("success", is(false))
      .body("message", is(SHOULD_BE_AUTHORISED));

    user.setAccessToken(accessToken);
  }

}