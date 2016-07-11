package Core.Catalogue;

import java.util.ArrayList;

/**
 * Classe contenente le informazioni su una categoria.
 * Contiene Getter & Setter.
 */
public class Category {

    private int id;
    private String name;
    private String desc;

    public ArrayList<Product> products;

    /**
     * Costruttore Category.
     * @param id ID Categoria.
     * @param name Nome Categoria.
     * @param desc Descrizione Categoria.
     */
    public Category(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;

        products = new ArrayList<Product>();
    }

    public int getCategoryID() { return id; }
    public String getCategoryName() { return name; }
    public String getCategoryDesc() { return desc; }

    /**
     * Aggiunge un prodotto alla categoria.
     * @param idProd ID Prodotto.
     * @param nameProd Nome Prodotto.
     * @param descProd Descrizione Prodotto.
     * @param priceProd Prezzo Prodotto.
     * @param discountProd Sconto Prodotto.
     * @param quantityProd Quantita' disponibile Prodotto.
     * @param imageProd Immagine del prodotto.
     * @param categoryName Nome della categoria che lo contiene.
     */
    public void setProduct(int idProd, String nameProd, String descProd, double priceProd, double discountProd, int quantityProd, String imageProd, String categoryName) {
        products.add(new Product(idProd, nameProd, descProd, priceProd, discountProd, quantityProd, imageProd, categoryName));
    }

    /**
     * Controlla se ha prodotti.
     * @return True se ci sono prodotti, False altrimenti.
     */
    public Boolean hasProducts() {
        if(products.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * Effettuo l'override del metodo toString della classe Object.
     * @return Nome della categoria.
     */
    @Override
    public String toString() { return name; }
}
