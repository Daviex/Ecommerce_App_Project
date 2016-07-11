package Client.Cart;

import Client.MainClient;
import Core.Cart.Cart;
import Core.Cart.ProductInCart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe che implementa Initializable. Rappresenta il riepilogo del carrello prima del checkout.
 */
public class DefaultCartController implements Initializable {
    @FXML
    Text lbl_myCart;
    @FXML
    TableView tbl_main;
    @FXML
    TableColumn tbl_product;
    @FXML
    TableColumn tbl_quantity;
    @FXML
    TableColumn tbl_price;
    @FXML
    TableColumn tbl_percentage;
    @FXML
    TableColumn tbl_total;
    @FXML
    Text lbl_total;
    @FXML
    Text lbl_totalDiscount;
    @FXML
    Button btn_checkout;

    Pane pane_cart;

    CartController cartController;

    /**
     * InnerClass: Utilizzata per definire un tipo da associare alle colonne della Tabella.
     * Contiene Getter & Setter.
     */
    public class TableInfo {
        String productName;
        int quantity;
        double productPrice;
        double discount;
        double total;

        /**
         * Costruttore TableInfo
         * @param productName Nome Prodotto
         * @param quantity Quantita'
         * @param productPrice Prezzo Prodotto
         * @param discount Percentuale Sconto
         * @param total Totale Prodotto
         */
        public TableInfo(String productName, int quantity, double productPrice, double discount, double total) {
            this.productName = productName;
            this.quantity = quantity;
            this.productPrice = productPrice;
            this.discount = discount;
            this.total = total;
        }

        public String getProductName() { return productName; }
        public void setProductName(String pName) { productName = pName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getProductPrice() { return productPrice; }
        public void setProductPrice(double pPrice) { productPrice = pPrice; }
        public double getDiscount() { return discount; }
        public void setDiscount(double discount) { this.discount = discount; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
    }

    /**
     * Metodo derivato da Initializable.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    /**
     * Inizializza la pagina del riepilogo con i dati.
     * @param cart Panel su cui inserire gli elementi vari.
     * @param cartController Controller per avere informazioni sul pannello corrente.
     */
    public void initializePage(Pane cart, CartController cartController) {
        pane_cart = cart;
        this.cartController = cartController;
        Cart myCart = MainClient.user.cart;

        tbl_product.setCellValueFactory(
                new PropertyValueFactory<TableInfo, String>("productName")
        );
        tbl_quantity.setCellValueFactory(
                new PropertyValueFactory<TableInfo, Integer>("quantity")
        );
        tbl_price.setCellValueFactory(
                new PropertyValueFactory<TableInfo, Double>("productPrice")
        );
        tbl_percentage.setCellValueFactory(
                new PropertyValueFactory<TableInfo, Double>("discount")
        );
        tbl_total.setCellValueFactory(
                new PropertyValueFactory<TableInfo, Double>("total")
        );

        ObservableList<TableInfo> data = FXCollections.observableArrayList();

        double totalCart = 0;
        double totalDiscountCart = 0;

        for(ProductInCart prod : myCart.productInCarts) {
            String productName = prod.product.getProductName();
            int quantity = prod.quantity;
            double productPrice = prod.product.getProductPrice();
            double discount = prod.product.getProductDiscountPercentage();

            double total = prod.product.getProductPrice() * (double)quantity;
            totalCart += total;
            double totalDiscount = prod.product.getProductDiscount() * (double)quantity;
            totalDiscountCart += totalDiscount;

            data.add(new TableInfo(productName, quantity, productPrice, discount, totalDiscount));
        }

        totalDiscountCart = totalCart - totalDiscountCart;

        lbl_total.setText(String.format("Totale: %.2f Euro", totalCart));
        lbl_totalDiscount.setText(String.format("Risparmio: %.2f Euro", totalDiscountCart));

        tbl_main.setItems(data);

        if(myCart.productInCarts.size() > 0) {
            btn_checkout.setDisable(false);
        } else {
            btn_checkout.setDisable(true);
        }
    }

    /**
     * Evento sul bottone per il checkout. Premendolo, si potrà procedere al checkout.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void CheckoutCart(ActionEvent action) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/CheckoutForm.fxml"));
            pane_cart.getChildren().clear();
            pane_cart.getChildren().add(loader.load());
            CheckoutController controller = loader.<CheckoutController>getController();
            controller.initializePage(cartController);
        } catch(Exception exc) {
            System.out.println("Errore-InitialieDefaultCartController: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
