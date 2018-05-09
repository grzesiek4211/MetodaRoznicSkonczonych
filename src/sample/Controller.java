package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    public TextField beta;
    @FXML
    public TextField epsilon;
    @FXML
    public Button save;
    @FXML
    public TextField potential;
    @FXML
    public Button start;
    @FXML
    public Button reset;
    @FXML
    public Label iterations;
    @FXML
    public Canvas canvas;

    public void onActionSave(ActionEvent actionEvent) {
    }

    public void onActionStart(ActionEvent actionEvent) {
    }

    public void onActionReset(ActionEvent actionEvent) {
    }

    public void initialize()
    {

    }
}
