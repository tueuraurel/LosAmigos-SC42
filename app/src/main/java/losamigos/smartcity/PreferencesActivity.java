package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

public class PreferencesActivity extends Activity {
        Handler handler;
        String pseudo = "";
        EditText MDP;
        EditText taille;
        EditText poids;
        Button btn;
        Utilisateur utilisateur;
        EditText jour;
        EditText mois;
        EditText annee;
        RadioButton boutonRadio;
        RadioGroup groupe;
        Button affinage;

        public PreferencesActivity(){
            handler = new Handler();
            utilisateur = new Utilisateur();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preferences);
            btn = (Button) findViewById(R.id.Modifiebutton);
            affinage = (Button) findViewById(R.id.affinerThemes);
            MDP = (EditText) findViewById(R.id.passwordModifie);
            taille = (EditText) findViewById(R.id.tailleModifie);
            poids = (EditText) findViewById(R.id.poidsModifie);
            jour = (EditText) findViewById(R.id.dayModifie);
            mois = (EditText) findViewById(R.id.monthModifie);
            annee = (EditText) findViewById(R.id.yearModifie);
            groupe = findViewById(R.id.groupeModifie);

            final Intent intent2 = getIntent();
            pseudo = intent2.getStringExtra("PSEUDO");

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent  = new Intent(PreferencesActivity.this, ActivitePrincipale.class);
                    Intent intent2 = getIntent();
                    final String pseudo = intent2.getStringExtra("PSEUDO");
                    intent.putExtra("LATITUDE", intent2.getStringExtra("LATITUDE"));
                    intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                    intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                    intent.putExtra("pseudoUser",pseudo);
                    intent.putExtra("PSEUDO",pseudo);
                    int sexe=-1;
                    boutonRadio = findViewById(groupe.getCheckedRadioButtonId());
                    if(boutonRadio!=null) {
                        if (boutonRadio.getText().equals("Femme")) {
                            sexe = 1;
                        }
                        if (boutonRadio.getText().equals("Homme")) {
                            sexe = 0;
                        }
                    }
                    final String date = jour.getText().toString()+"-"+mois.getText().toString()+"-"+annee.getText().toString();
                    //nouvelles valeurs
                    HashMap<String, String> parametres = new HashMap<String, String>();
                    parametres.put("pseudo",pseudo);
                    parametres.put("MDP", MDP.getText().toString());
                    parametres.put("taille",taille.getText().toString());
                    parametres.put("poids",poids.getText().toString());
                    parametres.put("dateNaissance", date);
                    parametres.put("sexe", String.valueOf(sexe));
                    //creation de l'utilisateur dans la base du serveur
                    new UtilisateurModifieTask(pseudo).execute(parametres);
                    startActivity(intent);
                }
            });

            affinage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent  = new Intent(PreferencesActivity.this, AffinageThemeActivity.class);
                    Intent intent2 = getIntent();
                    final String pseudo = intent2.getStringExtra("PSEUDO");
                    intent.putExtra("LATITUDE", intent2.getStringExtra("LATITUDE"));
                    intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                    intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                    intent.putExtra("pseudoUser",pseudo);
                    intent.putExtra("PSEUDO",pseudo);
                    startActivity(intent);
                }
            });
        }

    @Override
    protected void onResume() {
        super.onResume();
        updateUtilisateurData(pseudo);
    }

    private void updateUtilisateurData(final String pseudo) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetchUtilisateur.getJSON(pseudo);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(PreferencesActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            utilisateur = renderUtilisateur(json);
                            MDP.setText(utilisateur.getMDP());
                            taille.setText(String.valueOf(utilisateur.getTaille()));
                            poids.setText(String.valueOf(utilisateur.getPoids()));
                            String[] date = utilisateur.getDateNaissance().split("-");
                            jour.setText(date[0]);
                            mois.setText(date[1]);
                            annee.setText(date[2]);
                            if(utilisateur.getSexe() == 1) {
                                groupe.check(R.id.femmeModifie);
                            }
                            if(utilisateur.getSexe() == 0){
                                groupe.check(R.id.hommeModifie);
                            }
                        }
                    });
                }
            }
        }.start();

    }

    private Utilisateur renderUtilisateur(JSONObject json) {
        Utilisateur utilisateur = null;
        try {
            utilisateur = new Utilisateur(json.getString("pseudo"), json.getString("MDP"), json.getString("dateNaissance"), json.getInt("sexe"),(float)json.getDouble("taille"), (float)json.getDouble("poids"));
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return utilisateur;
    }

    }

    class UtilisateurModifieTask extends AsyncTask<HashMap<String,String>, Void, Void> {

    public String pseudo;

    public UtilisateurModifieTask(String pseudo) {
        this.pseudo = pseudo;
    }

    @Override
    protected Void doInBackground(HashMap<String, String>[] hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        InputStream inputStream = null;
        try {
            // 1. crée HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. spécifier que c'est une requête POST
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateurModifie");

            String json = "";

            // 3. construire le jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", hashMap.get("pseudo"));
            jsonObject.accumulate("MDP", hashMap.get("MDP"));
            jsonObject.accumulate("sexe", Integer.parseInt(hashMap.get("sexe")));
            jsonObject.accumulate("taille", Float.parseFloat(hashMap.get("taille")));
            jsonObject.accumulate("dateNaissance", hashMap.get("dateNaissance"));
            jsonObject.accumulate("poids", Float.parseFloat(hashMap.get("poids")));

            // 4. convertir JSONObject en String
            json = jsonObject.toString();

            // 5. json en StringEntity
            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            // 7. mise en place du Headers permettant au serveur de connaitre le format des données
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Executer la requete POST
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. recevoir la reponse
            inputStream = httpResponse.getEntity().getContent();

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
