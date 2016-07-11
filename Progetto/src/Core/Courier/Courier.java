package Core.Courier;

/**
 * Classe contenente le informazioni per un corriere.
 * Contiene Getter & Setter.
 */
public class Courier {
    private int id;
    private String name;
    private double cost;
    private String expressTime;

    /**
     * Costruttore Courier.
     * @param id ID del corriere.
     * @param name Nome.
     * @param cost Costo di spedizione.
     * @param expressTime Tempo di consegna.
     */
    public Courier(int id, String name, double cost, String expressTime) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.expressTime = expressTime;
    }

    public int getCourierID() { return id; }
    public String getCourierName() { return name; }
    public double getCourierCost() { return cost; }
    public String getCourierExpressTime() { return expressTime; }
}
