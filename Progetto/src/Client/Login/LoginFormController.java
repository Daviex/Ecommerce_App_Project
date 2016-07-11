package Client.Login;

import Client.MainClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import Core.User.User;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import Utils.*;
import Utils.StreamsJSON.*;

import com.google.gson.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;

/**
 * Classe che implementa Initiaizable. Rappresenta la finestra di Login.
 */
public class LoginFormController implements Initializable {
    @FXML
    TextField txt_username;
    @FXML
    PasswordField txt_password;
    @FXML
    Button btn_login;
    @FXML
    Hyperlink hpl_registration;

    Document document;

    Utils Util;

    /**
     * Evento sul bottone di login. Invia le informazioni dell'utente al server, che effettuera' i dovuti controlli
     * e restituira' il risultato del controllo sul database. In caso positivo, potra' accedere.
     * @param event Rappresenta lo stato dell'evento selezionato.
     */
    @FXML
    private void sendLoginCredentials(ActionEvent event) {
        String username = txt_username.getText();
        String password = txt_password.getText();

        if (username.equals("") || password.equals(""))
            Util.MessageBox("Hai lasciato uno dei due campi liberi.");
        else {
            try {
                if (MainClient.clientSocket == null) {
                    String ipAddress = document.getElementsByTagName("SocketData").item(0).getAttributes().item(0).getTextContent();
                    String portSocket = document.getElementsByTagName("SocketData").item(0).getAttributes().item(1).getTextContent();
                    MainClient.clientSocket = new Socket(InetAddress.getByName(ipAddress), Integer.parseInt(portSocket));
                }

                MainClient.reader = new JSONReader(MainClient.clientSocket.getInputStream());
                MainClient.writer = new JSONWriter(MainClient.clientSocket.getOutputStream());

                JsonObject obj = new JsonObject();

                obj.add("clientRequest", new JsonPrimitive("loginInformation"));
                obj.add("username", new JsonPrimitive(username));
                obj.add("password", new JsonPrimitive(password));

                MainClient.writer.write(obj);

                JsonObject json = MainClient.reader.readJson();

                if (json.get("result").getAsBoolean()) {
                    Gson gson = new Gson();
                    MainClient.user = gson.fromJson(json.get("userInfo"), new TypeToken<User>() {
                    }.getType());
                    //Util.MessageBox("Login effettuato con successo");
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/Client/FXML Forms/MainForm.fxml"));
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Il Covo");
                        stage.setResizable(false);
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            public void handle(WindowEvent we) {
                                MainClient.close();
                                System.exit(0);
                            }
                        });
                        stage.show();
                        stage.sizeToScene();
                        stage.centerOnScreen();

                        Stage oldStage = (Stage) btn_login.getScene().getWindow();
                        oldStage.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    Util.MessageBox("Login fallito. Controlla i dati e riprova.");
            } catch (Exception exc) {
                System.out.println("Errore: " + exc.getMessage());
                exc.printStackTrace();
            }
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
     * Evento su Label. Se viene richiamato, apre una nuova finestra, in cui e' possibile registrarsi.
     */
    @FXML
    private void ShowRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/RegistrationForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Il Covo - Registrazione");
            stage.setResizable(false);
            stage.show();
            stage.sizeToScene();
            stage.centerOnScreen();
            RegistrationFormController controller = loader.<RegistrationFormController>getController();
            controller.setXML(document);
        } catch(IOException io) {
            System.out.println("Errore-ShowRegistration: " + io.getMessage());
            io.printStackTrace();
        }
    }

    /**
     * Metodo derivato da Initializable.
     * @param url Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param rb Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Util = new Utils();
    }
}
