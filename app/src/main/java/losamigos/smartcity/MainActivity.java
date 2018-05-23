package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity {
    public TextView connecte;
    public EditText pseudo;
    public EditText password;
    public static String chemin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.deleteDatabase("SmartCity.db");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recuperation des preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences.Editor editor = preferences.edit();
        //tutu
        //editor.putString("cheminDev", "http://192.168.1.64/~aurelien/projetMobile/serveur/serveur.php/");
        //editor.putString("cheminDev","http://192.168.1.114/~aurelien/projetMobile/serveur/serveur.php/");
        // marine
        //editor.putString("cheminDev","http://10.0.2.2/~marine/mobile/serveur.php/");
        // sofian
        //editor.putString("cheminDev","http://192.168.1.14/~sofian/smartcity/serveur/serveur.php/");
        //editor.putString("cheminDev","http://192.168.1.55/~sofian/smartcity/serveur/serveur.php/");
        //editor.putString("cheminDev","https://calypso42.ddns.net/~smartcity/smartcity/serveur.php/");

        //editor.commit();

        // a enlever après le dev
        this.deleteDatabase("SmartCity.db");

        //chemin = "https://calypso42.ddns.net/~smartcity/smartcity/serveur.php/";
        chemin = "http://192.168.1.151/~smartcity/smartcity/serveur.php/";


        // Skip la partie connection
        if (!preferences.getString("pseudoUser", "").isEmpty() && !preferences.getString("mdp", "").isEmpty()) {
            String login = preferences.getString("pseudoUser", "");
            String mdp = preferences.getString("mdp", "");
            new RequestTask(login, mdp).execute(chemin + "utilisateur/" + login + "/" + mdp);
        }

            connecte = (TextView) findViewById(R.id.connecte);
            pseudo = (EditText) findViewById(R.id.pseudoInput);
            password = (EditText) findViewById(R.id.passwordInput);
            Button btn = (Button) findViewById(R.id.okbutton);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Affiche();
                }
            });

            Button btn2 = (Button) findViewById(R.id.inscriptionbutton);
            btn2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, InscriptionActivity.class);
                    startActivity(intent);
                }
            });
        }

    public void Affiche(){
        String login = pseudo.getText().toString();
        String mdp = password.getText().toString();
        new RequestTask(login,mdp).execute(chemin+"utilisateur/"+login+"/"+password);
    }

    private class RequestTask extends AsyncTask<String, Void, String> {
        private String response = "";
        String login;
        String password;

        public RequestTask(String login, String password){
            this.login = login;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... urls) {
            response=Recup_WS_domo();
            return response;
        }


        public String Recup_WS_domo(){
            response="";
            HttpClient httpclient= new DefaultHttpClient();
            try {
                HttpGet httpGet= new HttpGet(chemin+"utilisateur/"+login+"/"+password);
                HttpResponse httpresponse=httpclient.execute(httpGet);
                HttpEntity httpentity=httpresponse.getEntity();
                if (httpentity!=null){
                    InputStream inputstream=httpentity.getContent();
                    BufferedReader bufferedreader=new BufferedReader(
                            new InputStreamReader(inputstream));
                    String ligne=bufferedreader.readLine();
                    bufferedreader.close();

                    JSONObject jso=new JSONObject(ligne);
                    response=jso.getString("pseudo");


                }
            } catch (Exception e) {
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("")){
                connecte.setText("Erreur de connexion !");
            }
            else {
                connecte.setText(result + " est connecté !");
                new RequestInfosTask(login).execute(chemin+"choisiVille/"+login);
            }
        }
    }

    private class RequestInfosTask extends AsyncTask<String, Void, Villes> {
        private Villes villes;
        String login;

        public RequestInfosTask(String login){
            this.login = login;
        }

        @Override
        protected Villes doInBackground(String... urls) {
            villes =Recup_WS_Villes_domo();
            return villes;
        }


        public Villes Recup_WS_Villes_domo(){
            HttpClient httpclient= new DefaultHttpClient();
            try {
                HttpGet httpGet= new HttpGet(chemin+"choisiVille/"+login);
                HttpResponse httpresponse=httpclient.execute(httpGet);
                HttpEntity httpentity=httpresponse.getEntity();
                if (httpentity!=null){
                    InputStream inputstream=httpentity.getContent();
                    BufferedReader bufferedreader=new BufferedReader(
                            new InputStreamReader(inputstream));
                    StringBuilder strinbulder=new StringBuilder();
                    String ligne=bufferedreader.readLine();
                    bufferedreader.close();

                    JSONObject jso=new JSONObject(ligne);
                    villes = new Villes(jso.getString("nom"), jso.getString("latitude"), jso.getString("longitude"));


                }
            } catch (Exception e) {
            }
            return villes;
        }

        protected void onPostExecute(Villes result) {
            if(result.equals(null)){
                Toast.makeText(MainActivity.this, "Erreur : Ville non trouvée !", Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, ActivitePrincipale.class);
                intent.putExtra("PSEUDO",login);
                intent.putExtra("LATITUDE", villes.getLatitude());
                intent.putExtra("LONGITUDE", villes.getLongitude());
                intent.putExtra("VILLE", villes.getNom());
                Log.d("test pseudo", login);
                Log.d("test Latitude", villes.getLatitude());
                Log.d("test Longitude", villes.getLongitude());
                CheckBox resterConnecter=findViewById(R.id.resterConnecter);

                //On ajoute les preferences de connexion pour skip la connexion au prochain demarrage
                if (resterConnecter.isChecked()){
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pseudoUser",login);
                    editor.putString("mdp",password.getText().toString());
                    editor.commit();
                }
                startActivity(intent);
                finish();
            }
        }
    }
}
