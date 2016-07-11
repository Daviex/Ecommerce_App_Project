package Core.Order;

import Core.Catalogue.Product;
import Core.Courier.Shipment;

/**
 * Classe con le informazioni per i prodotti in un ordine.
 * Contiene Getter & Setter.
 */
public class ProductInOrder {
    private int id;
    private double total;
    private int quantity;

    private Product product;
    private String state;
    private Shipment shipment;

    /**
     * Costruttore ProductInOrder.
     * @param id Id.
     * @param total Totale.
     * @param quantity Quantita'.
     * @param product Prodotto.
     * @param state Stato dell'evasione.
     * @param shipment Dati sulla spedizione.
     */
    public ProductInOrder(int id, double total, int quantity, Product product, String state, Shipment shipment) {
        this.id = id;
        this.total = total;
        this.quantity = quantity;
        this.product = product;
        this.state = state;
        this.shipment = shipment;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
}
