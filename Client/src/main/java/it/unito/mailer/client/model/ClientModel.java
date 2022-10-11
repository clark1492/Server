package it.unito.mailer.client.model;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClientModel {

  private int id;
  private User user;
  private List<Email> emailList;
  private Socket server;
  private ObjectInputStream inStream;
  private ObjectOutputStream outStream;
  private Folder currentFolder;
  private Email currentMail;

  public Email getSelectedMail() {
    return currentMail;
  }

  public void setSelectedMail(Email currentMail) {
    this.currentMail = ClientModel.this.currentMail;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Email> getEmailList() {
    return emailList;
  }

  public void setEmailList(List<Email> emailList) {
    this.emailList = emailList;
  }

  public Socket getServer() {
    return server;
  }

  public void setServer(Socket server) {
    this.server = server;
  }

  public ObjectInputStream getInStream() {
    return inStream;
  }

  public void setInStream(ObjectInputStream inStream) {
    this.inStream = inStream;
  }

  public ObjectOutputStream getOutStream() {
    return outStream;
  }

  public void setOutStream(ObjectOutputStream outStream) {
    this.outStream = outStream;
  }

  public Folder getSelectedFold() {
    return currentFolder;
  }

  public void setSelectedFold(Folder currentFolder) {
    this.currentFolder = ClientModel.this.currentFolder;
  }

  public ClientModel(int id, User user, Socket server) throws IOException {
    this.id = id;
    this.user = user;
    this.server = server;
    this.inStream = new ObjectInputStream(server.getInputStream());
    this.outStream = new ObjectOutputStream(server.getOutputStream());
    emailList = new ArrayList<>();
    currentFolder = Folder.NONE;
  }

  public ArrayList<Email> mailBoxRequest(Folder folder) throws IOException, ClassNotFoundException {

    outStream.writeObject(Protocol.MAILLIST_REQUEST);
    outStream.writeObject(folder);

    ArrayList<Email> mailList = null;
    Object data = inStream.readObject();
    if(data.getClass() != mailList.getClass())
      throw new InvalidObjectException("[Invalid Object]: object read is not the right type");

    return (ArrayList<Email>) data;
  }


  public boolean emailRequest(Protocol p,Email email) throws IOException, ClassNotFoundException {

    outStream.writeObject(p);
    outStream.writeObject(email);
    return serverResponse();

  }

  private boolean serverResponse() throws IOException, ClassNotFoundException {

    Protocol serverResp = (Protocol) inStream.readObject();

    return (serverResp == Protocol.SERVER_SUCCESS);
  }

  public SimpleListProperty<Email> getCurrentMailBox(){

    List<Email> currentMailBox = new ArrayList<>();

    for (Email mail:
         emailList) {
      if(mail.getBelonging() == currentFolder)
        currentMailBox.add(mail);
    }
    ObservableList<Email> observableList = FXCollections.observableList(new LinkedList<>(currentMailBox));

    return new SimpleListProperty<Email>(observableList);
  }
}
