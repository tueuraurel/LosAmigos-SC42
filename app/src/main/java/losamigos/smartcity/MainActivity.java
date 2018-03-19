package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connecte= (TextView) findViewById(R.id.connecte);
        pseudo= (EditText) findViewById(R.id.pseudoInput);
        password= (EditText) findViewById(R.id.passwordInput);
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
        new RequestTask(login,mdp).execute("http://10.0.2.2/~marine/mobile/serveur.php/utilisateur/"+login+"/"+password);
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
                HttpGet httpGet= new HttpGet("http://10.0.2.2/~marine/mobile/serveur.php/utilisateur/"+login+"/"+password);
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
                Intent intent = new Intent(MainActivity.this, ActivitePrincipale.class);
                startActivity(intent);
            }
        }
    }
}
