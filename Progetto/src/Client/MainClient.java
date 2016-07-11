package Client;

import java.io.File;
import java.net.*;
import java.util.ArrayList;

import Client.Login.LoginFormController;
import Core.Catalogue.Catalogue;
import Core.Order.Order;
import Core.User.User;
import Utils.StreamsJSON.*;
import Utils.*;

import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.google.gson.*;
import org.w3c.dom.Document;

/**
 * Classe principale del Client.
 * Contiene informazioni riguardo la socket, i manager per gli Stream in lettura e scrittura,
 * L'utente che si e' collegato, ed il catalogo prodotti.
 */
public class MainClient extends Application{
    public static Socket clientSocket;
    public static JSONReader reader;
    public static JSONWriter writer;

    public static User user;
    public static Catalogue catalogue;
    public static Document ConfigFile;

    public static String siteAddress;

    /**
     * Metodo start, derivato da Application. Esegue un codice alla creazione della finestra.
     * @param stage Finestra su cui disegnare la mia GUI.
     */
    @Override
    public void start(Stage stage) {
        Utils Util = new Utils();
        if(new File("config/clientConfig.xml").exists()) {
            ConfigFile = Utils.ReadXML("config/clientConfig.xml");
            siteAddress = ConfigFile.getElementsByTagName("SiteAddress").item(0).getTextContent();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client//FXML Forms/Login.fxml"));
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Il Covo - Login");
                stage.setResizable(false);
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent we) {
                        close();
                        System.exit(0);
                    }
                });
                stage.show();
                stage.sizeToScene();
                LoginFormController controller = loader.<LoginFormController>getController();
                controller.setXML(ConfigFile);
            } catch (Exception exc) {
                System.out.println("Errore MainClient start: " + exc.getMessage());
                exc.printStackTrace();
            }
        } else {
            Util.MessageBox("Non ho trovato il file di configurazione.\nPosizionalo nella cartella config e riprova!");
        }
    }

    /**
     * Metodo Main del Client. Avvia l'applicazione.
     * @param args Parametri passati all'esecuzione dell'applicazione.
     */
	public static void main(String[] args) {
        launch(args);
	}

    /**
     * Aggiorna gli ordini dell'utente, controllandone prima la corrispondenza nel numero.
     */
    public static void UpdateOrders() {
        JsonObject jsonRequest = new JsonObject();

        jsonRequest.add("clientRequest", new JsonPrimitive("checkNumberOfOrders"));
        jsonRequest.add("userID", new JsonPrimitive(user.getId()));

        writer.write(jsonRequest);

        JsonObject jsonResponse = reader.readJson();

        if (jsonResponse.get("result").getAsInt() != user.orders.size()) {
            jsonRequest.add("clientRequest", new JsonPrimitive("loadOrders"));
            jsonRequest.add("userID", new JsonPrimitive(user.getId()));

            writer.write(jsonRequest);

            jsonResponse = reader.readJson();

            if (jsonResponse.get("result").getAsBoolean()) {
                user.orders = new Gson().fromJson(jsonResponse.get("ordersInfo"), new TypeToken<ArrayList<Order>>() {
                }.getType());
            }
        }
    }

    /**
     * Chiude la connessione col server.
     */
    public static void close() {
        if(clientSocket != null) {
            try {
                JsonObject obj = new JsonObject();
                obj.add("clientRequest", new JsonPrimitive("closeConnection"));
                writer.write(obj);
            } catch (Exception e) {
                System.out.println("Errore ChiusuraClient: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
