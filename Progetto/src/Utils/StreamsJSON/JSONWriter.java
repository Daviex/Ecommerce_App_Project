package Utils.StreamsJSON;

import java.io.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Estende da OutputStreamWriter, permette di scrivere un oggetto JSON sul flusso dati della socket.
 */
public class JSONWriter extends OutputStreamWriter {

    private Gson gson;

    /**
     * Costruttore JSONWriter.
     * @param out Flusso dati in uscita.
     */
    public JSONWriter(OutputStream out) {
        super(out);
        gson = new Gson();
    }

    /**
     * Converte un oggetto JsonObject in stringa, e la scrive sul flusso dati.
     * @param jObj Contiene le informazioni da scrivere sul flusso.
     */
    public void write(JsonObject jObj) {
        String s = gson.toJson(jObj);

        try {
            super.write(s + "\n");
            super.flush();
        } catch (IOException IOExc) {
            System.out.println("Errore JSONWriter: " + IOExc.getMessage());
            IOExc.printStackTrace();
        }
    }
}
