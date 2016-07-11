package Client.Reviews;

import Client.MainClient;

import java.net.URL;
import java.util.ResourceBundle;

import Core.Review.Review;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import Utils.*;

import Core.Catalogue.*;
import java.util.ArrayList;

import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Classe che implementa Initializable. Rappresenta le recensioni di un prodotto.
 */
public class ReviewsController implements Initializable {
    Product product;
    ArrayList<Review> reviews;

    Utils util;

    @FXML
    TreeView tree_reviews;

    @FXML
    Pane pane_review;

    /**
     * Metodo derivato da Initializable. Inizializza l'albero.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        util = new Utils();

        tree_reviews.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<Review> selectedItem = (TreeItem<Review>) newValue;
                if(selectedItem.getParent() == null) {
                    /* Root of the tree */
                    loadDefaultForm();
                } else if (selectedItem.getChildren().isEmpty()){
                    loadReview(selectedItem.getValue());
                }
            }
        });
    }

    /**
     * Imposta il prodotto di cui si vogliono visualizzare le recensioni.
     * @param product Prodotto da visualizzare.
     */
    public void setProductInfo(Product product) {
        this.product = product;
        loadReviews();
    }

    /**
     * Carica la recensione scelta sul pannello.
     * @param review Recensione da voler caricare.
     */
    public void loadReview(Review review) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/ReviewInfoForm.fxml"));
            pane_review.getChildren().clear();
            pane_review.getChildren().add(loader.load());
            ReviewInfoController controller = loader.<ReviewInfoController>getController();
            controller.setReview(review);
        } catch (Exception exc) {
            System.out.println("Errore-InitializeReviewsController: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Carica la pagina di riassunto su tutte le recensioni, e per aggiungerne / rimuoverne una.
     */
    public void loadDefaultForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/DefaultReviewForm.fxml"));
            pane_review.getChildren().clear();
            pane_review.getChildren().add(loader.load());
            DefaultReviewsController controller = loader.<DefaultReviewsController>getController();
            controller.setProductInfo(product);
            controller.setReviews(reviews);
        } catch (Exception exc) {
            System.out.println("Errore-InitializeReviewsController: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Carica le recensioni facendo la richiesta al server.
     */
    public void loadReviews() {
        reviews = new ArrayList<Review>();

        JsonObject obj = new JsonObject();

        obj.add("clientRequest", new JsonPrimitive("loadReviews"));
        obj.add("productID", new JsonPrimitive(product.getProductID()));

        MainClient.writer.write(obj);

        JsonObject result = MainClient.reader.readJson();

        TreeItem root = new TreeItem("Il Covo - Recensioni");
        root.setExpanded(true);

        if (result.get("result").getAsBoolean()) {
            Gson gson = new Gson();
            reviews = gson.fromJson(result.get("reviewsInfo"), new TypeToken<ArrayList<Review>>() {
            }.getType());

            TreeItem vote5 = new TreeItem("Voto 5");
            TreeItem vote4 = new TreeItem("Voto 4");
            TreeItem vote3 = new TreeItem("Voto 3");
            TreeItem vote2 = new TreeItem("Voto 2");
            TreeItem vote1 = new TreeItem("Voto 1");

            root.getChildren().add(vote5);
            root.getChildren().add(vote4);
            root.getChildren().add(vote3);
            root.getChildren().add(vote2);
            root.getChildren().add(vote1);

            for(Review review : reviews) {
                TreeItem<Review> treeReview = new TreeItem<Review>(review);
                switch(review.getVote()) {
                    case 1:
                        vote1.getChildren().add(treeReview);
                        break;
                    case 2:
                        vote2.getChildren().add(treeReview);
                        break;
                    case 3:
                        vote3.getChildren().add(treeReview);
                        break;
                    case 4:
                        vote4.getChildren().add(treeReview);
                        break;
                    case 5:
                        vote5.getChildren().add(treeReview);
                        break;
                }
            }
        }

        tree_reviews.setRoot(root);
        loadDefaultForm();
    }
}
