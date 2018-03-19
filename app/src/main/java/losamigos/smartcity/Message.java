package losamigos.smartcity;


public class Message {
    private int id;
    private String contenu;
    private String sujetReseau;
    private String pseudoAuteur;

    public Message() {}

    public Message(String contenu, String sujetReseau, String pseudoAuteur) {
        this.contenu = contenu;
        this.sujetReseau = sujetReseau;
        this.pseudoAuteur = pseudoAuteur;
    }

    public Message(int id,String contenu, String sujetReseau, String pseudoAuteur) {
        this.id = id;
        this.contenu = contenu;
        this.sujetReseau = sujetReseau;
        this.pseudoAuteur = pseudoAuteur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getSujetReseau() {
        return sujetReseau;
    }

    public void setSujetReseau(String sujetReseau) {
        this.sujetReseau = sujetReseau;
    }

    public String getPseudoAuteur() {
        return pseudoAuteur;
    }

    public void setPseudoAuteur(String pseudoAuteur) {
        this.pseudoAuteur = pseudoAuteur;
    }

    @Override
    public String toString() {
        return "Message{" +
                "contenu='" + contenu + '\'' +
                ", sujetReseau='" + sujetReseau + '\'' +
                ", pseudoAuteur='" + pseudoAuteur + '\'' +
                '}';
    }
}
