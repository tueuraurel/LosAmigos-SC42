package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aurelien on 16/03/18.
 */

public class RechercheReseauActivity extends Activity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche_nouveau_reseau_layout);
        ListView ListeDeNouveauxReseau = findViewById(R.id.listeViewRechercheNouveauReseau);


        ReseauBDD maBaseReseau = new ReseauBDD(this);
        maBaseReseau.open();
        ArrayList<Reseau> MesReseaux;
        MesReseaux = maBaseReseau.getAllReseauWithLieuEtVisibilite("Montpellier",1);
        maBaseReseau.close();

        if(MesReseaux!=null) {
            Log.d("ApresGETALL","La taille est :"+ MesReseaux.size());
            String[] NomReseaux = new String[MesReseaux.size()];

            for (int i = 0; i < MesReseaux.size(); i++) {
                NomReseaux[i] = MesReseaux.get(i).getSujet();
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RechercheReseauActivity.this,
                    android.R.layout.simple_list_item_1, NomReseaux);
            ListeDeNouveauxReseau.setAdapter(adapter);

            ListeDeNouveauxReseau.setOnItemClickListener(RechercheReseauActivity.this);
        }
        else {
            Toast.makeText(this, "Il n'y a pas de reseau visible dans votre ville"
                    , Toast.LENGTH_LONG).show();
        }

        //android.R.layout.simple_list_item_1 est une vue disponible de base dans le SDK android,
        //Contenant une TextView avec comme identifiant "@android:id/text1"


    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d("HelloListView", "You clicked Item: " + id + " at position:" + position);
        Log.d("HelloListView", "Vous avez choisi "+adapterView.getItemAtPosition(position));
        Intent intent = new Intent(RechercheReseauActivity.this,DemandeAdhesionActivity.class);
        intent.putExtra("reseauClique",(String)adapterView.getItemAtPosition(position));
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

}
