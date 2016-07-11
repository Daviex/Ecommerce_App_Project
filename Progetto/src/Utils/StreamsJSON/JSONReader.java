package Utils.StreamsJSON;

import java.io.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Estende da Buffered Reader, viene utilizzato per leggere dal flusso dati i dati JSON, e convertiti in JsonObject.
 */
public class JSONReader extends BufferedReader {

    private Gson gson;

    /**
     * Cosruttore JSONReader.
     * @param in Flusso dati in ingresso.
     */
    public JSONReader(InputStream in) {
        super(new InputStreamReader(in));
        gson = new Gson();
    }


    /**
     * Legge il flusso dati come stringa, e lo converte in JSON.
     * @return Un oggetto con le informazioni ricevute.
     */
    public JsonObject readJson() {
        JsonObject jObj = null;

        try {
            String json = super.readLine();
            jObj = gson.fromJson(json, JsonObject.class);
        } catch (IOException IOExc) {
            System.out.println("Errore JSONReader: " + IOExc.getMessage());
            IOExc.printStackTrace();
            jObj = new JsonObject();
            jObj.add("clientRequest", new JsonPrimitive("closeConnection"));
        }

        return jObj;
    }
}
