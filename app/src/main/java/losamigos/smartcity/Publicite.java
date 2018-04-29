package losamigos.smartcity;

public class Publicite {
    private String titre;
    private String fournisseur;
    private String lienPub;
    private String lieu;

    public Publicite(String titre, String fournisseur, String lienPub, String lieu) {
        this.titre = titre;
        this.fournisseur = fournisseur;
        this.lienPub = lienPub;
        this.lieu = lieu;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getLienPub() {
        return lienPub;
    }

    public void setLienPub(String lienPub) {
        this.lienPub = lienPub;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
}
