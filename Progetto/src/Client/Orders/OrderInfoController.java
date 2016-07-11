package Client.Orders;

import Client.MainClient;
import Core.Courier.Courier;
import Core.Courier.Shipment;
import Core.Order.Order;
import Core.Order.ProductInOrder;
import Utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Classe che implementa Initializable. Rappresenta le informazioni di un ordine.
 */
public class OrderInfoController implements Initializable {
    Utils Util;
    Order order;

    @FXML
    Text lbl_orderInfo;
    @FXML
    Text lbl_total;
    @FXML
    TableView tbl_main;
    @FXML
    TableColumn tbl_product;
    @FXML
    TableColumn tbl_quantity;
    @FXML
    TableColumn tbl_price;
    @FXML
    TableColumn tbl_status;
    @FXML
    TableColumn tbl_startDate;
    @FXML
    TableColumn tbl_goalDate;

    @FXML
    Button btn_saveXML;

    /**
     * InnerClass: Utilizzata per definire un tipo da associare alle colonne della Tabella.
     * Contiene Getter & Setter.
     */
    public class TableInfo {
        private String productName;
        private int quantity;
        private double total;
        private String status;
        private String startDate;
        private String goalDate;

        /**
         * Costruttore TableInfo.
         * @param productName Nome Prodotto.
         * @param quantity Quantita'.
         * @param total Totale.
         * @param status Stato.
         * @param startDate Data di partenza.
         * @param goalDate Data di arrivo.
         */
        public TableInfo(String productName, int quantity, double total, String status, String startDate, String goalDate) {
            this.productName = productName;
            this.quantity = quantity;
            this.total = total;
            this.status = status;
            this.startDate = startDate;
            this.goalDate = goalDate;
        }

        public String getProductName() { return productName; }
        public void setProductName(String oProductName) { this.productName = productName; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int oQuantity) { this.quantity = oQuantity; }

        public double getTotal() { return total; }
        public void setTotal(double oTotal) { this.total = oTotal; }

        public String getStatus() { return status; }
        public void setStatus(String oStatus) { status = oStatus; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String oStartDate) { startDate = oStartDate; }

        public String getGoalDate() { return goalDate; }
        public void setGoalDate(String oGoalDate) { goalDate = oGoalDate; }
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
     * Metodo per inizializzare la pagina.
     * @param order Ordine da rappresentare.
     */
    public void initializePage(Order order) {
        this.order = order;

        tbl_product.setCellValueFactory(
                new PropertyValueFactory<TableInfo, String>("productName")
        );
        tbl_quantity.setCellValueFactory(
                new PropertyValueFactory<TableInfo, Integer>("quantity")
        );
        tbl_price.setCellValueFactory(
                new PropertyValueFactory<TableInfo, Double>("total")
        );
        tbl_status.setCellValueFactory(
                new PropertyValueFactory<TableInfo, String>("status")
        );
        tbl_startDate.setCellValueFactory(
                new PropertyValueFactory<TableInfo, String>("startDate")
        );
        tbl_goalDate.setCellValueFactory(
                new PropertyValueFactory<TableInfo, String>("goalDate")
        );

        ObservableList<TableInfo> data = FXCollections.observableArrayList();

        double totalOrder = 0;

        for(ProductInOrder product : order.getProductsInOrder()) {
            String productName = product.getProduct().getProductName();
            int quantity = product.getQuantity();
            double total = product.getTotal();
            String state = product.getState();

            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = null;

            if(product.getShipment().getStartDate() == null) {
                startDate = "N/D";
            } else {
                try {
                    startDate = Util.fromMySQLDate(sdf.format(product.getShipment().getStartDate()));
                } catch(Exception exc) {
                    System.out.println("Initializing OrderInfoPage-StartDate: " + exc.getMessage());
                    exc.printStackTrace();
                }
            }

            String goalDate = null;

            if(product.getShipment().getGoalDate() == null) {
                goalDate = "N/D";
            } else {
                try {
                    goalDate = Util.fromMySQLDate(sdf.format(product.getShipment().getGoalDate()));
                } catch(Exception exc) {
                    System.out.println("Initializing OrderInfoPage-GoalDate: " + exc.getMessage());
                    exc.printStackTrace();
                }
            }

            data.add(new TableInfo(productName, quantity, total, state, startDate, goalDate));

            //Calculate total
            totalOrder += product.getTotal();
        }

        tbl_main.setItems(data);

        lbl_orderInfo.setText("Informazioni dell'ordine del " + order.getPurchaseDate());
        lbl_total.setText(String.format("Totale pagato: %.2f Euro", totalOrder));
    }

    /**
     * Evento del bottone per salvare il file XML. Viene creato il file XML, riempito con i dati, e salvato
     * nella path: {root}/ordini/dataordine_nomeutente.xml
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void SaveXML(ActionEvent action) {
        Document document = Utils.InitializeXML("Ordini");

        Element idOrder = document.createElement("IDOrdine");
        idOrder.setTextContent(String.valueOf(order.getId()));
        Utils.AddElement(document.getDocumentElement(), idOrder);

        Element dateOrder = document.createElement("DataOrdine");
        dateOrder.setTextContent(order.toString());
        Utils.AddElement(document.getDocumentElement(), dateOrder);

        Element totalOrder = document.createElement("TotaleOrdine");
        idOrder.setTextContent(String.valueOf(order.calculateTotal()));
        Utils.AddElement(document.getDocumentElement(), totalOrder);

        for(ProductInOrder productInOrder : order.getProductsInOrder()) {
            Element product = document.createElement("ProdottoOrdine");
            product.setAttribute("id", String.valueOf(productInOrder.getId()));

            Element productName = document.createElement("NomeProdotto");
            productName.setAttribute("id", String.valueOf(productInOrder.getProduct().getProductID()));
            productName.setTextContent(productInOrder.getProduct().getProductName());
            Utils.AddElement(product, productName);

            Element totaleProduct = document.createElement("TotaleProdotto");
            totaleProduct.setTextContent(String.valueOf(productInOrder.getTotal()));
            Utils.AddElement(product, totaleProduct);

            Element quantityProduct = document.createElement("QuantitaProdotto");
            quantityProduct.setTextContent(String.valueOf(productInOrder.getQuantity()));
            Utils.AddElement(product, totaleProduct);

            Element stateOrder = document.createElement("StatoOrdineProdotto");
            stateOrder.setTextContent(productInOrder.getState());
            Utils.AddElement(product, stateOrder);

            Shipment tempShip = productInOrder.getShipment();
            Element shipmentOrder = document.createElement("SpedizioneOrdine");
            shipmentOrder.setAttribute("id", String.valueOf(tempShip.getId()));
            shipmentOrder.setAttribute("startDate", Utils.convertDate(tempShip.getStartDate()));
            shipmentOrder.setAttribute("goalDate", Utils.convertDate(tempShip.getStartDate()));
            shipmentOrder.setAttribute("startLocation", tempShip.getStartLocation());
            shipmentOrder.setAttribute("goalLocation", tempShip.getGoalLocation());
            shipmentOrder.setAttribute("addressGoal", tempShip.getAddressGoal());
            shipmentOrder.setAttribute("capGoal", String.valueOf(tempShip.getCapGoal()));

            Courier tempCourier = productInOrder.getShipment().getCourier();
            Element courier = document.createElement("CorriereSpedizione");
            courier.setAttribute("id", String.valueOf(tempCourier.getCourierID()));
            courier.setAttribute("name", tempCourier.getCourierName());
            courier.setAttribute("cost", String.valueOf(tempCourier.getCourierCost()));
            courier.setAttribute("expressTime", tempCourier.getCourierExpressTime());
            Utils.AddElement(shipmentOrder, courier);
            Utils.AddElement(product, shipmentOrder);

            Utils.AddElement(document.getDocumentElement(), product);
        }

        String folderName = "ordini";
        String purchaseDate = order.toString();
        if(purchaseDate != "N/D") {
            purchaseDate = purchaseDate.replace('-', '_').replace(' ', '_').replace(':', '_');
        }
        String fileName = purchaseDate + "_" + MainClient.user.getUsername().toLowerCase();

        Utils.CreateFolder(folderName);
        Utils.CreateXML(document, folderName+"/"+fileName);
    }
}
