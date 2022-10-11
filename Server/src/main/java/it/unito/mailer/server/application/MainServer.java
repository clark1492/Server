package it.unito.mailer.server.application;

import it.unito.mailer.server.controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainServer extends Application{

  public void start(Stage stage) {

    FXMLLoader fxmlLoader = new FXMLLoader(MainServer.class.getResource("server.fxml"));
    ServerController controller = new ServerController();
    fxmlLoader.setController(controller);
    Scene scene;
    try {
      scene = new Scene(fxmlLoader.load(), 1024, 600);
      stage.setScene(scene);
      stage.show();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch();
  }
}
