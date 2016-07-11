package Client.Cart;

import Client.MainClient;
import Core.Cart.ProductInCart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe che implementa Initializable. Rappresenta il carrello dell'utente.
 */
public class CartController implements Initializable {
    @FXML
    TreeView tree_cart;

    @FXML
    Pane pane_cartInfo;

    CartController cartController;

    /**
     * Metodo derivato da Initializable. Inizializza l'albero.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cartController = this;

        tree_cart.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<ProductInCart> selectedItem = (TreeItem<ProductInCart>) newValue;
                if (selectedItem != null) {
                    if (selectedItem.getChildren().isEmpty() && selectedItem.getParent() != null) {
                        loadProductInCart(selectedItem.getValue(), cartController);
                    } else {
                        loadDefaultCart(cartController);
                    }
                }
            }
        });
    }

    /**
     * Inizializza la pagina di default del carrello, con un riepilogo dell'ordine, ed il bottone di checkout.
     */
    public void initializeDefaultCartPage() {
        loadDefaultCart(cartController);

        TreeItem root = new TreeItem("Il Covo - Carrello");
        root.setExpanded(true);

        for(ProductInCart prodInCart : MainClient.user.cart.productInCarts) {
            TreeItem<ProductInCart> product = new TreeItem<ProductInCart>(prodInCart);
            root.getChildren().add(product);
        }

        tree_cart.setRoot(root);
    }

    /**
     * Carica la pagina predefinita del carrello.
     * @param cartController Il controller di questa pagina, per poter effettuare modifiche su di un suo panel.
     */
    public void loadDefaultCart(CartController cartController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/DefaultCartForm.fxml"));
            pane_cartInfo.getChildren().clear();
            pane_cartInfo.getChildren().add(loader.load());
            DefaultCartController controller = loader.<DefaultCartController>getController();
            controller.initializePage(pane_cartInfo, cartController);
        } catch(Exception exc) {
            System.out.println("Errore-InitialieDefaultCartController: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Carica i dettagli di un prodotto all'interno del carrello su di un Panel.
     * @param product Prodotto del carrello.
     * @param cartController Il controller di questa pagina, per poter effettuare modifiche su di un suo panel.
     */
    public void loadProductInCart(ProductInCart product, CartController cartController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/CartInfoForm.fxml"));
            pane_cartInfo.getChildren().clear();
            pane_cartInfo.getChildren().add(loader.load());
            CartInfoController controller = loader.<CartInfoController>getController();
            controller.initializePage(product, cartController);
        } catch (Exception exc) {
            System.out.println("Errore-InitializeCartInfoController: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
