package Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.commons.validator.routines.EmailValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Classe con diverse funzioni utilizzate nel progetto.
 */
public class Utils {
    /**
     * Costruttore Utils
     */
    public Utils() { }

    /**
     * Metodo per creare una MessageBox.
     * @param text Messaggio da visualizzare.
     */
    public void MessageBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/MessageBoxOK.fxml"));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            MessageBoxOKController controller = loader.<MessageBoxOKController>getController();
            double newHeight = controller.setMessage(text);
            stage.setMinHeight(newHeight);
            stage.setTitle("Il Covo - Messaggio");
            stage.setResizable(false);
            stage.show();
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch(Exception e) {
            System.out.println("Errore-MessageBox: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Metodo per creare una MessageBox.
     * @param text Messaggio da visualizzare.
     * @param oldStage Finestra precedente da chiudere.
     */
    public void MessageBox(String text, Stage oldStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/FXML Forms/MessageBoxOK.fxml"));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            MessageBoxOKController controller = loader.<MessageBoxOKController>getController();
            controller.setOldStage(oldStage);
            double newHeight = controller.setMessage(text);
            stage.setMinHeight(newHeight);
            stage.setTitle("Il Covo - Messaggio");
            stage.setResizable(false);
            stage.show();
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch(Exception e) {
            System.out.println("Errore-MessageBox: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Controlla se la stringa contiene certi caratteri.
     * @param text Messaggio da controllare
     * @return True se la stringa e' corretta, False se errata.
     */
    public boolean CheckString(String text) {
        if(Pattern.compile("[a-zA-Z0-9_]*").matcher(text).matches())
            return true;
        else
            return false;
    }

    /**
     * Controlla se l'email e' corretta.
     * @param email Email da controllare.
     * @return True se e' valida, False altrimenti.
     */
    public boolean ValidateEmail(String email) {
        EmailValidator val = EmailValidator.getInstance();
        if(val.isValid(email))
            return true;
        else
            return false;
    }

    /**
     * Controlla se e' una stringa.
     * @param number Numero da controllare.
     * @return True se e' numero, False altrimenti.
     */
    public boolean CheckInteger(String number) {
        try {
            Integer.parseInt(number);
        } catch ( Exception exc ) {
            return false;
        }
        return true;
    }

    /**
     * Controlla i caratteri all'interno di un indirizzo.
     * @param address Indirizzo da controllare.
     * @return True se e' valido, False altrimenti.
     */
    public boolean CheckAddressString(String address) {
        if(Pattern.compile("[^@_*+\\-\\*]*").matcher(address).matches())
            return true;
        else
            return false;
    }

    /**
     * Controlla se i caratteri della citta' sono validi.
     * @param city Citta' da controllare.
     * @return True se e' valida, False altrimenti.
     */
    public boolean CheckCity(String city) {
        if(Pattern.compile("[\\sa-zA-Z0-9]*").matcher(city).matches())
            return true;
        else
            return false;
    }

    /**
     * Controlla la data di nascita.
     * @param day Giorno
     * @param month Mese
     * @param year Anno
     * @return Vero se e' una data valida, False altrimenti.
     */
    public boolean CheckBornDate(String day, String month, String year) {
        try {
            DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
            date.setLenient(false);
            date.parse(day+"-"+month+"-"+year);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * Controlla il numero di telefono
     * @param telephone Il numero da controllare.
     * @return True se e' valido. False altrimenti.
     */
    public boolean CheckTelephoneNumber(String telephone) {
        if(Pattern.compile("[0-9]*").matcher(telephone).matches())
            return true;
        else
            return false;
    }

    /**
     * Controlla il codice fiscale, che non abbia caratteri vietati e che sia di lunghezza 16 caratteri.
     * @param cf Il codice fiscale da controllare.
     * @return True se e' valido. False altrimenti.
     */
    public boolean CheckIdentityCode(String cf) {
        if(Pattern.compile("[a-zA-Z0-9]*").matcher(cf).matches() && cf.length() == 16)
            return true;
        else
            return false;
    }

    /**
     * Converte la data da quella di MySQL, con layout YYYY-MM-GG, al layout GG-MM-YYYY.
     * @param date Data con layout errato.
     * @return Data con layout corretto.
     */
    public static String fromMySQLDate(String date) {
        String day = date.split("-")[2];
        String month = date.split("-")[1];
        String year = date.split("-")[0];

        String newDate = day + "-" + month + "-" + year;
        return newDate;
    }

    /**
     * Converte la data da quella con layout GG-MM-YYYY a quella con layout YYYY-MM-GG per MySQL.
     * @param date Data con layout errato.
     * @return Data con layout corretto.
     */
    public static String toMySQLDate(String date) {
        String day = date.split("-")[0];
        String month = date.split("-")[1];
        String year = date.split("-")[2];

        String newDate = year + "-" + month + "-" + day;
        return newDate;
    }

    /**
     * Inizializza un nuovo documento XML
     * @param rootText Testo all'intenro del nodo di Root
     * @return Il documento XML su cui inserire gli elementi
     */
    public static Document InitializeXML(String rootText) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();

            Element root = document.createElement(rootText);
            document.appendChild(root);
        } catch(Exception exc) {
            System.out.println("Errore InitializeDocument: " + exc.getMessage());
            exc.printStackTrace();
        }

        return document;
    }

    /**
     * Legge il file XML.
     * @param path Path al file da leggere.
     * @return Il documento caricato.
     */
    public static Document ReadXML(String path) {
        Document document = null;
        try {
            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(file);
            document.getDocumentElement().normalize();
        } catch(Exception exc) {
            System.out.println("Errore ReadXML: " + exc.getMessage());
            exc.printStackTrace();
        }

        return document;
    }

    /**
     * Inserisce un elemento figlio dentro il padre.
     * @param parent Elemento padre.
     * @param children Elemento figlio.
     */
    public static void AddElement(Element parent, Element children) {
        parent.appendChild(children);
    }

    /**
     * Crea un file XML.
     * @param document Informazioni XML da scrivere.
     * @param fileName Nome del file.
     */
    public static void CreateXML(Document document, String fileName) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(fileName+".xml"));
            transformer.transform(source, result);
        } catch(Exception exc) {
            System.out.println("Errore CreateDocument: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    /**
     * Crea la cartella se non esiste.
     * @param folderName Nome della cartella.
     */
    public static void CreateFolder(String folderName) {
        File dir = new File(folderName);
        if(!dir.exists())
            dir.mkdir();
    }

    /**
     * Converte la data dal tipo Date a Stringa
     * @param date Data da convertire
     * @return Data convertita
     */
    public static String convertDate(Date date) {
        String result = null;
        if (date == null) {
            result = "N/D";
        } else {
            try {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                result = fromMySQLDate(sdf.format(date));
            } catch (Exception exc) {
                System.out.println("Initializing OrderInfoPage-GoalDate: " + exc.getMessage());
                exc.printStackTrace();
            }
        }
        return result;
    }
}
