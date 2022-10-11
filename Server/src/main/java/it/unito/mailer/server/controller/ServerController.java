package it.unito.mailer.server.controller;

import it.unito.mailer.server.model.ServerModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class ServerController {

  @FXML
  private TextArea logsArea;
  private ServerModel model;

  @FXML
  public void initialize() {

    if(this.model != null)
      throw new IllegalStateException();

    try {
      model = new ServerModel();
    } catch (IOException e) {
      e.printStackTrace();
    }

    model.logTextProperty().addListener((obs,oldString,newString) -> {
      if(oldString != null && newString != null && oldString != newString)
        appendLog();
            });
    Thread server = new Thread(model);
    server.start();
  }

  private void appendLog() {
    logsArea.appendText(logsArea.getText());
  }
}
