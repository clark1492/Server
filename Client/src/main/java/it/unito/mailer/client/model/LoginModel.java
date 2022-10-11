package it.unito.mailer.client.model;

import it.unito.mailer.client.application.Login;
import it.unito.mailer.client.controller.ClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class LoginModel {

  private Socket server;
  private ObjectInputStream inStream;
  private ObjectOutputStream outStream;
  private ArrayList<User> userList;

  public LoginModel() {

    try {
      server = new Socket(InetAddress.getLocalHost().getHostName(), 8189);
      inStream = new ObjectInputStream(server.getInputStream());
      outStream = new ObjectOutputStream(server.getOutputStream());
      userListRequest();
    } catch (IOException e) {
      e.printStackTrace();
      errorWindow("Errore di connessione");
    }

  }

  public ArrayList<User> getUserList() {
    return userList;
  }

  private void userListRequest() {

    try {
      outStream.writeObject(Protocol.USERLIST_REQUEST);
      Object data = inStream.readObject();

      if(!(data instanceof List<?>))
        throw new InvalidObjectException("[Invalid Object]: not a list");

      userList = (ArrayList<User>)data;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      errorWindow("Errore di connessione");
    }

  }

  public void loginRequest(User user)  {

    try {

      if(user == null) {
        throw new IllegalArgumentException("user cannot be null");
      }
      outStream.writeObject(Protocol.LOGIN_REQUEST);
      outStream.writeObject(user);


      if(inStream.readObject() == Protocol.SERVER_SUCCESS) {
        int id = (int) inStream.readObject();
        clientView(id, user, server);
      }


    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      errorWindow("Errore di connessione");
    }

  }

  private void clientView(int id,User user,Socket server) {
    try {

      ClientModel model = new ClientModel(id,user,server);
      ClientController controller = new ClientController();
      controller.initModel(model);

      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("complete.fxml"));

      Scene scene = new Scene(loader.load());

      Stage stage = Login.getStage();
      stage.setScene(scene);

    } catch (IOException e) {
      e.printStackTrace();
      errorWindow("Errore caricamento pagina");
    }
  }

  public void errorWindow(String errorMessage){
    Label error = new Label(errorMessage);
    StackPane root = new StackPane();
    root.getChildren().add(error);
    Scene scene = new Scene(root, 300, 150);
    Stage stage = Login.getStage();

    stage.setTitle("Error");
    stage.setScene(scene);

    stage.show();
  }

}
