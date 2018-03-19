package losamigos.smartcity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class InscriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        Button btn = (Button) findViewById(R.id.inscrbutton);
        final EditText pseudo = (EditText) findViewById(R.id.pseudoInput);
        final EditText MDP = (EditText) findViewById(R.id.passwordInput);
        final EditText jour = (EditText) findViewById(R.id.day);
        final EditText mois = (EditText) findViewById(R.id.month);
        final EditText annee = (EditText) findViewById(R.id.year);
        final String date = jour.getText().toString()+"-"+mois.getText().toString()+"-"+annee.getText().toString();
        final EditText taille = (EditText) findViewById(R.id.tailleInput);
        final EditText poids = (EditText) findViewById(R.id.poidsInput);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent  = new Intent(InscriptionActivity.this, ChoixThemeActivity.class);
                //creation de l'utilisateur dans la base du telephone
                final RadioGroup groupe = findViewById(R.id.groupe);
                int sexe=-1;
                RadioButton boutonRadio = findViewById(groupe.getCheckedRadioButtonId());
                if(boutonRadio!=null) {
                    if (boutonRadio.getText().equals("Femme")) {
                        sexe = 1;
                    }
                    if (boutonRadio.getText().equals("Homme")) {
                        sexe = 0;
                    }
                }
                UtilisateurBDD maBaseUtilisateur = new UtilisateurBDD(InscriptionActivity.this);
                maBaseUtilisateur.open();

                Log.d("CreationUtilisateur","en cours..");

                maBaseUtilisateur.insertUtilisateur(new Utilisateur(pseudo.getText().toString(), MDP.getText().toString(), date, sexe, Float.parseFloat(taille.getText().toString()), Float.parseFloat(poids.getText().toString())));
                //Log.d("CreationUtilisateur", "pseudo : "+intent.getStringExtra("pseudo"));
                maBaseUtilisateur.close();
                //ajouter l'utilisateur dans la base du serveur

                new RetrieveUtilisateurTask(pseudo.getText().toString(), MDP.getText().toString(), date, String.valueOf(sexe), taille.getText().toString(), poids.getText().toString()).execute();
                //lancement de la prochaine activité -> choix des themes
                startActivity(intent);
            }
        });
    }

}

class RetrieveUtilisateurTask extends AsyncTask<String, Void, Void> {

    private Exception exception;
    String pseudo;
    String MDP;
    String date;
    String sexe;
    String taille;
    String poids;

    public RetrieveUtilisateurTask(String pseudo, String MDP, String sexe, String taille, String poids, String date) {
        this.date = date;
        this.poids = poids;
        this.taille = taille;
        this.sexe = sexe;
        this.pseudo = pseudo;
        this.MDP = MDP;
    }

    protected Void doInBackground(String... urls) {
        try {
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("pseudo", pseudo));
            nameValuePairs.add(new BasicNameValuePair("MDP", MDP));
            nameValuePairs.add(new BasicNameValuePair("sexe", sexe));
            nameValuePairs.add(new BasicNameValuePair("taille", taille));
            nameValuePairs.add(new BasicNameValuePair("poids", poids));
            nameValuePairs.add(new BasicNameValuePair("dateNaissance", date));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);

            URL url = new URL("http://10.0.2.2/~marine/mobile/serveur.php/utilisateur");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream post = conn.getOutputStream();
            entity.writeTo(post);
            post.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine, response = "";

            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            post.close();
            in.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
