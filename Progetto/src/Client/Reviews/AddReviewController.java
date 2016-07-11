package Client.Reviews;

import Client.MainClient;
import Core.Catalogue.Product;
import Utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe che implementa Initializable. Rappresenta la finestra per aggiungere una recensione.
 */
public class AddReviewController implements Initializable {
    Utils Util;

    int purchaseID;
    Product product;

    @FXML
    Text lbl_productName;
    @FXML
    TextField txt_title;
    @FXML
    ComboBox cmb_vote;
    @FXML
    TextArea txt_reviewDescription;
    @FXML
    Button btn_confirm;

    /**
     * Metodo derivato da Initializable. Inizializza la combobox coi voti.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Util = new Utils();

        ObservableList<Integer> votes = FXCollections.observableArrayList();
        votes.add(1);
        votes.add(2);
        votes.add(3);
        votes.add(4);
        votes.add(5);

        cmb_vote.setItems(votes);
    }

    /**
     * Inizializza la pagina.
     * @param purchaseID
     * @param product
     */
    public void initializePage(int purchaseID, Product product) {
        this.purchaseID = purchaseID;
        this.product = product;

        lbl_productName.setText(product.getProductName());
    }

    /**
     * Evento sul bottone conferma, aggiunge la recensione al database.
     * @param event Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void ConfirmReview(ActionEvent event) {
        if(CheckFields()) {
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.add("clientRequest", new JsonPrimitive("addReview"));
            jsonRequest.add("title", new JsonPrimitive(txt_title.getText()));
            jsonRequest.add("text", new JsonPrimitive(txt_reviewDescription.getText()));
            jsonRequest.add("vote", new JsonPrimitive((int)cmb_vote.getValue()));
            jsonRequest.add("purchaseID", new JsonPrimitive(purchaseID));

            MainClient.writer.write(jsonRequest);

            boolean result = MainClient.reader.readJson().get("result").getAsBoolean();

            if(result) {
                Stage addReviewStage = (Stage) btn_confirm.getScene().getWindow();
                addReviewStage.close();
                showReviews();
                Util.MessageBox("Recensione aggiunta con successo.");
            } else {
                Util.MessageBox("Errore durante l'inserimento.\nRiprova!");
            }
        }
    }

    /**
     * Ricarica la pagina delle recensioni.
     */
    private void showReviews() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/ReviewsForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Il Covo - Recensioni");
            stage.setResizable(false);
            stage.show();

            ReviewsController controller = loader.<ReviewsController>getController();
            controller.setProductInfo(product);

            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controlla i campi riempiti.
     * @return True se sono corretti, False altrimenti.
     */
    private boolean CheckFields() {
        if (txt_title.getText().length() > 60 && txt_title.getText().length() <= 0) {
            Util.MessageBox("Il titolo supera i 60 caratteri.\nRiprova!");
            return false;
        }
        if(txt_reviewDescription.getText().length() > 1300 && txt_reviewDescription.getText().length() <= 0) {
            Util.MessageBox("Il testo supera i 1300 caratteri.\nRiprova!");
            return false;
        }
        if((int)cmb_vote.getValue() <= 0 || (int)cmb_vote.getValue() > 5) {
            Util.MessageBox("Voto non valido.\nRiprova!");
            return false;
        }
        return true;
    }
}
