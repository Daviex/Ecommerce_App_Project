package Core.Review;

/**
 * Classe con le informazioni riguardo ad una recensione.
 * Contiene Getter & Setter.
 */
public class Review {
    public int ID;
    private String title;
    private String text;
    private String date;
    private int vote;

    private int userID;
    private String userName;

    /**
     * Costruttore Review.
     * @param ID Id.
     * @param title Titolo
     * @param text Testo.
     * @param date Data.
     * @param vote Voto.
     * @param userID ID Utente.
     * @param userName Nome Utente.
     */
    public Review(int ID, String title, String text, String date, int vote, int userID, String userName) {
        this.ID = ID;
        this.title = title;
        this.text = text;
        this.date = date;
        this.vote = vote;
        this.userID = userID;
        this.userName = userName;
    }

    public int getID() { return ID; }
    public String getTitle() { return title; }
    public String getText() { return text; }
    public String getDate() { return date; }
    public int getVote() { return vote; }
    public int getUserID() { return userID; }
    public String getUserName() {return userName; }

    @Override
    public String toString() {
        return title;
    }
}
