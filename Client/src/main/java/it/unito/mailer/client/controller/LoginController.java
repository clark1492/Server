package it.unito.mailer.client.controller;

import it.unito.mailer.client.model.LoginModel;
import it.unito.mailer.client.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

  @FXML
  private Label label;
  @FXML
  private ChoiceBox<User> userChoice;

  private LoginModel model;


  @FXML
  protected void loginRequest() {
    model.loginRequest(userChoice.getValue());

  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    if (this.model != null)
      throw new IllegalStateException("Model can only be initialized once");

    model = new LoginModel();
    label.setText("Select your username");
    ObservableList<User> obsList = FXCollections.observableList(model.getUserList());
    userChoice.setItems(obsList);
  }

}