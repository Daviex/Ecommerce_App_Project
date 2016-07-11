package Client.Cart;

import Client.MainClient;
import Core.Cart.Cart;
import Core.Cart.ProductInCart;
import Core.Courier.Courier;
import Core.User.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Utils.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

/**
 * Classe che implementa Initializable. Rappresenta la pagina di conferma del checkout del carrello.
 */
public class CheckoutController implements Initializable {
    Utils Util;

    ArrayList<Courier> couriers;
    private double totalCart;
    private int indexCourierChosen;

    @FXML
    Text lbl_checkout;
    @FXML
    Text lbl_name;
    @FXML
    Text lbl_cf;
    @FXML
    Text lbl_address;
    @FXML
    Text lbl_city;
    @FXML
    Text lbl_telephone;
    @FXML
    Text lbl_email;
    @FXML
    Text lbl_selectedCourier;
    @FXML
    Text lbl_total;

    @FXML
    TableView tbl_couriers;
    @FXML
    TableColumn tbl_nameCour;
    @FXML
    TableColumn tbl_courCost;
    @FXML
    TableColumn tbl_expressTime;

    @FXML
    Button btn_checkout;

    CartController cartController;

    /**
     * InnerClass: Utilizzata per definire un tipo da associare alle colonne della Tabella.
     * Contiene Getter & Setter.
     */
    public class TableInfo {
        String courierName;
        double cost;
        String expressTime;

        public TableInfo(String courierName, double cost, String expressTime) {
            this.courierName = courierName;
            this.cost = cost;
            this.expressTime = expressTime;
        }

        public String getCourierName() { return courierName; }
        public void setCourierName(String cCourierName) { courierName = cCourierName; }

        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }

        public String getExpressTime() { return expressTime; }
        public void setExpressTime(String cExpressTime) { expressTime = cExpressTime; }
    }

    /**
     * Metodo derivato da Initializable.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Util = new Utils();
    }

    /**
     * Inizializza la pagina del checkout.
     * @param cartController Rappresenta il pannello corrente, per eventuali modifiche.
     */
    public void initializePage(CartController cartController) {
        this.cartController = cartController;
        User myUser = MainClient.user;
        lbl_name.setText("Nome: " + myUser.getName() + " " + myUser.getSurname());
        lbl_cf.setText("Codice Fiscale: " + myUser.getIdentitycode().toUpperCase());
        lbl_address.setText("Indirizzo: " + myUser.getAddress());
        lbl_city.setText(myUser.getCity() + ", " + myUser.getCap());
        lbl_telephone.setText("Telefono: " + myUser.getTelephone());
        lbl_email.setText("Email: " + myUser.getEmail());

        Cart myCart = MainClient.user.cart;

        JsonObject obj = new JsonObject();

        obj.add("clientRequest", new JsonPrimitive("loadCouriers"));

        MainClient.writer.write(obj);

        JsonObject jsonObject = MainClient.reader.readJson();

        if(jsonObject.get("result").getAsBoolean()) {
            Gson gson = new Gson();
            couriers = gson.fromJson(jsonObject.get("couriersInfo"), new TypeToken<ArrayList<Courier>>() {
            }.getType());

            tbl_nameCour.setCellValueFactory(
                    new PropertyValueFactory<TableInfo, String>("courierName")
            );
            tbl_courCost.setCellValueFactory(
                    new PropertyValueFactory<TableInfo, Double>("cost")
            );
            tbl_expressTime.setCellValueFactory(
                    new PropertyValueFactory<TableInfo, String>("expressTime")
            );

            ObservableList<TableInfo> data = FXCollections.observableArrayList();

            for(Courier courier : couriers) {
                data.add(new TableInfo(courier.getCourierName(), courier.getCourierCost(), courier.getCourierExpressTime()));
            }

            tbl_couriers.setItems(data);

            totalCart = 0;

            for (ProductInCart prod : myCart.productInCarts) {
                int quantity = prod.quantity;

                double totalDiscount = prod.product.getProductDiscount() * (double) quantity;
                totalCart += totalDiscount;
            }

            lbl_total.setText(String.format("Totale da pagare: %.2f", totalCart + couriers.get(0).getCourierCost()));
            lbl_selectedCourier.setText("Corriere scelto: " + couriers.get(0).getCourierName());

            tbl_couriers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    String courierName;
                    double currentCourierCost;

                    if(tbl_couriers.getSelectionModel().getSelectedItem() == null) {
                        courierName = couriers.get(0).getCourierName();
                        currentCourierCost = couriers.get(0).getCourierCost();
                        indexCourierChosen = 0;
                    } else {
                        TableInfo selected = (TableInfo)tbl_couriers.getSelectionModel().getSelectedItem();
                        currentCourierCost = selected.getCost();
                        courierName = selected.getCourierName();
                        indexCourierChosen = data.indexOf(selected);
                    }
                    lbl_selectedCourier.setText("Corriere scelto: " + courierName);
                    lbl_total.setText(String.format("Totale da pagare: %.2f", totalCart + currentCourierCost));
                }
            });
        }
    }

    /**
     * Evento sul bottone per la conferma. Conferma l'acquisto.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void checkoutCart(ActionEvent action) {
        /* Confirm the buy. Take all the things from cart, the courier chosen, the user and buy it! */
        JsonObject jObj = new JsonObject();
        Gson gson = new Gson();
        jObj.add("clientRequest", new JsonPrimitive("confirmCheckout"));
        //First of All, the cart
        jObj.add("cartInfo", new JsonParser().parse(gson.toJson(MainClient.user.cart)));
        //Now, Courier Chosen
        jObj.add("courierInfo", new JsonParser().parse(gson.toJson(couriers.get(indexCourierChosen))));
        //Now the user info
        jObj.add("userInfo", new JsonParser().parse(gson.toJson(MainClient.user)));

        MainClient.writer.write(jObj);

        //If Result of Insert is okay, clean the cart, and refresh back the cart page
        boolean result = MainClient.reader.readJson().get("result").getAsBoolean();

        if(result) {
            MainClient.user.cart = new Cart();
            Util.MessageBox("Acquisto effettuato con successo.\nLa ringraziamo per averci scelto.");
            cartController.initializeDefaultCartPage();
            MainClient.UpdateOrders();
        } else {
            Util.MessageBox("C'e' stato un errore durante l'acquisto.\nLa preghiamo di riprovare.");
        }
    }
}
