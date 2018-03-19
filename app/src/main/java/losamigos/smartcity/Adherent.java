package losamigos.smartcity;



public class Adherent  {
    private String pseudo;
    //private ArrayList<Reseau> listeReseau= new ArrayList<>();
    private String sujetReseau;

    public Adherent(String pseudo, String sujetReseau) {
        this.pseudo = pseudo;
        this.sujetReseau = sujetReseau;
    }

    public Adherent() {}

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String  getSujetReseau() {
        return sujetReseau;
    }

    public void setSujetReseau(String sujetReseau) {
        this.sujetReseau = sujetReseau;
    }

    @Override
    public String toString() {
        return "Adherent{" +
                "pseudo='" + pseudo + '\'' +
                ", reseau=" + sujetReseau +
                '}';
    }
}
