package losamigos.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnnonceActivity extends AppCompatActivity {

    private Handler handler;
    private TextView textViewTitreAnnonce;
    private TextView textViewContenuAnnonce;
    private TextView textViewNomCommerce;
    private Button voirCommerce;

    public AnnonceActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAnnonce();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menucommerce, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.retourAccueil:
                Intent intentIn = getIntent();
                Intent intent = new Intent(AnnonceActivity.this, ActivitePrincipale.class );
                intent.putExtra("PSEUDO",intentIn.getStringExtra("pseudoUser"));
                intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateAnnonce() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final int idAnnonce = intent.getIntExtra("idAnnonce", 0);
                final JSONObject jsonAnnonce = RecuperationInfosAnnonce.getJSON(idAnnonce);

                if (jsonAnnonce == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(AnnonceActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                textViewTitreAnnonce = (TextView) findViewById(R.id.titreAnnonce);
                                textViewContenuAnnonce = (TextView) findViewById(R.id.contenuAnnonce);
                                textViewNomCommerce = (TextView) findViewById(R.id.nomCommerce);
                                voirCommerce = (Button) findViewById(R.id.voirCommerce);

                                textViewTitreAnnonce.setText(jsonAnnonce.getString("titre"));
                                textViewContenuAnnonce.setText(jsonAnnonce.getString("contenu"));
                                textViewNomCommerce.setText(jsonAnnonce.getString("nomCommerce"));

                                voirCommerce.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intentIn = getIntent();
                                        Intent intent = new Intent(AnnonceActivity.this, CommerceActivity.class );
                                        try {
                                            intent.putExtra("idCommerce", jsonAnnonce.getInt("idCommerce"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                                        intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
                                        intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
                                        intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
                                        startActivity(intent);
                                    }
                                });

                            } catch (Exception e){

                            }
                        }
                    });
                }
            }
        }.start();

    }
}

class RecuperationInfosAnnonce {

    public static JSONObject getJSON(int idAnnonce){

        try {
            URL url;
            url = new URL(MainActivity.chemin+"annonce/"+idAnnonce);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String ligne=reader.readLine();
            reader.close();

            JSONObject jso=new JSONObject(ligne);

            return jso;

        }catch(Exception e){
            return null;
        }

    }
}