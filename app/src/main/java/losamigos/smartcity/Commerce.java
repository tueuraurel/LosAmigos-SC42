package losamigos.smartcity;

public class Commerce {

    private int id;
    private String nom;
    private String pseudoCommercant;
    private String localisation;
    private String longitude;
    private String latitude;
    private double distance;

    public Commerce() {}

    public Commerce(int id, String nom, String pseudoCommercant, String localisation, String longitude, String latitude) {
        this.id = id;
        this.nom = nom;
        this.pseudoCommercant = pseudoCommercant;
        this.localisation = localisation;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Commerce(int id, String nom, String pseudoCommercant, String localisation, String longitude, String latitude, double distance) {
        this.id = id;
        this.nom = nom;
        this.pseudoCommercant = pseudoCommercant;
        this.localisation = localisation;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Commerce{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", pseudoCommercant='" + pseudoCommercant + '\'' +
                ", localisation='" + localisation + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", distance=" + distance +
                '}';
    }
}
