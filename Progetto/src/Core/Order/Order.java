package Core.Order;

import Utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe contenente le informazioni di un ordine.
 * Contiene Getter & Setter.
 */
public class Order {
    Utils Util;

    private int id;
    private Date purchaseDate;
    private ArrayList<ProductInOrder> productsInOrder;

    /**
     * Costruttore Order.
     * @param id ID Ordine.
     * @param date Data Ordine.
     */
    public Order(int id, String date) {
        Util = new Utils();
        this.id = id;

        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            purchaseDate = sdf.parse(date);
        } catch(Exception exc) {
            System.out.println("Errore ConstructorDateOrder: " + exc.getMessage());
            exc.printStackTrace();
        }

        productsInOrder = new ArrayList<ProductInOrder>();
    }

    /**
     * Aggiunge i dettagli di un prodotto all'ordine.
     * @param product Dettagli di un prodotto acquistato.
     */
    public void addProduct(ProductInOrder product) {
        productsInOrder.add(product);
    }
    public ArrayList<ProductInOrder> getProductsInOrder() { return productsInOrder; }
    public int getId() { return id; }

    /**
     * Calcola il totale dell'ordine.
     * @return Totale dell'ordine.
     */
    public double calculateTotal() {
        double total = 0;
        for(ProductInOrder product : getProductsInOrder()) {
            total += product.getTotal();
        }

        return total;
    }

    /**
     * Controlla se ci sono prodotti in quest'ordine.
     * @return Se ci sono, allora ritorna True, False altrimenti.
     */
    public boolean hasProducts() {
        if(productsInOrder.size() > 0)
            return true;
        else
            return false;
    }

    /**
     * Converte la data dell'acquisto.
     * @return La data convertita, N/D se non esiste.
     */
    public String getPurchaseDate() {
        String result = "N/D";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            result = Util.fromMySQLDate(sdf.format(purchaseDate)).replace("-", "/");
        } catch(Exception exc) {
            System.out.println("Initializing OrderInfoPage-StartDate: " + exc.getMessage());
            exc.printStackTrace();
        }
        return result;
    }

    /**
     * Effettuo l'override del metodo toString della classe Object.
     * @return La data convertita in stringa.
     */
    @Override
    public String toString() {
        String date = null;
        try {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = sdf.format(purchaseDate);
        } catch(Exception exc) {
            System.out.println("Errore ToString-Order: " + exc.getMessage());
            exc.printStackTrace();
        }
        return date;
    }
}
