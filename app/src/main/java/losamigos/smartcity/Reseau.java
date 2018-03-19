package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class Reseau {
    private String sujet;
    private String description;
    private String pseudoAdmin;
    private String localisation;
    private int visibilite=0;

    public Reseau() {}

    public Reseau(String sujet, String description, String pseudoAdmin, String localisation, int visibilite) {
        this.sujet = sujet;
        this.description = description;
        this.pseudoAdmin = pseudoAdmin;
        this.localisation = localisation;
        this.setVisibilite(visibilite);
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPseudoAdmin() {
        return pseudoAdmin;
    }

    public void setPseudoAdmin(String pseudoAdmin) {
        this.pseudoAdmin = pseudoAdmin;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public int getVisibilite() {
        return visibilite;
    }

    public void setVisibilite(int visibilite) {
        if (visibilite==0 || visibilite ==1) {
            this.visibilite = visibilite;
        }
    }

    @Override
    public String toString() {
        return "Reseau{" +
                "sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", pseudoAdmin='" + pseudoAdmin + '\'' +
                ", localisation='" + localisation + '\'' +
                ", visibilite='"+ visibilite +'\''+
                '}';
    }

    /**
     * Created by aurelien on 15/03/18.
     */

    public static class ListeMessageReseau extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setContentView(R.layout.liste_message_reseaux);

            Intent intent= getIntent();
            TextView test = findViewById(R.id.test);
            test.setText(intent.getStringExtra("sujetReseau"));
        }
    }
}
