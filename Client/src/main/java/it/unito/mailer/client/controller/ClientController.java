package it.unito.mailer.client.controller;

import it.unito.mailer.client.model.ClientModel;
import it.unito.mailer.client.model.Email;
import it.unito.mailer.client.model.Folder;
import it.unito.mailer.client.model.Protocol;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ClientController {

  private ClientModel model;
  private Email selectedMail;
  private Folder currentFolder;

  @FXML
  private Label lblFolder;

  @FXML
  private Label lblSubject;

  @FXML
  private Label lblSource;

  @FXML
  private Label lblDate;

  @FXML
  private TextArea emailContent;

  @FXML
  private Label lblUsername;

  @FXML
  private ListView<Email> lstEmails;

  @FXML
  private TextField txtTo;

  @FXML
  private VBox readBox;

  @FXML
  private VBox writeBox;

  @FXML
  private TextField fromField;

  @FXML
  private TextField toField;

  @FXML
  private TextField subjectsField;


  @FXML
  public void onNewEmailButtonClick(){
    model.setSelectedFold(Folder.NONE);
    model.setSelectedMail(null);
//    writeMode();
  }


  @FXML
  public void onIncomingMailButtonClick(){
    model.setSelectedFold(Folder.INBOX);
    model.setSelectedMail(null);
  }

  @FXML
  public void onDraftsButtonClick(){
    model.setSelectedFold(Folder.DRAFTS);
    model.setSelectedMail(null);
  }

  @FXML
  public void onSentMailButtonClick(){
    model.setSelectedFold(Folder.SENT);
    model.setSelectedMail(null);
  }

  @FXML
  public void sendOnAction() throws IOException, ClassNotFoundException {

    Email mail = new Email();
    composeMail(mail);
    model.emailRequest(Protocol.SEND_EMAIL,mail);

  }

  @FXML
  public void deleteOnAction() throws IOException, ClassNotFoundException {
    model.emailRequest(Protocol.DEL_EMAIL, selectedMail);
  }

  @FXML
  public void saveOnAction() throws IOException, ClassNotFoundException {
    Email mail = new Email();
    composeMail(mail);
    model.emailRequest(Protocol.DRAFTS_EMAIL,mail );
  }

  private void composeMail(Email new_mail){

    new_mail.setId(model.getId());
    new_mail.setRead(false);
    //new_mail.setSubject(textSubject.getText());
    new_mail.setDate(Calendar.getInstance().getTime());
    new_mail.setContent(emailContent.getText());

    String [] arrayDest = txtTo.getText().split("[,;\\s\\t]+");
    new_mail.setDests(new ArrayList<>(Arrays.asList(arrayDest)));
  }

  public void initModel(ClientModel model) {
    this.model = model;
    this.selectedMail = null;
    this.currentFolder = Folder.INBOX;

    lstEmails.itemsProperty().bind(model.getCurrentMailBox());
    lstEmails.setOnMouseClicked(this::showSelectedMail);
    lblUsername.setText(model.getUser().getEmail());
  }

  protected void showSelectedMail(MouseEvent event){
    selectedMail = lstEmails.getSelectionModel().getSelectedItem();

    updateDetailView(selectedMail);
  }

  protected void updateDetailView(Email mail) {
    if (mail != null) {
      lblSource.setText(mail.getSender().getEmail());
      lblSubject.setText(mail.getSubject());
      lblDate.setText(mail.getDate().toString());
      emailContent.setText(mail.getContent());
    }
  }

  protected void writeMode(Email mail){
    if(readBox.isVisible())
      readBox.setVisible(false);
    if(!writeBox.isVisible())
      writeBox.setVisible(true);
    if(mail != null) {
//      from.setText(mail.getSender().getEmail());
      lblSubject.setText(mail.getSubject());
      emailContent.setText(mail.getContent());
    }
  }
}
