package losamigos.smartcity;

public class Theme {
    String nom;
    int id;

    public Theme(String nom, int id) {
        this.nom = nom;
        this.id= id;
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
}
