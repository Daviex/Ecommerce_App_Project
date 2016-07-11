package Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Implementa l'interfaccia di JavaFX. Crea una finestra dinamica, che si ridimensiona a seconda del testo contenuto.
 */
public class MessageBoxOKController implements Initializable {

    @FXML
    Text txt_message;

    @FXML
    Pane pane_box;

    @FXML
    Button btn_ok;

    Stage oldStage;

    /**
     * Override dell'interfaccia Initializable. Viene eseguito alla creazione della finestra.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    /**
     * Evento onAction per un bottone. Se viene premuto, chiude la finestra corrente, e se inserita, anche un altro stage.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void OKButton(ActionEvent action) {
        Stage mboxStage = (Stage)btn_ok.getScene().getWindow();
        mboxStage.close();

        if(oldStage != null) {
            oldStage.close();
        }
    }

    /**
     * Se passato uno stage, viene impostato sull'oggetto.
     * @param oldStage Lo stage di una finestra secondaria.
     */
    public void setOldStage(Stage oldStage) {
        this.oldStage = oldStage;
    }

    /**
     * Imposta il messaggio che verra' mostrato nella finestra.
     * @param text Il messaggio da mostrare.
     * @return La nuova grandezza della finestra.
     */
    public double setMessage(String text) {
        double widthBox = pane_box.getPrefWidth();
        double heightBox = pane_box.getPrefHeight();

        try {
            txt_message.setText(text);
            Bounds geom = txt_message.getBoundsInLocal();

            double width = geom.getWidth();
            double height = geom.getHeight();

            double buttonY = btn_ok.getLayoutY();
            btn_ok.setLayoutY(buttonY + height);

            return height + heightBox + buttonY;
        } catch(Exception e) {
            System.out.println("Errore-MessageBox-Show: " + e.getMessage());
            e.printStackTrace();
            return heightBox;
        }
    }
}
