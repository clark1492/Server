package it.unito.mailer.server.model;

import it.unito.mailer.server.handler.HandleClient;
import it.unito.mailer.server.utils.FilesManager;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerModel implements Runnable{

  private final ServerSocket client;

  private SimpleStringProperty logText;

  private HashMap<Integer,User> usersList;

  public ServerModel() throws IOException{

    this.client = new ServerSocket(8990);
    this.usersList = new HashMap<>();

  }

  public SimpleStringProperty logTextProperty() {
    return logText;
  }

  public void setLogText(String string) {
    this.logText.setValue(string);
  }

  public ServerSocket getClient() {
    return client;
  }

  public HashMap<Integer, User> getUsersList() {
    return usersList;
  }

  public Integer getUserId(User user) {

    Integer id = 0;

    if(!userExist(user.getEmail()))
      return id;

    for (Integer key: usersList.keySet()) {
      if(usersList.get(key) == user)
        id = key;
    }
    return id;
  }

  public void loadUsersList(){

    ArrayList<User> users = FilesManager.getUsers();

    if(users == null)
      throw new IllegalArgumentException();

    for(int i = 0; i < users.size(); i++){
      usersList.put(i,users.get(i));
    }
  }

  public boolean userExist(String user){

    if(user == null)
      throw new IllegalArgumentException();

    return usersList.containsValue(user);
  }

  public void initialize(){
    try{
      if(!Files.exists(Paths.get(FilesManager.FILES_PATH)))
        Files.createDirectories(Paths.get(FilesManager.FILES_PATH));
    } catch(IOException e){
      System.out.println(e.getMessage());
    }

    loadUsersList();

    for (User user: usersList.values()) {
      if(!Files.exists(Paths.get(FilesManager.FILES_PATH + user.getEmail())));
      FilesManager.createFiles(user);
    }
  }

  @Override
  public void run() {
    Thread.currentThread().setName("Server");
    initialize();
    setLogText("[SERVER] : Waiting clients connection...");
    while(true){
      try{
        Socket socket = client.accept();
        HandleClient client = new HandleClient(this,socket);
        client.start();
      }catch (IOException e){
        System.out.println(e.getMessage());
      }
    }
  }
}