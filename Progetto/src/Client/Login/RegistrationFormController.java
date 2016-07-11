package Client.Login;

import Client.MainClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import Utils.*;
import Utils.StreamsJSON.*;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;

/**
 * Classe che implementa Initializable. Rappresenta la finestra di registrazione.
 */
public class RegistrationFormController implements Initializable {

    private Utils Util;

    @FXML
    TextField txt_username;
    @FXML
    TextField txt_password;
    @FXML
    TextField txt_email;
    @FXML
    TextField txt_name;
    @FXML
    TextField txt_surname;
    @FXML
    TextField txt_address;
    @FXML
    TextField txt_city;
    @FXML
    TextField txt_telephone;
    @FXML
    TextField txt_cap;
    @FXML
    TextField txt_cf;
    @FXML
    ComboBox cmb_day;
    @FXML
    ComboBox cmb_month;
    @FXML
    ComboBox cmb_year;

    @FXML
    Button btn_reset;
    @FXML
    Button btn_register;

    Document document;

    /**
     * Metodo derivato da Initializable. Inizializza le combobox con i valori delle date.
     * @param url Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param rb Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Util = new Utils();

        Date date = new Date();
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));

        for(int i = 1; i <= 31; i++) {
            cmb_day.getItems().add(String.valueOf(i));
        }

        for(int i = 1; i <= 12; i++) {
            cmb_month.getItems().add(String.valueOf(i));
        }

        for(int i = 1900; i <= currentYear - 15; i++) {
            cmb_year.getItems().add(String.valueOf(i));
        }

        cmb_day.setValue(cmb_day.getItems().get(0));
        cmb_month.setValue(cmb_month.getItems().get(0));
        cmb_year.setValue(cmb_year.getItems().get(0));
    }

    /**
     * Evento sul bottone di reset. Svuota i campi del form.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void ResetFields(ActionEvent action) {
        txt_username.setText("");
        txt_password.setText("");
        txt_name.setText("");
        txt_surname.setText("");
        txt_address.setText("");
        txt_city.setText("");
        txt_telephone.setText("");
        cmb_day.setValue(cmb_day.getItems().get(0));
        cmb_month.setValue(cmb_month.getItems().get(0));
        cmb_year.setValue(cmb_year.getItems().get(0));
        txt_cf.setText("");
        txt_email.setText("");
    }

    /**
     * Evento sul bottone di conferma. Invia una richiesta al server per completare il processo di registrazione.
     * @param action Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void RegisterUser(ActionEvent action) {
        try {
            boolean isCorrect = CheckFields();

            if(isCorrect) {
                if (MainClient.clientSocket == null) {
                    String ipAddress = document.getElementsByTagName("SocketData").item(0).getAttributes().item(0).getTextContent();
                    String portSocket = document.getElementsByTagName("SocketData").item(0).getAttributes().item(1).getTextContent();
                    MainClient.clientSocket = new Socket(InetAddress.getByName(ipAddress), Integer.parseInt(portSocket));
                }

                MainClient.reader = new JSONReader(MainClient.clientSocket.getInputStream());
                MainClient.writer = new JSONWriter(MainClient.clientSocket.getOutputStream());

                JsonObject obj = new JsonObject();

                obj.add("clientRequest", new JsonPrimitive("registrationCredentials"));
                obj.add("username", new JsonPrimitive(txt_username.getText()));
                obj.add("password", new JsonPrimitive(txt_password.getText()));
                obj.add("email", new JsonPrimitive(txt_email.getText()));
                obj.add("name", new JsonPrimitive(txt_name.getText()));
                obj.add("surname", new JsonPrimitive(txt_surname.getText()));
                obj.add("address", new JsonPrimitive(txt_address.getText()));
                obj.add("city", new JsonPrimitive(txt_city.getText()));
                obj.add("cap", new JsonPrimitive(txt_cap.getText()));
                obj.add("telephone", new JsonPrimitive(txt_telephone.getText()));
                obj.add("identitycode", new JsonPrimitive(txt_cf.getText()));
                obj.add("borndate", new JsonPrimitive((String)cmb_year.getValue()+"-"+(String)cmb_month.getValue()+"-"+(String)cmb_day.getValue()));

                MainClient.writer.write(obj);
                Boolean result = MainClient.reader.readJson().get("result").getAsBoolean();

                if(result) {
                    Util.MessageBox("Registrazione effettuata con successo.\nOra puoi effettuare il login.");

                    Stage oldStage = (Stage)btn_register.getScene().getWindow();
                    oldStage.close();
                } else {
                    Util.MessageBox("L'username o l'email inserita esiste gia'.\nRiprova!");
                }
            }
        } catch(Exception exc) {
            System.out.println("Errore-RegisterUser: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Imposta il file di configurazione XML da cui leggere i dati.
     * @param document Dati XML.
     */
    public void setXML(Document document) {
        this.document = document;
    }

    /**
     * Metodo che controlla i campi del form, e vengono verificati.
     * @return True se tutti i campi sono corretti, False se almeno un campo non e' verificato.
     */
    private boolean CheckFields() {
        if (!Util.CheckString(txt_username.getText())) {
            Util.MessageBox("L'username non puo' avere caratteri speciali.\nRiprova!");
            return false;
        }
        if(!Util.CheckString(txt_password.getText())) {
            Util.MessageBox("La password non puo' avere caratteri speciali.\nRiprova!");
            return false;
        }
        if(!Util.ValidateEmail(txt_email.getText())) {
            Util.MessageBox("Email non valida, prego riprovare!");
            return false;
        }
        if(!Util.CheckString(txt_name.getText())) {
            Util.MessageBox("La nome non puo' avere caratteri speciali.\nRiprova!");
            return false;
        }
        if(!Util.CheckString(txt_surname.getText())) {
            Util.MessageBox("La cognome non puo' avere caratteri speciali.\nRiprova!");
            return false;
        }
        if(!Util.CheckAddressString(txt_address.getText())) {
            Util.MessageBox("Il tuo indirizzo non puo' avere caratteri speciali.\nRiprova!");
            return false;
        }
        if(!Util.CheckCity(txt_city.getText())) {
            Util.MessageBox("La tua citta' non puo' avere caratteri speciali. \nRiprova!");
        }
        if(!Util.CheckInteger(txt_cap.getText())) {
            Util.MessageBox("Il CAP non puo' avere caratteri. \nRiprova");
            return false;
        }
        if(!Util.CheckTelephoneNumber(txt_telephone.getText())) {
            Util.MessageBox("Il numero di telefono puo' avere solo numeri. \nRiprova!");
            return false;
        }
        if(!Util.CheckIdentityCode(txt_cf.getText())) {
            Util.MessageBox("Il codice fiscale non puo' avere caratteri speciali. \nRiprova!");
            return false;
        }
        if(!Util.CheckBornDate((String)cmb_day.getValue(), (String)cmb_month.getValue(), (String)cmb_year.getValue())) {
            Util.MessageBox("La data di nascita e' errata. \nRiprova!");
            return false;
        }
        return true;
    }
}
