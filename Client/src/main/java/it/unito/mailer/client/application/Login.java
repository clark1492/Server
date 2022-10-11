package it.unito.mailer.client.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Login extends Application {

  private static Stage stg;

  public static Stage getStage() {
    return stg;
  }

  @Override
  public void start(Stage stage) {
    try {

      stg = stage;

      URL loginUrl = Login.class.getResource("login.fxml");
      FXMLLoader fxmlLoader = new FXMLLoader(loginUrl);
      Scene scene = new Scene(fxmlLoader.load(), 600, 400);

      stage.setTitle("Log In");
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      Label error = new Label("Errore caricamento della pagina");
      StackPane root = new StackPane();
      root.getChildren().add(error);
      Scene scene = new Scene(root, 300, 150);
      stage.setTitle("Error");
      stage.setScene(scene);

      stage.show();
    }
  }

  public static void main(String[] args) {
    launch();
  }
}