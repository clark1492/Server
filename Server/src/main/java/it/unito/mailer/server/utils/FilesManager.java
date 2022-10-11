package it.unito.mailer.server.utils;

import it.unito.mailer.server.model.Email;
import it.unito.mailer.server.model.Folder;
import it.unito.mailer.server.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Static class
public class FilesManager {

  public static final String FILES_PATH = "files/";
  public static final String SERVER_PATH = "files/server";
  public static final String LOGINS_FILE = "files/server/users.dat";
  public static final String ID_COUNTER_FILE = "files/server/id_counter.dat";
  public static final String INBOX_FILENAME = "/inbox.dat";
  public static final String DRAFTS_FILENAME = "/drafts.dat";
  public static final String SENT_FILENAME = "/sent.dat";
  public static final String BIN_FILENAME = "/bin.dat";


  // File methods

  public static void createFiles(User username) {

    try{
      if (username == null)
        throw new IllegalArgumentException("[Illegal Argument]: username to be created must be not null");

      Files.createDirectories(Paths.get(FILES_PATH + username.getEmail()));

      File inbox = new File(FILES_PATH + username.getEmail() + INBOX_FILENAME);
      File drafts = new File(FILES_PATH + username.getEmail() + DRAFTS_FILENAME);
      File sent = new File(FILES_PATH + username.getEmail() + SENT_FILENAME);
      File bin = new File(FILES_PATH + username.getEmail() + BIN_FILENAME);

      inbox.createNewFile();
      drafts.createNewFile();
      sent.createNewFile();
      bin.createNewFile();

    }catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public static boolean addUserToFile(User user) throws IllegalArgumentException{

    if (user == null)
      throw new IllegalArgumentException();

    ArrayList<User> accounts = getUsers();

    if(accounts.contains(user))
      return false;

    accounts.add(user);
    createFiles(user);

    return true;
  }


  // Get user list
  public static ArrayList<User> getUsers() {

    ArrayList<User> users = new ArrayList<User>();
    try{
      ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(LOGINS_FILE));
      users = (ArrayList<User>) inputStream.readObject();
      inputStream.close();
    } catch (IOException e){
      System.out.println(e.getMessage());
    } catch (ClassNotFoundException e){
      System.out.println(e.getMessage());
    }
    return users;
  }

  private static File getFile(String user, Folder folder){
    String path = FILES_PATH + user;
    switch (folder) {
      case INBOX -> {
        path += INBOX_FILENAME;}
      case DRAFTS -> {
        path += DRAFTS_FILENAME;}
      case BIN -> {
        path += BIN_FILENAME;}
      case SENT -> {
        path += SENT_FILENAME;}
    }

    return new File(path);
  }

  // Mailboxes methods
  public static ArrayList<Email> getMailBox(String user, Folder folder) throws IllegalArgumentException{

    if (user == null || folder == null)
      throw new IllegalArgumentException();

    File mailBox = getFile(user, folder);
    ArrayList<Email> emailList = new ArrayList<>();

    try {
      ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(mailBox));
      emailList = (ArrayList<Email>) inputStream.readObject();
      inputStream.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch(ClassNotFoundException e){
      System.out.println(e.getMessage());
    }

    return emailList;
  }

  private static void updateMailBox(File mailBox, ArrayList<Email> updated){

    try {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mailBox));
      out.writeObject(updated);
      out.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  public static boolean rmMailFromMailbox(String user, Email toRemove) throws IllegalArgumentException{

    boolean removed = false;

    if (toRemove == null || user == null || toRemove.getBelonging() !=Folder.BIN)
      throw new IllegalArgumentException();

    File bin = new File(FILES_PATH + user + BIN_FILENAME);

    synchronized (bin) {

      ArrayList<Email> mailList = getMailBox(user,Folder.BIN);
      int idToRemove = toRemove.getId();

      for (Email mail : mailList) {
        if (mail.getId() == idToRemove) {
          mailList.remove(mail);
          removed = true;
          break;
        }
      }
      if (removed)
        updateMailBox(bin,mailList);
    }
    return removed;
  }

  public static void addInbox(Email toInsert) throws IllegalArgumentException {

    List<String> dests = toInsert.getDests();

    if (toInsert == null || dests == null)
      throw new IllegalArgumentException();

    ArrayList<Email> inbox;

    boolean reply = (toInsert.getReply() != null);

    for (String user : dests) {
      if (reply)
        toInsert.setId(toInsert.getReply().getId());
      else
        toInsert.setId(getID());

      toInsert.setBelonging(Folder.INBOX);

      File file = new File(FILES_PATH + user + INBOX_FILENAME);

      synchronized (file) {
        inbox = getMailBox(user,Folder.INBOX);
        if (reply) {
          for (int i = 0; i < inbox.size(); i++) {
            if (inbox.get(i).getId() == toInsert.getId())
              inbox.remove(i);
          }
        }
        inbox.add(toInsert);
        updateMailBox(file, inbox);
        if (!reply)
          incrementID();
      }
    }
  }

  public static void addSentMail(Email toInsert)
          throws IllegalArgumentException {

    User sender = toInsert.getSender();

    if (toInsert == null || sender == null)
      throw new IllegalArgumentException();

    ArrayList<Email> sent;

    boolean reply = (toInsert.getReply() != null);

    File file = getFile(sender.getEmail(),Folder.SENT);

    synchronized (file) {
      sent = getMailBox(sender.getEmail(),Folder.SENT);
      toInsert.setRead(true);
      if (reply) {
        for (int i = 0; i < sent.size(); i++) {
          if (sent.get(i).getId() == toInsert.getId())
            sent.remove(i);
        }
      }
      sent.add(toInsert);
      updateMailBox(file,sent);
    }
  }

  public static void addDrafts(User user,Email toSave){

    if(toSave == null || user == null)
      throw new IllegalArgumentException();

    File file = getFile(user.getEmail(),Folder.DRAFTS);
    ArrayList<Email> drafts = new ArrayList<>();

    synchronized (file) {
      drafts = getMailBox(user.getEmail(),Folder.DRAFTS);
      drafts.add(toSave);
      updateMailBox(file,drafts);
    }
  }

  public static boolean setMailRead(User user, Email toFlag) {

    if (toFlag == null || toFlag.getBelonging() !=Folder.INBOX || user == null)
      throw new IllegalArgumentException();

    File inbox = getFile(user.getEmail(),Folder.INBOX);

    ArrayList<Email> mailBox = new ArrayList<Email>();
    boolean found = false;

    synchronized (inbox) {
      mailBox = getMailBox(user.getEmail(),Folder.INBOX);
      for (Email email : mailBox) {
        if (email.getId() == toFlag.getId()) {
          email.setRead(true);
          found = true;
          break;
        }
      }
    }
    return found;
  }

  // ID Counter
  public static int getID() {

    File server = new File(ID_COUNTER_FILE);
    int id = 0;
    synchronized (server) {
      try {
        DataInputStream inputStream = new DataInputStream(new FileInputStream(server));
        id = inputStream.readInt();
        inputStream.close();
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
      return id;
    }
  }

  public static void incrementID() {

    File server = new File(ID_COUNTER_FILE);

    synchronized (server) {
      try {
        DataInputStream inputStream = new DataInputStream(new FileInputStream(server));
        int id = inputStream.readInt();
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(server));
        outputStream.writeInt(id +1);
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    }
  }
}

