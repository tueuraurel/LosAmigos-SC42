package losamigos.smartcity;


/**
 * Created by aurelien on 14/03/18.
 */

public class Utilisateur {
    private String pseudo;
    private String MDP;
    private String dateNaissance;
    private int sexe;
    private float taille;
    private  float poids;
    private int age=0;

    public Utilisateur() {}

    public Utilisateur(String pseudo, String MDP, String date, int sexe, float taille, float poids) {
        this.pseudo = pseudo;
        this.MDP = MDP;
        this.dateNaissance = date;
        this.sexe = sexe;
        this.taille = taille;
        this.poids = poids;
        // CODER POUR OBTENIR L AGE EN FONCTION DE LA DATE
    }

    public String getPseudo() {


        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMDP() {
        return MDP;
    }

    public void setMDP(String MDP) {
        this.MDP = MDP;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public int getSexe() {
        return sexe;
    }

    public void setSexe(int sexe) {
        this.sexe = sexe;
    }

    public float getTaille() {
        return taille;
    }

    public void setTaille(float taille) {
        this.taille = taille;
    }

    public float getPoids() {
        return poids;
    }

    public void setPoids(float poids) {
        this.poids = poids;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    @Override
    public String toString() {
        return "Utilisateur{" +
                "pseudo='" + pseudo + '\'' +
                ", MDP='" + MDP + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", sexe=" + sexe +
                ", taille=" + taille +
                ", poids=" + poids +
                ", age=" + age +
                '}';
    }
}
