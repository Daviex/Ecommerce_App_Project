package Client.Orders;

import Client.MainClient;
import Core.Order.Order;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

import Utils.*;

/**
 * Classe che implementa Initializable. Rappresenta le informazioni riguardo agli ordini che sono stati effettuati
 * dall'utente in passato.
 */
public class OrdersController implements Initializable {
    Utils Util;

    @FXML
    ListView lst_orders;
    @FXML
    Pane pane_orderInfo;

    /**
     * Metodo derivato da Initializable. Inizializza l'evento della ListView per la selezione di un file.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Util = new Utils();

        lst_orders.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Order selectedItem = (Order)newValue;

                if(selectedItem != null) {
                    loadOrderInfo(selectedItem);
                }
            }
        });
    }

    /**
     * Inizializza la pagina, ricaricando gli ordini, e caricandoli nella lista.
     */
    public void initializePage() {
        /* Request to the server for all the orders I've done */
        MainClient.UpdateOrders();

        if(MainClient.user.orders.size() > 0) {
            ObservableList<Order> data = FXCollections.observableArrayList();

            for (Order order : MainClient.user.orders) {
                data.add(order);
            }

            lst_orders.setItems(data);
        }
    }

    /**
     * Viene richiamato se viene selezionato un elemento della ListView. Carica le informazioni
     * su di un singolo ordine nel Panel.
     * @param order Informazioni sull'ordine interessato.
     */
    public void loadOrderInfo(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/OrderInfoForm.fxml"));
            pane_orderInfo.getChildren().clear();
            pane_orderInfo.getChildren().add(loader.load());
            OrderInfoController controller = loader.<OrderInfoController>getController();
            controller.initializePage(order);
        } catch(Exception exc) {
            System.out.println("Errore-InitialieDefaultCartController: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
