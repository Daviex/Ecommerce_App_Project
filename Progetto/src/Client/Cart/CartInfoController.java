package Client.Cart;

import Client.MainClient;
import Core.Cart.ProductInCart;
import Utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe che implementa Initializable. Rappresenta i dettagli per un prodotto nel carrello.
 */
public class CartInfoController implements Initializable {
    private ProductInCart productInfo;
    private CartController cartController;

    @FXML
    private Pane pane_cartInfo;
    @FXML
    private Text lbl_productTitle;
    @FXML
    private Text lbl_categoryName;
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
     * Metodo derivato da Initializable.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
     * Inizializza i dettagli riguardanti il prodotto acquistato.
     * @param productInfo Le informazioni sul prodotto acquistato.
     * @param cartController Rappresenta il pannello corrente.
     */
    public void initializePage(ProductInCart productInfo, CartController cartController) {
        this.productInfo = productInfo;
        this.cartController = cartController;

        String prodName = productInfo.product.getProductName();
        if(prodName.length() > 40)
        {
            String firstName = prodName.substring(0, 40);
            String secondName = prodName.substring(40, prodName.length());
            prodName = firstName + "\n" + secondName;
        }

        lbl_categoryName.setText("Categoria: " + productInfo.product.getCategoryName());

        lbl_productTitle.setText(prodName);
        txt_description.setText(productInfo.product.getProductDesc());

        lbl_priceNormal.setText(String.valueOf(productInfo.product.getProductPrice()) + " Euro");

        if(productInfo.product.hasDiscount()) {
            lbl_priceNormal.setStrikethrough(true);
            lbl_priceDiscounted.setText(String.valueOf(productInfo.product.getProductDiscount()) + " Euro");
        } else {
            lbl_priceDiscounted.setVisible(false);
        }

        txt_description.setWrapText(true);

        txt_quantity.setText(String.valueOf(productInfo.quantity));

        String url = "http://" + MainClient.siteAddress + "/Progetto/products/images/" + productInfo.product.getCategoryName().toLowerCase().replace(' ', '_') + "/" + productInfo.product.getProductImage();
        Image img = new Image(url);

        img_product.setImage(img);
    }

    /**
     * Evento sul bottone per aumentare la quantita' da voler acquistare.
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
     * Evento sul bottone per diminuire la quantita' da voler acquistare.
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

    /**
     * Evento sul bottone aggiorna, per aggiornare la quantita' da voler acquistare.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void updateQuantity(ActionEvent action) {
        int value = Integer.parseInt(txt_quantity.getText());

        if(value > 0 && value <= 100) {
            if(productInfo.CheckIfAvailable(value)) {
                if (MainClient.user.cart.updateQuantity(productInfo.product.getProductID(), value)) {
                    Util.MessageBox("Quantita' del prodotto prodotto aggiornata.");
                } else {
                    Util.MessageBox("Errore SetQuantity");
                }
            } else {
                if (MainClient.user.cart.updateQuantity(productInfo.product.getProductID(), productInfo.product.getProductQuantity())) {
                    Util.MessageBox("Quantita' non disponibile in magazzino.\nAggiornata al massimo disponibile.");
                } else {
                    Util.MessageBox("Errore SetQuantity");
                }
            }
        }
    }

    /**
     * Evento sul bottone rimuovi, per rimuovere il prodotto dal carrello.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void removeProductFromCart(ActionEvent action) {
        //If I remove an Item, it should update tree + pane with default pane.
        if(MainClient.user.cart.removeFromCart(productInfo.product.getProductID())){
            Util.MessageBox("Prodotto rimosso dal carrello con successo.");
            cartController.initializeDefaultCartPage();
        } else {
            Util.MessageBox("Errore durante la rimozione.");
        }
    }
}
