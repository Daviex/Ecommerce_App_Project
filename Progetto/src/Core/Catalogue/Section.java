package Core.Catalogue;

import java.util.ArrayList;

/**
 * Classe contenente le informazioni su una sezione del catalogo.
 * Contiene Getter & Setter
 */
public class Section {

    private int id;
    private String name;
    private String desc;

    public ArrayList<Category> categories;

    /**
     * Costruttore Section
     * @param id ID Sezione
     * @param name Nome Sezione
     * @param desc Descrizione Sezione
     */
    public Section(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;

        categories = new ArrayList<Category>();
    }

    public int getSectionID() { return id; }
    public String getSectionName() { return name; }
    public String getSectionDesc() { return desc; }

    /**
     * Aggiunge una nuova categoria alla sezione.
     * @param idCat ID Categoria
     * @param nameCat Nome Categoria
     * @param descCat Descrizione Categoria
     */
    public void setCategory(int idCat, String nameCat, String descCat) {
        categories.add(new Category(idCat, nameCat, descCat));
    }

    /**
     * Controlla se la sezione ha categorie.
     * @return True se la sezione ha categorie, False altrimenti.
     */
    public Boolean hasCategories() {
        if(categories.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * Effettuo l'override del metodo toString della classe Object.
     * @return Nome della sezione.
     */
    @Override
    public String toString() { return name; }
}
