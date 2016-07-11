package Core.User;

import Core.Cart.Cart;
import Core.Order.Order;
import Utils.StreamsJSON.*;
import Utils.*;

import com.google.gson.*;

import Server.*;

import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Classe contenente le informazioni sull'utente.
 */
public class User {
    Utils Util;
    private int id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private String address;
    private String city;
    private int cap;
    private String telephone;
    private String identitycode;
    private String borndate;

    public Cart cart;
    public ArrayList<Order> orders;

    /**
     * Costruttore User.
     * @param id Id.
     * @param username Username.
     * @param email Email.
     * @param name Nome.
     * @param surname Cognome.
     * @param address Indirizzo.
     * @param city Citta'.
     * @param cap CAP.
     * @param telephone Numero di Telefono.
     * @param identitycode Codice Fiscale.
     * @param borndate Data di Nascita.
     */
    public User(int id, String username, String email, String name, String surname,
                String address, String city, int cap, String telephone, String identitycode,
                String borndate) {
        Util = new Utils();

        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.city = city;
        this.cap = cap;
        this.telephone = telephone;
        this.identitycode = identitycode;
        this.borndate = Utils.fromMySQLDate(borndate);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public int getCap() { return cap; }
    public String getTelephone() { return telephone; }
    public String getIdentitycode() { return identitycode; }
    public String getBorndate() { return borndate; }
}
