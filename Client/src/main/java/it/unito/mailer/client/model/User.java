package it.unito.mailer.client.model;

import java.util.ArrayList;
import java.util.List;

public class User {

  private String email;
  private int id;
  private List<Email> emailList;

  public User(String email){
    this.email = email;
  }

  public User(String email,int id) {

    this.email = email;
    this.id = id;
    emailList = new ArrayList<Email>();

  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Email> getEmailList() {
    return emailList;
  }

  public void setEmailList(List<Email> emailList) {
    this.emailList = emailList;
  }

  @Override
  public String toString() {
    return "User{" +
            "email='" + email + '\'' +
            ", id=" + id +
            '}';
  }
}
