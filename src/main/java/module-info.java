module com.example.progiiilab {
  requires javafx.controls;
  requires javafx.fxml;


  opens com.example.progiiilab to javafx.fxml;
  exports com.example.progiiilab;
}