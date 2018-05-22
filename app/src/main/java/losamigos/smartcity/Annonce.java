package losamigos.smartcity;


public class Annonce {
    private int id;
    private int idTheme;
    private int idCommerce;
    private String titre;
    private String contenu;
    private String nomCommerce;

    public Annonce(String titre, String contenu) {
        this.titre = titre;
        this.contenu = contenu;
    }

    public Annonce(int id, int idTheme, int idCommerce, String titre, String contenu, String nomCommerce) {
        this.id = id;
        this.idTheme = idTheme;
        this.idCommerce = idCommerce;
        this.titre = titre;
        this.contenu = contenu;
        this.nomCommerce = nomCommerce;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTheme() {
        return idTheme;
    }

    public void setIdTheme(int idTheme) {
        this.idTheme = idTheme;
    }

    public int getIdCommerce() {
        return idCommerce;
    }

    public void setIdCommerce(int idCommerce) {
        this.idCommerce = idCommerce;
    }

    public String getNomCommerce() {
        return nomCommerce;
    }

    public void setNomCommerce(String nomCommerce) {
        this.nomCommerce = nomCommerce;
    }

    @Override
    public String toString() {
        return "Annonce{" +
                "titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                '}';
    }
}
