package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;



public class CreationReseauxActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_reseaux_layout);
        final EditText editTextSujet = findViewById(R.id.newSujetReseau);
        final EditText editTextDescription = findViewById(R.id.newDescriptionReseau);
        Button confirmer = findViewById(R.id.newReseauConfirmer);



        final Intent intent = getIntent();

        if (intent!=null){
            final RadioGroup groupe = findViewById(R.id.radioGroupeVisibilite);
            confirmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int visibilite=-1;
                    RadioButton boutonRadio = findViewById(groupe.getCheckedRadioButtonId());
                    if(boutonRadio!=null){
                        if (boutonRadio.getText().equals("Reseau public")){
                            visibilite=1;
                        }
                        if (boutonRadio.getText().equals("Reseau prive")){
                            visibilite=0;
                        }
                    }else {
                        Toast.makeText(CreationReseauxActivity.this, "Veuillez choisir public ou prive", Toast.LENGTH_LONG).show();
                    }
                    if (editTextSujet.getText().toString().isEmpty()){ /* Il faudra aussi verifier que le sujet n est pas deja dans la base */
                        Toast.makeText(CreationReseauxActivity.this, "Veuillez remplir le sujet", Toast.LENGTH_LONG).show();
                    }else {
                        if (editTextDescription.getText().toString().isEmpty()) {
                            Toast.makeText(CreationReseauxActivity.this, "Veuillez remplir la description", Toast.LENGTH_LONG).show();
                        }
                        else {
                                ReseauBDD maBaseReseau = new ReseauBDD(CreationReseauxActivity.this);
                                maBaseReseau.open();

                                Log.d("CreationReseau","La visibilite est : " + visibilite);

                                maBaseReseau.insertReseau(new Reseau (editTextSujet.getText().toString(),editTextDescription.getText().toString(),
                                        intent.getStringExtra("pseudoUser"),intent.getStringExtra("lieuUser"),visibilite));
                                Log.d("CreationReseau", "pseudoUser : "+intent.getStringExtra("pseudoUser"));
                                Log.d("CreationReseau", "lieuUser : " + intent.getStringExtra("lieuUser"));
                                maBaseReseau.close();
                    /* A modifier ensuite pour ne pas pouvoir faire un retour dessus,
                     utiliser finish();
                     */

                                Intent intentRetour = new Intent(CreationReseauxActivity.this,ListeReseauUtilisateurActivity.class);
                                intentRetour.putExtra("pseudoUser",intent.getStringExtra("pseudoUser"));
                                intentRetour.putExtra("lieuUser",intent.getStringExtra("lieuUser"));
                                startActivity(intentRetour);
                            }
                    }

                }
            });

        }
    }

}