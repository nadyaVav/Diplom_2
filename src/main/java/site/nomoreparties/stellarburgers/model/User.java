package site.nomoreparties.stellarburgers.model;

import lombok.Data;

@Data
public class User {
  String email;
  String password;
  String name;
  String accessToken;
}
