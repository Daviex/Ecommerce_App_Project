package Core.Catalogue;

/**
 * Classe contenente le informazioni di un prodotto.
 * Contiene Getter & Setter.
 */
public class Product {

    private int id;
    private String name;
    private String desc;

    private double price;
    private double discountPercentage;
    private int quantity;

    private String imageProduct;

    private String categoryName;

    /**
     * Costruttore Product.
     * @param id ID Prodotto.
     * @param name Nome Prodotto.
     * @param desc Descrizione Prodotto.
     * @param price Prezzo Prodotto.
     * @param discountPercentage Percentuale di Sconto.
     * @param quantity Quantità Disponibile del Prodotto.
     * @param imageProduct Immagine Prodotto.
     * @param categoryName Nome della categoria in cui è contenuto.
     */
    public Product(int id, String name, String desc, double price, double discountPercentage, int quantity, String imageProduct, String categoryName) {
        this.id = id;
        this.name = name;
        this.desc = desc;

        this.price = price;
        this.discountPercentage = discountPercentage;
        this.quantity = quantity;

        this.imageProduct = imageProduct;
        this.categoryName = categoryName;
    }

    public int getProductID() { return id; }
    public String getProductName() { return name; }
    public String getProductDesc() { return desc; }
    public double getProductPrice() { return price; }
    public boolean hasDiscount() { return discountPercentage != 0 ? true : false; }
    public double getProductDiscount() { return price - ( ( price / 100 ) * discountPercentage ); }
    public String getCategoryName() { return categoryName; }
    public double getProductDiscountPercentage() { return discountPercentage; }
    public int getProductQuantity() { return quantity; }
    public String getProductImage() { return imageProduct; }
    public void setQuantity(int newValue) { quantity = newValue; }

    /**
     * Effettuo l'override del metodo toString della classe Object.
     * @return Nome del prodotto.
     */
    @Override
    public String toString() { return name; }
}
