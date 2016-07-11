package Client.Reviews;

import java.net.URL;
import java.util.ResourceBundle;

import Client.MainClient;
import Core.Catalogue.Product;

import Core.Order.Order;
import Core.Order.ProductInOrder;
import Core.Review.Review;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import Utils.*;

import java.util.ArrayList;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Classe che implementa Initializable. Rappresenta la pagina di riassunto di tutte le recensioni di un prodotto.
 */
public class DefaultReviewsController implements Initializable {
    Product product;
    ArrayList<Review> reviews;
    int purchaseID;

    Utils Util;

    @FXML
    Text lbl_productName;
    @FXML
    Text lbl_defaultReview;
    @FXML
    Text lbl_user_date;
    @FXML
    Text lbl_title;
    @FXML
    Text lbl_vote;
    @FXML
    TextArea txt_reviewDescription;

    @FXML
    Text lbl_averageVotes;
    @FXML
    Text lbl_numVotes5;
    @FXML
    Text lbl_numVotes4;
    @FXML
    Text lbl_numVotes3;
    @FXML
    Text lbl_numVotes2;
    @FXML
    Text lbl_numVotes1;

    @FXML
    ProgressBar prgr_vote5;
    @FXML
    ProgressBar prgr_vote4;
    @FXML
    ProgressBar prgr_vote3;
    @FXML
    ProgressBar prgr_vote2;
    @FXML
    ProgressBar prgr_vote1;

    @FXML
    Button btn_addReview;
    @FXML
    Button btn_removeReview;

    /**
     * Metodo derivato da Initializable. Inizializza l'albero.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Util = new Utils();
    }

    /**
     * Imposta il prodotto di cui si devono visualizzare le recensioni.
     * @param product Prodotto selezionato.
     */
    public void setProductInfo(Product product) { this.product = product;  }

    /**
     * Imposta le recensioni da visualizzare.
     * @param reviews Tutte le recensioni su quel prodotto.
     */
    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        initializePanel();
    }

    /**
     * Inizializza il pannello con i dati generali sulle recensioni.
     */
    public void initializePanel() {
        lbl_productName.setText(product.getProductName());

        int averageVotes = 0;
        int vote1Count = 0, vote2Count = 0, vote3Count = 0, vote4Count = 0, vote5Count = 0;
        int numberOfVotes = reviews.size();

        Review myReview = null;

        int idAcq = -1;

        for (Order order : MainClient.user.orders) {
            for (ProductInOrder productInOrder : order.getProductsInOrder()) {
                if (productInOrder.getProduct().getProductID() == product.getProductID()) {
                    idAcq = productInOrder.getId();
                    break;
                }
            }
            if (idAcq != -1)
                break;
        }

        if(reviews.size() > 0) {
            for (Review review : reviews) {
                averageVotes += review.getVote();
                if (review.getVote() == 1)
                    vote1Count++;
                else if (review.getVote() == 2)
                    vote2Count++;
                else if (review.getVote() == 3)
                    vote3Count++;
                else if (review.getVote() == 4)
                    vote4Count++;
                else if (review.getVote() == 5)
                    vote5Count++;

                if (review.getUserID() == MainClient.user.getId()) {
                    myReview = review;
                }
            }

            averageVotes /= numberOfVotes;
            lbl_averageVotes.setText("Media voti: " + averageVotes + "/5");

        /*Vote 1 Calculation*/
            double vote1Progress = (1 / (double) numberOfVotes) * (double) vote1Count;
            prgr_vote1.setProgress(vote1Progress);
            lbl_numVotes1.setText(vote1Count + " / " + numberOfVotes);
        /*Vote 2 Calculation*/
            double vote2Progress = (1 / (double) numberOfVotes) * (double) vote2Count;
            prgr_vote2.setProgress(vote2Progress);
            lbl_numVotes2.setText(vote2Count + " / " + numberOfVotes);
        /*Vote 3 Calculation*/
            double vote3Progress = (1 / (double) numberOfVotes) * (double) vote3Count;
            prgr_vote3.setProgress(vote3Progress);
            lbl_numVotes3.setText(vote3Count + " /  " + numberOfVotes);
        /*Vote 4 Calculation*/
            double vote4Progress = (1 / (double) numberOfVotes) * (double) vote4Count;
            prgr_vote4.setProgress(vote4Progress);
            lbl_numVotes4.setText(vote4Count + " /  " + numberOfVotes);
        /*Vote 5 Calculation*/
            double vote5Progress = (1 / (double) numberOfVotes) * (double) vote5Count;
            prgr_vote5.setProgress(vote5Progress);
            lbl_numVotes5.setText(vote5Count + " /  " + numberOfVotes);


        /* Check I bought the product */
            if (myReview == null) {
                //I print the last review added.
                lbl_defaultReview.setText("Ultima recensione inserita:");
                String nome = reviews.get(0).getUserName();
                String date = reviews.get(0).getDate();

                String[] name = nome.split(" ");

                nome = name[0] + " " + name[1].charAt(0) + ".";

                lbl_user_date.setText("Di: " + nome + " il: " + Utils.fromMySQLDate(date.split(" ")[0]));
                lbl_title.setText("Titolo: " + reviews.get(0).getTitle());
                lbl_vote.setText("Voto: " + String.valueOf(reviews.get(0).getVote()) + "/5");
                txt_reviewDescription.setText(reviews.get(0).getText());

                if (idAcq != -1) {
                    purchaseID = idAcq;
                    btn_addReview.setDisable(false);
                }
            } else {
                //I print my review and enable remove review.
                lbl_defaultReview.setText("La tua recensione:");
                String nome = myReview.getUserName();
                String date = myReview.getDate();

                String[] name = nome.split(" ");

                nome = name[0] + " " + name[1].charAt(0) + ".";

                lbl_user_date.setText("Di: " + nome + " il: " + Utils.fromMySQLDate(date.split(" ")[0]));
                lbl_title.setText("Titolo: " + myReview.getTitle());
                lbl_vote.setText("Voto: " + String.valueOf(myReview.getVote()) + "/5");
                txt_reviewDescription.setText(myReview.getText());

                purchaseID = idAcq;
                btn_removeReview.setDisable(false);
            }
        } else {
            lbl_averageVotes.setText("Media voti: 0/5");

            lbl_defaultReview.setVisible(false);
            lbl_user_date.setVisible(false);
            lbl_title.setVisible(false);
            lbl_vote.setVisible(false);
            txt_reviewDescription.setVisible(false);

            if (idAcq != -1) {
                purchaseID = idAcq;
                btn_addReview.setDisable(false);
            }
        }
    }

    /**
     * Evento sul bottone aggiungi, permette di aggiungere una nuova recensione se si ha acquistato il prodotto.
     * @param event Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void AddReview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/AddReviewForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Il Covo - Aggiungi Recensione");
            stage.setResizable(false);
            stage.show();

            AddReviewController controller = loader.<AddReviewController>getController();
            controller.initializePage(purchaseID, product);

            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage reviewStage = (Stage)btn_addReview.getScene().getWindow();
        reviewStage.close();
    }

    /**
     * Evento sul bottone rimuovi, rimuove la recensione inserita dall'utente precedentemente.
     * @param event Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void RemoveReview(ActionEvent event) {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.add("clientRequest", new JsonPrimitive("removeReview"));
        jsonRequest.add("purchaseID", new JsonPrimitive(purchaseID));

        MainClient.writer.write(jsonRequest);
        boolean result = MainClient.reader.readJson().get("result").getAsBoolean();

        if(result) {
            Stage reviewStage = (Stage)btn_addReview.getScene().getWindow();
            reviewStage.close();
            showReviews();
            Util.MessageBox("Recensione rimossa con successo.");
        } else {
            Util.MessageBox("Errore durante la rimozione.\nRiprova!");
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
}
