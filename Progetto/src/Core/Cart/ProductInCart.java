package Core.Cart;

import Core.Catalogue.Product;

/**
 * Created by david on 26/10/2015.
 */
public class ProductInCart {
    public Product product;
    public int quantity;

    public ProductInCart(Product product, int quantity) {
        this.product = product;
        if(product.getProductQuantity() < quantity)
            this.quantity = product.getProductQuantity();
        else
            this.quantity = quantity;
    }


    /**
     * Effettuo l'override del metodo toString della classe Object.
     * @return Nome del prodotto.
     */
    @Override
    public String toString() { return product.getProductName(); }

    public boolean CheckIfAvailable(int newQuantity) {
        if(product.getProductQuantity() < newQuantity) {
            return false;
        } else {
            return true;
        }
    }
}
