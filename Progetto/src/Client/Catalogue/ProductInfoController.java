package Client.Catalogue;

import Client.MainClient;

import java.net.URL;
import java.util.ResourceBundle;

import Client.Reviews.ReviewsController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import Utils.*;

import Core.Catalogue.*;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Classe che implementa Initializable. Rappresenta le informazioni di un singolo prodotto nel catalogo.
 */
public class ProductInfoController implements Initializable {
    private Product productInfo;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Text lbl_productTitle;
    @FXML
    private Text lbl_priceNormal;
    @FXML
    private Text lbl_priceDiscounted;
    @FXML
    private ImageView img_product;
    @FXML
    private TextField txt_quantity;
    @FXML
    private TextArea txt_description;
    @FXML
    private Button btn_buy;
    @FXML
    private Button btn_reviews;

    Utils Util;

    /**
     * Metodo derivato da Initializable. Imposta l'evento alla TextField per la quantita'.
     * @param url Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param rb Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        txt_quantity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue,
                                String oldValue, String newValue) {
                try {
                    int value = Integer.parseInt(newValue);

                    if (value <= 0 || value > 100)
                        txt_quantity.setText(oldValue);
                    else
                        txt_quantity.setText(newValue);
                } catch (NumberFormatException ex) {
                    txt_quantity.setText(oldValue);
                }
            }
        });

        Util = new Utils();
    }

    /**
     * Inizializza il prodotto selezionato.
     * @param productInfo
     */
    public void setProduct(Product productInfo) {
        this.productInfo = productInfo;

        initializeInfoProduct();
    }

    /**
     * Inizializza la pagina riempendo le componenti con i dati del prodotto.
     */
    private void initializeInfoProduct() {
        String prodName = productInfo.getProductName();
        if(prodName.length() > 40)
        {
            String firstName = prodName.substring(0, 40);
            String secondName = prodName.substring(40, prodName.length());
            prodName = firstName + "\n" + secondName;
        }

        lbl_productTitle.setText(prodName);
        txt_description.setText(productInfo.getProductDesc());

        lbl_priceNormal.setText(String.format("%.2f Euro", productInfo.getProductPrice()));

        if(productInfo.hasDiscount()) {
            lbl_priceNormal.setStrikethrough(true);
            lbl_priceDiscounted.setText(String.format("%.2f Euro", productInfo.getProductDiscount()));
        } else {
            lbl_priceDiscounted.setVisible(false);
        }

        txt_description.setWrapText(true);

        String url = "http://" + MainClient.siteAddress + "/Progetto/products/images/" + productInfo.getCategoryName().toLowerCase().replace(' ', '_') + "/" + productInfo.getProductImage();
        Image img = new Image(url);

        img_product.setImage(img);

        if(productInfo.getProductQuantity() <= 0)
            btn_buy.setDisable(true);
    }

    /**
     * Evento sul bottone per visualizzare le recensioni. Se attivato, apre una nuova finestra
     * con le recensioni
     * del prodotto.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void showReviews(ActionEvent action) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/ReviewsForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Il Covo - Recensioni");
            stage.setResizable(false);
            stage.show();

            ReviewsController controller = loader.<ReviewsController>getController();
            controller.setProductInfo(productInfo);

            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Evento sul bottone acquista. Aggiunge il prodotto al carrello.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void addProductToCart(ActionEvent action) {
        if(MainClient.user.cart.addToCart(productInfo, Integer.parseInt(txt_quantity.getText()))) {
            Util.MessageBox("Prodotto inserito nel carrello con successo.");
        }
        else {
            Util.MessageBox("Errore durante l'inserimento del prodotto nel carrello.");
        }
    }

    /**
     * Evento sul bottone incrementa quantita'. Incrementa la quantita' del prodotto che si vuole acquistare.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void increaseQuantity(ActionEvent action)
    {
        int value = Integer.parseInt(txt_quantity.getText()) + 1;

        if(value > 100)
            value = 100;

        txt_quantity.setText(String.valueOf(value));
    }

    /**
     * Evento sul bottone decrementa quantita'. Decrementa la quantita' del prodotto che si vuole acquistare.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void decreaseQuantity(ActionEvent action)
    {
        int value = Integer.parseInt(txt_quantity.getText()) - 1;

        if(value <= 0)
            value = 1;

        txt_quantity.setText(String.valueOf(value));
    }
}
