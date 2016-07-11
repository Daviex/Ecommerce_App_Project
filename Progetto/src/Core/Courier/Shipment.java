package Core.Courier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe con le informazioni di una spedizione di un ordine.
 * Contiene Getter & Setter.
 */
public class Shipment {
    private int id;
    private Date startDate;
    private Date goalDate;
    private String startLocation;
    private String goalLocation;
    private String addressGoal;
    private int capGoal;
    private Courier courier;

    /**
     * Costruttore Shipment.
     * @param id ID della spedizione.
     * @param startDate Data di partenza.
     * @param goalDate Data di arrivo.
     * @param startLoc Luogo di partenza.
     * @param goalLoc Luogo di arrivo.
     * @param addrGoal Indirizzo d'arrivo della spedizione.
     * @param capGoal CAP d'arrivo.
     * @param courier Corriere che ha in consegna la spedizione.
     */
    public Shipment(int id, String startDate, String goalDate, String startLoc, String goalLoc, String addrGoal, int capGoal, Courier courier) {
        this.id = id;

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if(startDate != null) {
            try {
                this.startDate = sdf.parse(startDate);
            } catch(Exception exc) {
                System.out.println("Errore ConstructorDateOrder-StartDate: " + exc.getMessage());
                exc.printStackTrace();
            }
        } else {
            this.startDate = null;
        }

        if(goalDate != null) {
            try {
                this.startDate = sdf.parse(goalDate);
            } catch(Exception exc) {
                System.out.println("Errore ConstructorDateOrder-GoalDate: " + exc.getMessage());
                exc.printStackTrace();
            }
        } else {
            this.goalDate = null;
        }

        this.startLocation = startLoc;
        this.goalLocation = goalLoc;
        this.addressGoal = addrGoal;
        this.capGoal = capGoal;
        this.courier = courier;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getGoalDate() { return goalDate; }
    public void setGoalDate(Date goalDate) { this.goalDate = goalDate; }
    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    public String getGoalLocation() { return goalLocation; }
    public void setGoalLocation(String goalLocation) { this.goalLocation = goalLocation; }
    public String getAddressGoal() { return addressGoal; }
    public void setAddressGoal(String addressGoal) { this.addressGoal = addressGoal; }
    public int getCapGoal() { return capGoal; }
    public void setCapGoal(int capGoal) { this.capGoal = capGoal; }
    public Courier getCourier() { return courier; }
    public void setCourier(Courier courier) { this.courier = courier; }
}