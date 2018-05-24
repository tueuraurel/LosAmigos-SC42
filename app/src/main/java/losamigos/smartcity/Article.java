package losamigos.smartcity;


public class Article {
    String titre;
    String auteur;
    String description;
    String URL;

    public Article(String titre, String auteur, String description, String URL){
        this.titre = titre;
        this.auteur = auteur;
        this.description = description;
        this.URL = URL;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getDescription() {
        return description;
    }

    public String getURL() {
        return URL;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
