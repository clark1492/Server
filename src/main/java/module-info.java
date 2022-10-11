module com.example.progiiilab.login {
  requires javafx.controls;
  requires javafx.fxml;


  opens com.example.progiiilab to javafx.fxml;
  exports com.example.progiiilab.login;
  opens com.example.progiiilab.login to javafx.fxml;
  exports com.example.progiiilab.client;
  opens com.example.progiiilab.client to javafx.fxml;
  exports com.example.progiiilab.utils;
  opens com.example.progiiilab.utils to javafx.fxml;
  exports com.example.progiiilab.server;
  opens com.example.progiiilab.server to javafx.fxml;

}