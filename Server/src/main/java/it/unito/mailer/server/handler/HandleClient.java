package it.unito.mailer.server.handler;

import it.unito.mailer.server.model.*;
import it.unito.mailer.server.utils.FilesManager;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HandleClient extends Thread {

  private final ServerModel server;

  private User client;
  private ObjectInputStream inStream;
  private ObjectOutputStream outStream;
  private boolean exit = false;

  public HandleClient(ServerModel server, Socket clientSocket) throws IllegalArgumentException, IOException {

    if (server == null || clientSocket == null)
      throw new IllegalArgumentException();

    this.server = server;

    inStream = new ObjectInputStream(clientSocket.getInputStream());
    outStream = new ObjectOutputStream(clientSocket.getOutputStream());

  }

  public void sendResponse(Protocol resp) throws IllegalArgumentException, IOException {
    if (resp == null)
      throw new IllegalArgumentException();

    outStream.writeObject(resp);
  }

  public void loginData() throws InvalidObjectException, IOException, ClassNotFoundException {

    Object data = inStream.readObject();
    if (!(data.getClass() == User.class))
      throw new InvalidObjectException("[Invalid Object]: You need an user to login\n");

    User user = (User) data;
    if (server.userExist(user.getEmail())) {
      client = user;
      sendResponse(Protocol.SERVER_SUCCESS);
      outStream.writeObject(server.getUserId(user));
      server.setLogText("Client" + user.getEmail() + "authenticated");
    } else {
      sendResponse(Protocol.SERVER_UNSUCCESS);
      server.setLogText("Authentication problem: user" + user.getEmail() + "does not exist");
    }
  }

  public void mailBoxRequest() throws IOException, ClassNotFoundException {

    ArrayList<Email> mailList = null;

    Object data = inStream.readObject();
    if (data instanceof Folder)
      throw new InvalidObjectException("[Invalid Object]: You need to specify the folder\n");

    mailList = FilesManager.getMailBox(client.getEmail(), (Folder) data);

    outStream.writeObject(mailList);

  }

  public void sendEmailRequest() throws IOException, ClassNotFoundException {

    Object data = inStream.readObject();

    if (!(data instanceof Email))
      throw new InvalidObjectException("[Invalid Object]: You need to specify the email\n");

    Email mail = (Email) data;

    List<String> dests = mail.getDests();


    for (String user : dests)
      if (!server.userExist(user)) {
        sendResponse(Protocol.SERVER_UNSUCCESS);
        server.setLogText("["+ client.getEmail() + "] : Impossible to sent email, user " + user + " does not exist");
        return;
      }

    FilesManager.addInbox(mail);

    FilesManager.addSentMail(mail);
    sendResponse(Protocol.SERVER_SUCCESS);
    server.setLogText("["+ client.getEmail() + "] : Mail "+ mail.getId() + " sent correctly to " + dests);

  }

  public void deleteEmailRequest() throws IOException, ClassNotFoundException {
    Object data = inStream.readObject();

    if (!(data instanceof Email))
      throw new InvalidObjectException("[Invalid Object]: You need to specify the email\n");

    Email mail = (Email) data;

    FilesManager.rmMailFromMailbox(client.getEmail(), mail);
    sendResponse(Protocol.SERVER_SUCCESS);
    server.setLogText("["+ client.getEmail() + "] : Mail " + mail.getId() +" removed correctly");
  }


  public void saveEmailRequest() throws IOException, ClassNotFoundException {
    Object data = inStream.readObject();

    if (!(data instanceof Email))
      throw new InvalidObjectException("[Invalid Object]: You need to specify the email\n");

    Email mail = (Email) data;

    FilesManager.addDrafts(client, mail);
    sendResponse(Protocol.SERVER_SUCCESS);
    server.setLogText("["+ client.getEmail() + "] : Mail " + mail.getId() +" removed correctly");
  }

  public void readFlagRequest() throws IOException, ClassNotFoundException {

    Object data = inStream.readObject();

    if (!(data instanceof Email))
      throw new InvalidObjectException("[Invalid Object]: You need to specify the email\n");

    Email mail = (Email) data;

    FilesManager.setMailRead(client, mail);
    sendResponse(Protocol.SERVER_SUCCESS);
    server.setLogText("["+ client.getEmail() + "] : Mail " + mail.getId() +" set read flag");
  }


  @Override
  public void run() {

    while (!exit) {
      try {
        Object read = null;
        if ((read = inStream.readObject()) != null)
          if (read instanceof Protocol) {
            switch ((Protocol) read) {
              case LOGIN_REQUEST -> {
                loginData();
              }
              case USERLIST_REQUEST -> {
                outStream.writeObject(server.getUsersList());
              }
              case MAILLIST_REQUEST -> {
                mailBoxRequest();
              }
              case SEND_EMAIL -> {
                sendEmailRequest();
              }
              case DEL_EMAIL -> {
                deleteEmailRequest();
              }
              case DRAFTS_EMAIL -> {
                saveEmailRequest();
              }
              case READ_FLAG -> {
                readFlagRequest();
              }
              case EXIT_REQUEST -> {
                exit = true;
              }
            }
          }
      } catch (ClassNotFoundException | IOException e) {
        exit = true;
        e.printStackTrace();
      }
    }
  }

}
