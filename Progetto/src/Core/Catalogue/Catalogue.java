package Core.Catalogue;

import java.util.ArrayList;

/**
 * Classe contenente le informazioni sul catalogo.
 * Contiene Getter & Setter.
 */
public class Catalogue {
    private ArrayList<Section> sections;

    /**
     * Costruttore Catalogue.
     */
    public Catalogue() { }

    public ArrayList<Section> getCatalogue() { return sections; }

    public void setCatalogue(ArrayList<Section> sections) { this.sections = sections; }

    /**
     * Aggiunge una sezione al catalogo.
     * @param section Sezione da aggiungere.
     */
    public void addSection(Section section) {
        sections.add(section);
    }

    /**
     * Controlla se esistono sezioni.
     * @return Se ci sono sezioni, ritorna True, False altrimenti.
     */
    public boolean hasSections() {
        if(sections.size() > 0)
            return true;
        else
            return false;
    }
}
