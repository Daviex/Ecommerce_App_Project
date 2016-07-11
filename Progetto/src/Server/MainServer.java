package Server;

import Core.Cart.Cart;
import Core.Courier.Courier;
import Core.User.*;

import Utils.*;
import Utils.StreamsJSON.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;

import java.io.*;
import java.net.*;

/**
 * Classe col metodo Main per il server.
 */
public class MainServer {

    /**
     * Metodo Main per il server.
     * @param args Contiene eventuali informazione passate come parametro all'avvio dell'applicazione.
     */
	public static void main(String[] args) {
        if(new File("config/clientConfig.xml").exists()) {
            Document document = Utils.ReadXML("config/serverConfig.xml");

            final int SocketPort = Integer.parseInt(document.getElementsByTagName("SocketPort").item(0).getTextContent());

            try {
                ServerSocket serverSocket = new ServerSocket(SocketPort);
                try {
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        new ServerThread(clientSocket, document);
                    }
                } catch (Exception e) {
                    System.out.println("Errore: " + e.getMessage());
                    e.printStackTrace();
                }
                serverSocket.close();
            } catch (IOException IOExc) {
                System.out.println("Errore: " + IOExc.getMessage());
                IOExc.printStackTrace();
            }
        } else {
            System.out.println("Non ho trovato il file di configurazione.\nInseriscilo nella cartella config e riprova!");
        }
	}
}

/**
 * Estende la classe Thread. Definisce le azioni che deve effettuare ogni singolo Thread creato dal MainServer.
 */
class ServerThread extends Thread {
    private static int counter = 0;
    private int id = ++counter;
    private Socket socket;
    private Database dbConnection;

    private JSONReader reader;
    private JSONWriter writer;

    /**
     * Costruttore ServerThread.
     * @param s Socket con il collegamento al Client.
     */
    public ServerThread(Socket s, Document document) {
        socket = s;
        String IP = document.getElementsByTagName("Database").item(0).getAttributes().item(0).getTextContent();
        String Port = document.getElementsByTagName("Database").item(0).getAttributes().item(1).getTextContent();
        String DBName = document.getElementsByTagName("Database").item(0).getChildNodes().item(0).getTextContent();
        String DBUser = document.getElementsByTagName("Database").item(0).getChildNodes().item(1).getTextContent();
        String DBPswd = document.getElementsByTagName("Database").item(0).getChildNodes().item(2).getTextContent();
        dbConnection = new Database(IP, Port, DBName, DBUser, DBPswd);
        start();
        System.out.println("ServerThread " + id + ": started");
    }

    /**
     * Override del metodo run della classe Thread. Codice da eseguire all'avvio del Thread.
     * Fara' da intermediario con il Client, ed effettua le richieste ricevute dal Client.
     */
    @Override
    public void run() {
        try {
            reader = new JSONReader(socket.getInputStream());
            writer = new JSONWriter(socket.getOutputStream());

            Boolean isConnected = true;

            while(isConnected) {
                try {
                    JsonObject message = reader.readJson();
                    if(message != null) {
                        if (message.get("clientRequest").getAsString().equals("loginInformation")) {
                            String username = message.get("username").getAsString();
                            String password = message.get("password").getAsString();

                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: loginInformation\nUsername: " + username);

                            JsonObject result = Core.checkCredentials(username, password, dbConnection);

                            writer.write(result);
                        }
                        else if(message.get("clientRequest").getAsString().equals("registrationCredentials")) {
                            String username = message.get("username").getAsString();
                            String password = message.get("password").getAsString();
                            String email = message.get("email").getAsString();
                            String name = message.get("name").getAsString();
                            String surname = message.get("surname").getAsString();
                            String address = message.get("address").getAsString();
                            String city = message.get("city").getAsString();
                            String cap = message.get("cap").getAsString();
                            String telephone = message.get("telephone").getAsString();
                            String identitycode = message.get("identitycode").getAsString();
                            String borndate = message.get("borndate").getAsString();

                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: registrationCredentials\nUsername: " + username);

                            writer.write(Core.registerUser(username, password, email, name, surname, address, city, cap, telephone, identitycode, borndate, dbConnection));
                        }
                        else if(message.get("clientRequest").getAsString().equals("loadCatalogue")) {
                            //If I receive a request for the catalogue, I've to get it from the database
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: loadCatalogue");

                            JsonObject catalogue = Core.LoadCatalogue(dbConnection);

                            writer.write(catalogue);
                        }
                        else if(message.get("clientRequest").getAsString().equals("loadReviews")) {
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: loadReviews");

                            int productID = message.get("productID").getAsInt();

                            JsonObject reviews = Core.LoadReviews(dbConnection, productID);

                            writer.write(reviews);
                        }
                        else if(message.get("clientRequest").getAsString().equals("addReview")) {
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: addReview");

                            String title = message.get("title").getAsString();
                            String text = message.get("text").getAsString();
                            int vote = message.get("vote").getAsInt();
                            int purchaseID = message.get("purchaseID").getAsInt();

                            JsonObject result = Core.AddReview(title, text, vote, purchaseID, dbConnection);

                            writer.write(result);
                        }
                        else if(message.get("clientRequest").getAsString().equals("removeReview")) {
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: removeReview");
                            int purchaseID = message.get("purchaseID").getAsInt();

                            JsonObject result = Core.RemoveReview(purchaseID, dbConnection);

                            writer.write(result);
                        }
                        else if(message.get("clientRequest").getAsString().equals("loadCouriers")) {
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: loadCouriers");

                            JsonObject couriers = Core.LoadCouriers(dbConnection);

                            writer.write(couriers);
                        }
                        else if(message.get("clientRequest").getAsString().equals("confirmCheckout")) {
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: confirmCheckout");

                            Gson gson = new Gson();

                            Cart cart = gson.fromJson(message.get("cartInfo"), new TypeToken<Cart>() {
                            }.getType());
                            Courier courier = gson.fromJson(message.get("courierInfo"), new TypeToken<Courier>() {
                            }.getType());
                            User user = gson.fromJson(message.get("userInfo"), new TypeToken<User>() {
                            }.getType());

                            JsonObject result = Core.ConfirmCheckout(cart, courier, user, dbConnection);

                            writer.write(result);
                        }
                        else if(message.get("clientRequest").getAsString().equals("loadOrders")) {
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: loadOrders");

                            int userID = message.get("userID").getAsInt();

                            JsonObject result = Core.LoadOrders(userID, dbConnection);

                            writer.write(result);
                        }
                        else if(message.get("clientRequest").getAsString().equals("checkNumberOfOrders")) {
                            System.out.println("Client ID: " + id);
                            System.out.println("Requested: checkNumberOfOrders");

                            int userID = message.get("userID").getAsInt();

                            JsonObject result = Core.CheckNumberOfOrders(userID, dbConnection);

                            writer.write(result);
                        }
                        else if(message.get("clientRequest").getAsString().equals("closeConnection")) {
                            isConnected = false;
                        }
                    }
                } catch (Exception Exc) {
                    System.out.println("Errore Server-ExecuteQuery: " + Exc.getMessage());
                    Exc.printStackTrace();
                }
            }
        } catch (Exception e) {
        } finally {
            //Sia nel caso di uscita regolare dal ciclo di ascolto del client, sia nel caso di terminazione anormale, si chiude la socket
            try {
                reader.close();
                writer.close();
                socket.close();
                System.out.println("Un utente si e' disconnesso.");
            } catch (IOException IOExc) {
                System.out.println("Errore: " + IOExc.getMessage());
                IOExc.printStackTrace();
            }
        }
    }
}
