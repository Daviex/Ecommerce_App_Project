package Client.Main;

import Client.Cart.CartController;
import Client.MainClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Client.Catalogue.CatalogueController;
import Client.Orders.OrdersController;
import Core.Cart.Cart;
import Core.Catalogue.Catalogue;
import Core.Order.Order;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import Utils.*;

import javafx.scene.layout.Pane;

/**
 * Classe che implementa Initializable. Rappresenta la finestra principale.
 */
public class MainFormController implements Initializable {

    @FXML
    private TabPane tabpane_main;

    @FXML
    private Tab tab_home;
    @FXML
    private Tab tab_cart;
    @FXML
    private Tab tab_orders;

    @FXML
    private Pane pane_cartMain;
    @FXML
    private Pane pane_catalogueMain;
    @FXML
    private Pane pane_ordersMain;

    Utils Util;

    /**
     * Metodo derivato da Initializable. Inizializza le diverse funzionalità disponibili
     * con l'applicazione.
     * @param url Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param rb Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Util = new Utils();

        /* Initialization catalogue */
        MainClient.catalogue = new Catalogue();
        initializeCataloguePage();

        /* Initialization cart */
        MainClient.user.cart = new Cart();

        /* Initialize orders */
        MainClient.user.orders = new ArrayList<Order>();
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.add("clientRequest", new JsonPrimitive("loadOrders"));
        jsonRequest.add("userID", new JsonPrimitive(MainClient.user.getId()));

        MainClient.writer.write(jsonRequest);

        JsonObject jsonResponse = MainClient.reader.readJson();

        if(jsonResponse.get("result").getAsBoolean()) {
            MainClient.user.orders = new Gson().fromJson(jsonResponse.get("ordersInfo"), new TypeToken<ArrayList<Order>>() {
            }.getType());
        }

        tabpane_main.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if(newValue == tab_home)
                    initializeCataloguePage();
                if(newValue == tab_cart)
                    initializeCartPage();
                if(newValue == tab_orders)
                    initializeOrdersPage();
            }
        });
    }

    /**
     * Inizializza il Panel del catalogo.
     */
    public void initializeCataloguePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/CataloguePage.fxml"));
            pane_catalogueMain.getChildren().clear();
            pane_catalogueMain.getChildren().add(loader.load());
            CatalogueController controller = loader.<CatalogueController>getController();
            controller.initializePage();
        } catch (Exception exc) {
            System.out.println("Errore-InitializeCataloguePage: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Inizializza il Panel del carrello.
     */
    public void initializeCartPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/CartForm.fxml"));
            pane_cartMain.getChildren().clear();
            pane_cartMain.getChildren().add(loader.load());
            CartController controller = loader.<CartController>getController();
            controller.initializeDefaultCartPage();
        } catch (Exception exc) {
            System.out.println("Errore-InitializeCartController: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Inizializza il Panel degli ordini.
     */
    public void initializeOrdersPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/OrdersForm.fxml"));
            pane_ordersMain.getChildren().clear();
            pane_ordersMain.getChildren().add(loader.load());
            OrdersController controller = loader.<OrdersController>getController();
            controller.initializePage();
        } catch (Exception exc) {
            System.out.println("Errore-InitializeOrdersPage: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Evento sul MenuFile, per mostrare l'About.
     * @param event Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void ShowAbout(ActionEvent event) {
        Util.MessageBox("ECommerce Desktop App \"Il Covo\".\nCreata da Davide Iuffrida.\nProgetto per Programmazione 2.\nProfessore: Massimo Villari");
    }

    /**
     * Evento sul MenuFile, per chiudere l'applicazione.
     * @param event Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void CloseWindow(ActionEvent event) {
        MainClient.close();
        System.exit(0);
    }
}
