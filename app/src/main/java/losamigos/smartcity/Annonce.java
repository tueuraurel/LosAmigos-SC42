package losamigos.smartcity;

/**
 * Created by aurelien on 14/03/18.
 */

public class Annonce {
    private String titre;
    private String contenu;

    public Annonce(String titre, String contenu) {
        this.titre = titre;
        this.contenu = contenu;
    }

    public Annonce() {}

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    @Override
    public String toString() {
        return "Annonce{" +
                "titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                '}';
    }
}
