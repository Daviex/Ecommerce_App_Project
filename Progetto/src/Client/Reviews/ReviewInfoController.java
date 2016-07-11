package Client.Reviews;

import Client.MainClient;
import Core.Review.Review;
import Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Classe che implementa Initializable. Rappresenta le informazioni di una singola recensione.
 */
public class ReviewInfoController implements Initializable {

    private Review review;

    @FXML
    Text lbl_review;
    @FXML
    Text lbl_reviewTitle;
    @FXML
    Text lbl_user_date;
    @FXML
    Text lbl_vote;
    @FXML
    Text lbl_descriptionTitle;
    @FXML
    TextArea txt_description;

    /**
     * Metodo derivato da Initializable.
     * @param location Path relativa all'oggetto iniziale. Nullo se non esiste.
     * @param resources Risorse utilizzare per risalire all'oggetto iniziale. Nullo se non esiste.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    /**
     * Imposta la pagina con i dati della recensione.
     * @param review Recensione da visualizzare.
     */
    public void setReview(Review review) {
        this.review = review;

        String[] name = review.getUserName().split(" ");

        String nome = name[0] + " " + name[1].charAt(0) + ".";

        lbl_reviewTitle.setText("Titolo: " + review.getTitle());
        if(review.getUserID() == MainClient.user.getId())
            lbl_user_date.setText("Di: " + nome + " il " + Utils.fromMySQLDate(review.getDate().split(" ")[0]) + " ( TUA )");
        else
            lbl_user_date.setText("Di: " + nome + " il " + Utils.fromMySQLDate(review.getDate().split(" ")[0]));
        lbl_vote.setText("Voto: " + review.getVote() + " / 5");
        txt_description.setWrapText(true);
        txt_description.setText(review.getText());
    }
}
