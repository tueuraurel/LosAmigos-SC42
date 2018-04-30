package losamigos.smartcity;

public class Commerce {

    int id;
    String nom;
    String pseudoCommercant;
    String localisation;

    public Commerce() {}

    public Commerce(int id, String nom, String pseudoCommercant, String localisation) {
        this.id = id;
        this.nom = nom;
        this.pseudoCommercant = pseudoCommercant;
        this.localisation = localisation;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPseudoCommercant() {
        return pseudoCommercant;
    }

    public void setPseudoCommercant(String pseudoCommercant) {
        this.pseudoCommercant = pseudoCommercant;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Commerce{" +
                "nom='" + nom + '\'' +
                ", pseudoCommercant='" + pseudoCommercant + '\'' +
                ", localisation='" + localisation + '\'' +
                ", id=" + id +
                '}';
    }
}
