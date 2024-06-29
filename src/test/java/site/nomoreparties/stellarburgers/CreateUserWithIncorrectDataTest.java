package site.nomoreparties.stellarburgers;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static site.nomoreparties.stellarburgers.config.ErrorMessages.CREATE_USER_WITHOUT_REQUIRED_FIELDS;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.steps.UserSteps;

@Feature("[POST /api/auth/register] Create user")
@RunWith(Parameterized.class)
public class CreateUserWithIncorrectDataTest extends AbstractTest{

  private User user;
  private String name;
  private String email;
  private String password;

  public CreateUserWithIncorrectDataTest(String email, String password, String name) {
    this.email = email;
    this.password = password;
    this.name = name;
  }

  @Parameterized.Parameters
  public static Object[][] getTestData() {
    return new Object[][] {
      {null, null, null},
      {randomAlphabetic(10), randomAlphabetic(10), null},
      {null, randomAlphabetic(10), randomAlphabetic(10)},
      {randomAlphabetic(10), null, randomAlphabetic(10)},
      {randomAlphabetic(10),randomAlphabetic(10), null},
    };
  }

  @Before
  public void setUp() {
    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    user = new User();
    user.setEmail(email);
    user.setPassword(password);
    user.setName(name);
  }

  @Test
  @DisplayName("[403] [POST api/auth/register] Create user. Incorrect request")
  public void shouldReturn403IfRequiredFieldsIsIncorrect() {

    new UserSteps()
      .createUser(user)
      .then()
      .statusCode(equalTo(SC_FORBIDDEN))
      .body("success", is(false))
      .body("message", is(CREATE_USER_WITHOUT_REQUIRED_FIELDS));

  }


}