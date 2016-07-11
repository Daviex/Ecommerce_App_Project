package Client.Catalogue;

import Client.MainClient;
import Core.Catalogue.Catalogue;
import Core.Catalogue.Category;
import Core.Catalogue.Product;
import Core.Catalogue.Section;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.util.ResourceBundle;

import Utils.*;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

/**
 * Metodo che implementa Initializable. Rappresenta il catalogo prodotti dell'azienda.
 */
public class CatalogueController implements Initializable {
    Utils Util;

    @FXML
    Pane pane_productInfo;

    @FXML
    TreeView tree_catalogue;

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
     * Inizializza la pagina, caricando il catalogo.
     */
    public void initializePage() {
        JsonObject obj = new JsonObject();

        /* Initialization catalogue */
        obj.add("clientRequest", new JsonPrimitive("loadCatalogue"));
        MainClient.writer.write(obj);

        JsonObject catalogue = MainClient.reader.readJson();

        Boolean result = catalogue.get("result").getAsBoolean();

        if(result) {
            MainClient.catalogue = new Gson().fromJson(catalogue.get("catalogueInfo"), new TypeToken<Catalogue>() {}.getType());
        } else {
            Util.MessageBox("Non sono riuscito a scaricare il catalogo. Riprova più tardi.");
            MainClient.close();
        }

        setupTreeCatalogue();

        tree_catalogue.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<Product> selectedItem = (TreeItem<Product>) newValue;
                if(selectedItem.getChildren().isEmpty()) {
                    loadProductInfo(selectedItem.getValue());
                }
            }
        });
    }

    /**
     * Carica le informazioni su di un prodotto selezionato.
     * @param prod Prodotto selezionato.
     */
    private void loadProductInfo(Product prod) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/ProductInfo.fxml"));
            pane_productInfo.getChildren().clear();
            pane_productInfo.getChildren().add(loader.load());
            ProductInfoController controller = loader.<ProductInfoController>getController();
            controller.setProduct(prod);
        } catch ( Exception e ) {
            System.out.println("Errore-LoadProductInfo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Imposta l'albero contenente il catalogo.
     */
    private void setupTreeCatalogue() {
        try {
            TreeItem root = new TreeItem("Il Covo - Catalogo Prodotti");
            root.setExpanded(true);

            for(Section sec : MainClient.catalogue.getCatalogue()) {
                try {
                    TreeItem section = new TreeItem(sec.getSectionName());
                    root.getChildren().add(section);

                    for (Category cat : sec.categories) {
                        try {
                            TreeItem category = new TreeItem(cat.getCategoryName());
                            section.getChildren().add(category);
                            for (Product prod : cat.products) {
                                try {
                                    TreeItem<Product> product = new TreeItem<Product>(prod);
                                    category.getChildren().add(product);
                                } catch (Exception exc3) {
                                    System.out.println("Exception-SetupTree-Product " + exc3.getMessage());
                                    exc3.printStackTrace();
                                }
                            }
                        } catch (Exception exc2) {
                            System.out.println("Exception-SetupTree-Category " + exc2.getMessage());
                            exc2.printStackTrace();
                        }
                    }
                } catch(Exception exc1) {
                    System.out.println("Exception-SetupTree-Section " + exc1.getMessage());
                    exc1.printStackTrace();
                }
            }
            tree_catalogue.setRoot(root);
        } catch (Exception exc) {
            System.out.println("Exception-SetupTree-Root " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
