package Core.Cart;

import Core.Catalogue.Product;

import java.util.ArrayList;

/**
 * Classe contenente le informaioni sul carrello.
 */
public class Cart {

    public ArrayList<ProductInCart> productInCarts;

    /**
     * Costruttore Cart. Inizializza i prodotti nel carrello ad una lista vuota.
     */
    public Cart() {
        productInCarts = new ArrayList<ProductInCart>();
    }

    /**
     * Aggiunge un prodotto al carrello.
     * @param prod Prodotto da inserire nel carrello.
     * @param quantity Quantita' da voler acquistare.
     * @return Risultato dell'inserimento nel carrello, potrebbe essere gia' presente.
     */
    public boolean addToCart(Product prod, int quantity) {
        //Check if already exist
        for(ProductInCart product : productInCarts) {
            if(product.product == prod)
                return false;
        }
        //Try to add it
        if(productInCarts.add(new ProductInCart(prod, quantity)))
            return true;
        else
            return false;
    }

    /**
     * Rimuove un prodotto dal carrello.
     * @param productID ID del prodotto da voler rimuovere.
     * @return Risultato della rimozione.
     */
    public boolean removeFromCart(int productID) {
        //Check if exist
        for(ProductInCart prod : productInCarts) {
            if(prod.product.getProductID() == productID) {
                productInCarts.remove(prod);
                return true;
            }
        }
        return false;
    }

    /**
     * Aggiorna la quantita' di un prodotto nel carrello.
     * @param productID ID del prodotto di cui si vuole aggiornare la quantita'.
     * @param newValue Nuovo valore del prodotto nel carrello.
     * @return Se e' stato aggiornato con successo, ritorna True, altrimenti False.
     */
    public boolean updateQuantity(int productID, int newValue) {
        //Check if exist
        for(ProductInCart prod : productInCarts) {
            if(prod.product.getProductID() == productID) {
                prod.quantity = newValue;
                return true;
            }
        }
        return false;
    }
}
