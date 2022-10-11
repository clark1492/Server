package it.unito.mailer.client.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Email implements Serializable {

  private int id;
  private User sender;
  private List<String> dests;
  private String subject;
  private String content;
  private Folder folder;
  private boolean read;
  private Email reply;
  private Date date;

  public Email(){}

  public Email(int id, User sender, List<String> dests, String subject, Date date) {
    this.id = id;
    this.sender = sender;
    this.dests = dests;
    this.subject = subject;
    this.date = date;
    folder = Folder.NONE;
    read = false;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public List<String> getDests() {
    return dests;
  }

  public void setDests(List<String> dests) {
    this.dests = dests;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Folder getBelonging() {
    return folder;
  }

  public void setBelonging(Folder folder) {
    this.folder = folder;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Email getReply() {
    return reply;
  }

  public void setReply(Email reply) {
    this.reply = reply;
  }


  @Override
  public String toString() {
    return "Email{" +
            "id=" + id +
            ", sender='" + sender + '\'' +
            ", dests=" + dests +
            ", subject='" + subject + '\'' +
            ", folder=" + folder +
            ", read=" + read +
            ", date=" + date +
            '}';
  }
}
