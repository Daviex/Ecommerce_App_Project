package Server;

import Utils.StreamsJSON.*;

import com.google.gson.*;

import java.sql.*;
import java.io.*;
import java.net.*;

/**
 * Classe per la comunicazione con la base di dati.
 */
public class Database {
    public Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    /**
     * Costruttore Database. Viene inizializzato il driver JDBC al suo interno.
     */
    public Database(String IP, String Port, String dbName, String dbUser, String dbPaswd) {
        try {
            //Carica il driver
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + Port + "/" + dbName, dbUser, dbPaswd);

            System.out.println("Connected to database");
        } catch(Exception e){
            System.out.println("Errore ConnessioneDB " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inizializza lo statement ed il ResultSet.
     */
    public void Initialize() {
        InitializeStatement();
        InitializeResultSet();
    }

    /**
     * Chiude lo statement ed il ResultSet.
     */
    public void Close() {
        CloseResultSet();
        CloseStatement();
    }

    /**
     * Effettua il commit dei dati al database.
     */
    public void Commit() {
        try {
            connection.commit();
        } catch(SQLException sqlExc) {
            System.out.println("Errore CommitDatabase: " + sqlExc.getMessage());
            sqlExc.printStackTrace();
        }
    }

    /**
     * Disabilita le modifiche effettuate al database.
     */
    public void Rollback() {
        try {
            connection.rollback();
        } catch(SQLException sqlExc) {
            System.out.println("Errore RollbackDatabase: " + sqlExc.getMessage());
            sqlExc.printStackTrace();
        }
    }

    /**
     * Inizializza uno statement.
     */
    public void InitializeStatement() {
        try {
            statement = null;
            statement = connection.createStatement();
            statement.setEscapeProcessing(true);
        } catch(SQLException exc) {
            System.out.println("Errore InitializeStatement: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Chiude lo statement.
     */
    public void CloseStatement() {
        try {
            statement.close();
        } catch(SQLException exc) {
            System.out.println("Errore CloseStatement: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Inizializza il ResultSet.
     */
    public void InitializeResultSet() {
        resultSet = null;
    }

    /**
     * Chiude il ResultSet.
     */
    public void CloseResultSet() {
        try {
            resultSet.close();
        } catch(SQLException exc) {
            System.out.println("Errore CloseResultSet: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Effettua una query di selezione nel database.
     * @param query Query di selezione.
     * @return ResultSet con le informazioni.
     */
    public ResultSet SelectionQuery(String query) {
        try {
            resultSet = statement.executeQuery(query);
        } catch(SQLException exc) {
            System.out.println("Errore ExecuteQuery: " + exc.getMessage());
            exc.printStackTrace();
        }
        finally {
            return resultSet;
        }
    }

    /**
     * Controlla se esiste un elemento.
     * @param query Query di selezione.
     * @return Se esiste ritornera' True, altrimenti False.
     */
    public boolean CheckIfExistQuery(String query) {
        boolean result = false;
        try {
            resultSet = statement.executeQuery(query);
            if(resultSet != null) {
                if(resultSet.next()) {
                    result = true;
                }
            }
        } catch(SQLException exc) {
            System.out.println("Errore CheckIfExistQuery: " + exc.getMessage());
            exc.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     * Effettua un inserimento nel database.
     * @param query Query di inserimento.
     * @return Risultato dell'inserimento, se e' 0 vuol dire che è fallito.
     */
    public boolean InsertQuery(String query) {
        boolean result = false;
        try {
            int results;
            results = statement.executeUpdate(query);
            if(results != 0) {
                result = true;
            }
        } catch(SQLException exc) {
            System.out.println("Errore InsertQuery: " + exc.getMessage());
            exc.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     * Effettua una rimozione dal database.
     * @param query Query di rimozione.
     * @return Se la rimozione e' avvenuta correttamente, ritornera' un numero diverso da 0.
     */
    public boolean RemoveQuery(String query) {
        boolean result = false;
        try {
            int results;
            results = statement.executeUpdate(query);

            if(results != 0) {
                result = true;
            }
        } catch(SQLException exc) {
            System.out.println("Errore RemoveQuery: " + exc.getMessage());
            exc.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     * Richiesta dell'ultimo ID che e' stato inserito nel database.
     * @return L'ultimo ID inserito.
     */
    public long GetLastInsertId() {
        long result = -1;
        try {
            resultSet = statement.executeQuery("SELECT last_insert_id() AS last_id");
            if(resultSet.next())
                result = resultSet.getLong("last_id");
        } catch(SQLException exc) {
            System.out.println("Errore LastInsertID: " + exc.getMessage());
            exc.printStackTrace();
        } finally {
            return result;
        }
    }
}
