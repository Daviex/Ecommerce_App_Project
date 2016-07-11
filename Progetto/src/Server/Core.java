package Server;

import Core.Cart.Cart;
import Core.Cart.ProductInCart;
import Core.Catalogue.Catalogue;
import Core.Catalogue.Product;
import Core.Courier.Courier;
import Core.Catalogue.Category;
import Core.Catalogue.Section;
import Core.Courier.Shipment;
import Core.Order.Order;
import Core.Order.ProductInOrder;
import Core.Review.Review;
import Core.User.User;
import com.google.gson.*;

import Server.Database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.ArrayList;

/**
 * Classe principale. Contiene le varie funzioni che comunicano con il database.
 */
public class Core {
    /**
     * Controlla le credenziali inserite dall'utente nel form di login.
     * @param username Username
     * @param password Password
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Un oggetto contenente il risultato della richiesta, e le informazioni dell'utente.
     */
    public static JsonObject checkCredentials(String username, String password, Database dbConnection) {
        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        try {
            dbConnection.Initialize();
            ResultSet results = dbConnection.SelectionQuery(String.format("SELECT * FROM utenti WHERE username='%s'", username));

            if (results != null) {
                if (results.next()) {
                    if (username.equalsIgnoreCase(results.getString("username")) && password.equals(results.getString("password"))) {
                        returnMessage = new JsonObject();
                        //Login effettuato con successo! Ritorno un esito positivo!
                        returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));

                        int id = results.getInt("id");
                        String Username = results.getString("Username");
                        String Email = results.getString("Email");
                        String Nome = results.getString("Nome");
                        String Cognome = results.getString("Cognome");
                        String Indirizzo = results.getString("Indirizzo");
                        String Citta = results.getString("Citta");
                        int CAP = results.getInt("CAP");
                        String Num_Tel = results.getString("Num_Tel");
                        String Data_Nasc = results.getString("Data_Nasc");
                        String Cod_Fisc = results.getString("Cod_Fisc");

                        User myUser = new User(id, Username, Email, Nome, Cognome, Indirizzo, Citta, CAP, Num_Tel, Cod_Fisc, Data_Nasc);

                        Gson gson = new Gson();
                        returnMessage.add("userInfo", new JsonParser().parse(gson.toJson(myUser)));
                    }
                }
            }
        } catch (SQLException Exc) {
            System.out.println("Errore SQLException-User: " + Exc.getMessage());
            Exc.printStackTrace();
        } finally {
            dbConnection.Close();
            return returnMessage;
        }
    }

    /**
     * Effettua la registrazione di un nuovo utente.
     * @param username Username
     * @param password Password
     * @param email Email
     * @param name Nome
     * @param surname Cognome
     * @param address Indirizzo
     * @param city Citta'
     * @param cap CAP
     * @param telephone Numero di Telefono
     * @param identitycode Codice Fiscale
     * @param borndate Data di Nascita
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Un oggetto contenente il risultato della richiesta.
     */
    public static JsonObject registerUser(String username, String password, String email, String name, String surname,
                                   String address, String city, String cap, String telephone, String identitycode,
                                   String borndate, Database dbConnection) {
        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        dbConnection.Initialize();
        if(!dbConnection.CheckIfExistQuery("SELECT * FROM utenti WHERE Username = '" + username + "' OR Email = '" + email + "' LIMIT 1")) {
            try {
                dbConnection.Close();
                String insertQuery = String.format("INSERT INTO utenti (Nome, Cognome, Indirizzo, Citta, CAP, Num_Tel, Data_Nasc, Cod_Fisc, Username, Password, Email, Rank) VALUES "
                                + "(\"%s\", \"%s\", \"%s\", \"%s\", \"%d\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", 1)",
                        name, surname, address, city, Integer.parseInt(cap), telephone, borndate, identitycode, username, password, email);

                dbConnection.InitializeStatement();
                boolean result = dbConnection.InsertQuery(insertQuery);

                if (result) {
                    returnMessage = new JsonObject();
                    returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));
                }
                dbConnection.CloseStatement();
            } catch (Exception exc) {
                System.out.println("Errore InsertQuery: " + exc.getMessage());
                exc.printStackTrace();
            }
        }
        return returnMessage;
    }

    /**
     * Carica il catalogo prodotti del sito di ECommerce.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Oggetto contenente il risultato, ed il catalogo da visualizzare.
     */
    public static JsonObject LoadCatalogue(Database dbConnection) {
        Catalogue catalogue = new Catalogue();

        catalogue.setCatalogue(new ArrayList<Section>());

        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        dbConnection.Initialize();
        ResultSet results = dbConnection.SelectionQuery("SELECT * FROM reparti");

        if (results != null) {
            try {
                while (results.next()) {
                    int secID = results.getInt("id");
                    String secName = results.getString("nome");
                    String secDesc = results.getString("desc");
                    catalogue.addSection(new Section(secID, secName, secDesc));
                }
                dbConnection.Close();
            } catch (SQLException SQLExc1) {
                System.out.println("Errore SQLException-Core-LoadCatalogue-Section: " + SQLExc1.getMessage());
                SQLExc1.printStackTrace();
            }

            if (catalogue.hasSections()) {
                returnMessage = new JsonObject();
                returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));

                for (Section sec : catalogue.getCatalogue()) {
                    dbConnection.Initialize();
                    results = dbConnection.SelectionQuery(String.format("SELECT * FROM categorie WHERE IDRep = %d", sec.getSectionID()));

                    if (results != null) {
                        try {
                            while (results.next()) {
                                int catID = results.getInt("id");
                                String catName = results.getString("nome");
                                String catDesc = results.getString("desc");

                                sec.setCategory(catID, catName, catDesc);
                            }
                            dbConnection.Close();
                        } catch (SQLException SQLExc2) {
                            System.out.println("Errore SQLException-Core-LoadCatalogue-Category: " + SQLExc2.getMessage());
                            SQLExc2.printStackTrace();
                        }

                        if (sec.hasCategories()) {
                            for (Category cat : sec.categories) {
                                dbConnection.Initialize();
                                results = dbConnection.SelectionQuery(String.format("SELECT * FROM prodotti WHERE IDCat = %d", cat.getCategoryID()));

                                if (results != null) {
                                    try {
                                        while (results.next()) {
                                            int prodID = results.getInt("id");
                                            String prodName = results.getString("nome");
                                            String prodDesc = results.getString("desc");
                                            double prodPrice = results.getDouble("prezzo");
                                            int prodQuantity = results.getInt("quantita");
                                            String prodImage = results.getString("IMG");

                                            double prodDiscount = loadDiscount(dbConnection, prodID);

                                            cat.setProduct(prodID, prodName, prodDesc, prodPrice, prodDiscount, prodQuantity, prodImage, cat.getCategoryName());
                                        }

                                        dbConnection.Close();
                                    } catch (SQLException SQLExc2) {
                                        System.out.println("Errore SQLException-Core-LoadCatalogue-Category: " + SQLExc2.getMessage());
                                        SQLExc2.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }

                if (returnMessage.get("result").getAsBoolean()) {
                    Gson gson = new Gson();
                    returnMessage.add("catalogueInfo", new JsonParser().parse(gson.toJson(catalogue)));
                }
            }
        }

        return returnMessage;
    }

    /**
     * Controlla lo sconto sul prodotto richiesto.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @param productID ID del prodotto su cui controllare se esiste uno sconto.
     * @return Ritorna la percentuale di sconto del prodotto.
     */
    public static double loadDiscount(Database dbConnection, int productID) {
        double percentage = 0.0;
        try {
            dbConnection.Initialize();
            ResultSet result = dbConnection.SelectionQuery(String.format("SELECT * FROM sconti WHERE IDProd = %d", productID));

            if(result != null) {
                if (result.next()) {
                    percentage = result.getDouble("Percentuale");
                }
            }

            dbConnection.Close();
        } catch (SQLException exc) {
            System.out.println("Errore SQLException-Core-LoadCatalogue-Discount: " + exc.getMessage());
            exc.printStackTrace();
        } finally {
            return percentage;
        }
    }

    /**
     * Carica le recensioni di un dato prodotto.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @param productID ID del prodotto di cui si vogliono le recensioni
     * @return Recensioni trovate su di quel prodotto.
     */
    public static JsonObject LoadReviews(Database dbConnection, int productID) {
        ArrayList<Review> reviews = new ArrayList();

        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        String query = "SELECT CONCAT(utenti.Nome, ' ', utenti.Cognome) AS Nominativo, recensioni.ID, recensioni.Titolo, recensioni.Testo, recensioni.Data, recensioni.Voto, utenti.ID AS userID " +
                "FROM ((recensioni JOIN acquisti ON recensioni.IDAcq = acquisti.ID) JOIN ordini ON acquisti.IDOrdine = ordini.ID ) JOIN utenti ON ordini.IDUser = utenti.ID " +
                "WHERE acquisti.IDProd = '" + productID + "' ORDER BY recensioni.Data DESC";

        dbConnection.Initialize();
        ResultSet results = dbConnection.SelectionQuery(query);

        if(results != null) {
            try {
                while (results.next()) {
                    String name = results.getString("Nominativo");
                    int ID = results.getInt("ID");
                    String title = results.getString("Titolo");
                    String text = results.getString("Testo");
                    String date = results.getString("Data");
                    int vote = results.getInt("Voto");
                    int userID = results.getInt("userID");

                    Review review = new Review(ID, title, text, date, vote, userID, name);
                    reviews.add(review);
                }

                dbConnection.Close();
            } catch ( Exception Exc ) {
                System.out.println("Errore-LoadReviews-While: " + Exc.getMessage());
                Exc.printStackTrace();
            }

            if(reviews.size() > 0) {
                returnMessage = new JsonObject();
                returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));

                Gson gson = new Gson();
                returnMessage.add("reviewsInfo", new JsonParser().parse(gson.toJson(reviews)));
            }
        }

        return  returnMessage;
    }

    /**
     * Richiesta per aggiungere una nuova recensione.
     * @param title Titolo della recensione
     * @param text Testo della recensione.
     * @param vote Voto della recenzione.
     * @param purchaseID ID dell'acquisto.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return
     */
    public static JsonObject AddReview(String title, String text, int vote, int purchaseID, Database dbConnection) {
        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        Date date = new Date();
        String dateNow = "N/D";
        try {
            dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch(Exception exc) {
            System.out.println("Errore AddReview: " + exc.getMessage());
            exc.printStackTrace();
        }

        String addReview = "INSERT INTO recensioni (Titolo, Testo, Data, Voto, IDAcq) VALUES " +
                "('" + title + "', '" + text + "', '" + dateNow + "', '" + vote + "', '" + purchaseID + "')";
        dbConnection.InitializeStatement();
        boolean result = dbConnection.InsertQuery(addReview);

        if(result) {
            returnMessage = new JsonObject();
            returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));
        }

        dbConnection.CloseStatement();

        return returnMessage;
    }

    /**
     * Richiesta per rimuovere una recensione dal database.
     * @param purchaseID ID dell'acquisto.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Risultato sulla rimozione, se e' avvenuta o meno.
     */
    public static JsonObject RemoveReview(int purchaseID, Database dbConnection) {
        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        String removeReview = "DELETE FROM recensioni WHERE IDAcq = '" + purchaseID + "'";
        dbConnection.InitializeStatement();
        boolean result = dbConnection.RemoveQuery(removeReview);

        if(result) {
            returnMessage = new JsonObject();
            returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));
        }

        dbConnection.CloseStatement();

        return returnMessage;
    }

    /**
     * Richiesta per caricare i corrieri disponibili per la spedizione degli acquisti.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Il risultato della richiesta, ed i corrieri trovati.
     */
    public static JsonObject LoadCouriers(Database dbConnection) {
        ArrayList<Courier> couriers = new ArrayList<Courier>();

        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        String query = "SELECT * FROM corrieri";

        dbConnection.Initialize();
        ResultSet results = dbConnection.SelectionQuery(query);

        if(results != null) {
            try {
                while (results.next()) {
                    String name = results.getString("Nominativo");
                    int ID = results.getInt("ID");
                    double cost = results.getDouble("Costo");
                    String expressTime = results.getString("Tempi_Consegna");

                    Courier courier = new Courier(ID, name, cost, expressTime);
                    couriers.add(courier);
                }

                dbConnection.Close();
            } catch ( Exception Exc ) {
                System.out.println("Errore-LoadCouriers-While: " + Exc.getMessage());
                Exc.printStackTrace();
            }

            if(couriers.size() > 0) {
                returnMessage = new JsonObject();
                returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));

                Gson gson = new Gson();
                returnMessage.add("couriersInfo", new JsonParser().parse(gson.toJson(couriers)));
            }
        }

        return returnMessage;
    }

    /**
     * Effettua il checkout del carrello.
     * @param cart I prodotti nel carrello.
     * @param courier Corriere scelto per la spedizione.
     * @param user L'utente che ha effettuato l'acquisto.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Ritorna il risultato della richiesta.
     */
    public static JsonObject ConfirmCheckout(Cart cart, Courier courier, User user, Database dbConnection) {
        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        try {
            dbConnection.Initialize();
            dbConnection.connection.setAutoCommit(false);

            String addExpress = "INSERT INTO spedizioni (Luogo_Part, Citta_Arr, Indirizzo_Arr, CAP_Arr, IDCorr) VALUES " +
                    "('Messina', ' " + user.getCity() + "', '" + user.getAddress() + "', '" + user.getCap() + "', '" + courier.getCourierID() + "')";
            boolean result = dbConnection.InsertQuery(addExpress);
            long expressID = dbConnection.GetLastInsertId();

            if (result) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String nowDate = sdf.format(date);

                String addOrder = "INSERT INTO ordini (Data_Acq, IDUser) VALUES ('" + nowDate + "','" + user.getId() + "')";
                result = dbConnection.InsertQuery(addOrder);

                long orderID = dbConnection.GetLastInsertId();

                if (result) {
                    for (ProductInCart product : cart.productInCarts) {
                        double totale = Double.parseDouble(String.format("%.2f", product.product.getProductDiscount() * (double) product.quantity).replace(',', '.'));
                        String addPurchase = "INSERT INTO acquisti (Costo, Quantita, IDProd, IDSped, IDStato, IDOrdine) VALUES " +
                                "('" + totale + "', '" + product.quantity + "', '" + product.product.getProductID() + "', '" + expressID + "','2', '" + orderID + "')";

                        result = dbConnection.InsertQuery(addPurchase);

                        if (!result)
                            dbConnection.Rollback();
                        else {
                            String updateQuantityProduct = "UPDATE prodotti SET Quantita = Quantita - " + product.quantity + " WHERE ID = '" + product.product.getProductID() + "'";
                            result = dbConnection.InsertQuery(updateQuantityProduct);
                        }
                    }

                    if (result) {
                        returnMessage = new JsonObject();
                        returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));
                        dbConnection.Commit();
                        dbConnection.Close();
                    }
                }
            }

            dbConnection.connection.setAutoCommit(true);
        } catch (Exception exc) {
            System.out.println("Errore ConfirmCheckout: " + exc.getMessage());
            exc.printStackTrace();
            dbConnection.Rollback();
        }

        return returnMessage;
    }

    /**
     * Controlla il numero di ordini di un dato utente.
     * @param userID ID Utente.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Risultato della richiesta ed il numero di ordini-
     */
    public static JsonObject CheckNumberOfOrders(int userID, Database dbConnection) {
        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(0));

        try {
            String countOrders = "SELECT COUNT(*) AS NumOrders FROM ordini WHERE IDUser = '" + userID + "'";
            dbConnection.Initialize();
            ResultSet results = dbConnection.SelectionQuery(countOrders);

            if(results != null) {
                if(results.next()) {
                    int NumOrders = results.getInt("NumOrders");

                    if(NumOrders > 0) {
                        returnMessage = new JsonObject();
                        returnMessage.add("result", new JsonPrimitive(NumOrders));
                    }
                }
            }
            dbConnection.Close();
        } catch(Exception exc) {
            System.out.println("Errore CheckNumberOfOrders: " + exc.getMessage());
            exc.printStackTrace();
        }

        return returnMessage;
    }

    /**
     * Carica gli ordini di un dato utente.
     * @param userID ID dell'utente.
     * @param dbConnection Riferimento all'oggetto del database creato dal server.
     * @return Risultato della richiesta, con tutti gli ordini trovati.
     */
    public static JsonObject LoadOrders(int userID, Database dbConnection) {
        JsonObject returnMessage = new JsonObject();
        returnMessage.add("result", new JsonPrimitive(Boolean.FALSE));

        ArrayList<Order> orders = new ArrayList<Order>();

        try {
            String selectOrders = "SELECT ordini.ID AS orderID, ordini.Data_Acq AS orderDate, " +
                    "acquisti.ID as purchaseID, acquisti.costo AS purchaseCost, acquisti.quantita AS purchaseQuantity, " +
                    "stati.nome AS statusName, spedizioni.ID AS shipID, spedizioni.Data_Part AS shipStartDate, " +
                    "spedizioni.Data_Arr AS shipGoalDate, spedizioni.Luogo_Part AS shipStartLoc, " +
                    "spedizioni.Citta_Arr AS shipGoalLoc, spedizioni.Indirizzo_Arr AS shipGoalAddress, " +
                    "spedizioni.CAP_Arr AS shipGoalCap, corrieri.ID AS courierID, corrieri.Nominativo AS courierName, " +
                    "corrieri.costo AS courierCost, corrieri.Tempi_Consegna AS courierExpressTime, " +
                    "prodotti.ID AS productID, prodotti.Nome AS productName, prodotti.Desc AS productDesc, " +
                    "prodotti.Prezzo AS productPrice, prodotti.Quantita AS productQuantity, prodotti.IMG AS productImage, " +
                    "sconti.Percentuale AS discountProduct, categorie.Nome AS categoryName " +
                    "FROM ((((((ordini JOIN acquisti ON ordini.ID = acquisti.IDOrdine) " +
                    "JOIN stati ON acquisti.IDStato = stati.ID) " +
                    "JOIN spedizioni ON acquisti.IDSped = spedizioni.ID) " +
                    "JOIN corrieri ON spedizioni.IDCorr = corrieri.ID) " +
                    "JOIN prodotti ON acquisti.IDProd = prodotti.ID) " +
                    "JOIN categorie ON prodotti.IDCat = categorie.ID) " +
                    "LEFT JOIN sconti ON prodotti.ID = sconti.IDProd " +
                    "WHERE ordini.IDUser = '" + userID + "' " +
                    "ORDER BY ordini.Data_Acq DESC";

            dbConnection.Initialize();
            ResultSet results = dbConnection.SelectionQuery(selectOrders);

            int oldOrderId = -1;

            Order order = null;

            if(results != null) {
                while (results.next()) {
                    //Info about the order
                    int orderId = results.getInt("orderID");

                    if (oldOrderId != orderId) {
                        if (oldOrderId != -1)
                            orders.add(order);
                        oldOrderId = orderId;
                        String orderDate = results.getString("orderDate");
                        order = new Order(orderId, orderDate);
                    }
                    String state = results.getString("statusName");

                    //First, the product, the easiest one
                    int productId = results.getInt("productID");
                    String productName = results.getString("productName");
                    String productDesc = results.getString("productDesc");
                    double productPrice = results.getDouble("productPrice");
                    double discountProduct = results.getDouble("discountProduct");
                    int productQuantity = results.getInt("productQuantity");
                    String productImage = results.getString("productImage");
                    String categoryName = results.getString("categoryName");

                    Product product = new Product(productId, productName, productDesc, productPrice, discountProduct, productQuantity, productImage, categoryName);

                    //Now the Courier
                    int courierId = results.getInt("courierID");
                    String courierName = results.getString("courierName");
                    double courierCost = results.getDouble("courierCost");
                    String courierExpressTime = results.getString("courierExpressTime");

                    Courier courier = new Courier(courierId, courierName, courierCost, courierExpressTime);

                    //Now the shipment
                    int shipID = results.getInt("shipID");
                    String shipStartDate = results.getString("shipStartDate");
                    String shipGoalDate = results.getString("shipGoalDate");
                    String shipStartLoc = results.getString("shipStartLoc");
                    String shipGoalLoc = results.getString("shipGoalLoc");
                    String shipGoalAddress = results.getString("shipGoalAddress");
                    int shipGoalCap = results.getInt("shipGoalCap");

                    Shipment shipment = new Shipment(shipID, shipStartDate, shipGoalDate, shipStartLoc, shipGoalLoc, shipGoalAddress, shipGoalCap, courier);

                    //As last, I do the product
                    int purchaseID = results.getInt("purchaseID");
                    double purchaseTotal = results.getDouble("purchaseCost");
                    int purchaseQuantity = results.getInt("purchaseQuantity");

                    ProductInOrder productInOrder = new ProductInOrder(purchaseID, purchaseTotal, purchaseQuantity, product, state, shipment);

                    order.addProduct(productInOrder);
                }

                if(order != null)
                    orders.add(order);

                if(orders.size() > 0) {
                    returnMessage = new JsonObject();
                    returnMessage.add("result", new JsonPrimitive(Boolean.TRUE));
                    returnMessage.add("ordersInfo", new JsonParser().parse(new Gson().toJson(orders)));
                }

                dbConnection.Close();
            }

        } catch(Exception exc) {
            System.out.println("Errore LoadOrders: " + exc.getMessage());
            exc.printStackTrace();
        }

        return returnMessage;
    }
}
