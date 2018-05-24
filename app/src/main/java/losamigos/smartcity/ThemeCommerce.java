package losamigos.smartcity;

public class ThemeCommerce {
    String nom;
    int id;
    int idNomPere;

    public ThemeCommerce() {}

    public ThemeCommerce(String nom, int id, int idNomPere) {
        this.nom = nom;
        this.id = id;
        this.idNomPere = idNomPere;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getIdNomPere() {
        return idNomPere;
    }

    public void setIdNomPere(int idNomPere) {
        this.idNomPere = idNomPere;
    }

    @Override
    public String toString() {
        return "ThemeCommerce{" +
                "nom='" + nom + '\'' +
                ", id=" + id +
                ", idNomPere=" + idNomPere +
                '}';
    }
}
