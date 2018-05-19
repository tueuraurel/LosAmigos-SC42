package losamigos.smartcity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommerceActivity extends AppCompatActivity {

    TextView textViewNomCommerce;
    Button boutonFavoriCommerce;
    Handler handler;

    public CommerceActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commerce);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCommerce();

        Intent intent = getIntent();
        int idCommerce = intent.getIntExtra("idTheme",0);
        Log.v("test",String.valueOf(idCommerce));
    }

    private void updateCommerce() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final int idCommerce = intent.getIntExtra("idCommerce",0);
                final String pseudoUser = intent.getStringExtra("pseudoUser");
                final JSONObject jsonCommerce = RecuperationInfosCommerce.getJSON(idCommerce, pseudoUser);

                if (jsonCommerce == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                textViewNomCommerce = (TextView) findViewById(R.id.nomCommerce);
                                boutonFavoriCommerce = (Button) findViewById(R.id.favoriCommerce);

                                textViewNomCommerce.setText(jsonCommerce.getString("nom"));

                                if (jsonCommerce.getBoolean("favori")) {
                                    boutonFavoriCommerce.setText(R.string.supprimerFavori);
                                    boutonFavoriCommerce.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            supprimerFavoriThread();
                                        }
                                    });
                                } else {
                                    boutonFavoriCommerce.setText(R.string.ajoutFavori);
                                    boutonFavoriCommerce.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            ajouterFavoriThread();
                                        }
                                    });
                                }
                            } catch (Exception e){

                            }
                        }
                    });
                }
            }
        }.start();

    }

    private void supprimerFavoriThread(){
        new Thread() {
            public void run() {
                final String supprimerFavori = supprimerFavori();

                if (supprimerFavori == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.erreur, Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (supprimerFavori.equals("1")){
                    handler.post(new Runnable() {
                        public void run() {
                            boutonFavoriCommerce.setText(R.string.ajoutFavori);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.erreur, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();
    }

    private String supprimerFavori() {
        Intent intent = getIntent();
        final int idCommerce = intent.getIntExtra("idCommerce",0);
        final String pseudoUser = intent.getStringExtra("pseudoUser");
        InputStream inputStream = null;

        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur/favoriCommerce/supprimer");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", pseudoUser);
            jsonObject.accumulate("idCommerce", idCommerce);
            //Log.d("insertionBase",jsonObject.toString());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            String resultat = out.toString();

            return resultat;

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

    private void ajouterFavoriThread(){
        new Thread() {
            public void run() {
                final String ajoutFavori = ajouterFavori();

                if (ajoutFavori == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.erreur, Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (ajoutFavori.equals("1")){
                    handler.post(new Runnable() {
                        public void run() {
                            boutonFavoriCommerce.setText(R.string.supprimerFavori);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.erreur, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();
    }

    private String ajouterFavori() {
        Intent intent = getIntent();
        final int idCommerce = intent.getIntExtra("idCommerce",0);
        final String pseudoUser = intent.getStringExtra("pseudoUser");
        InputStream inputStream = null;

        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur/favoriCommerce/ajout");

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", pseudoUser);
            jsonObject.accumulate("idCommerce", idCommerce);
            //Log.d("insertionBase",jsonObject.toString());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            String resultat = out.toString();

            return resultat;

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

class RecuperationInfosCommerce {

    // Recupere l'ensemble des commerces correspondant au thème
    public static JSONObject getJSON(int idCommerce, String pseudoUser){

        try {
            URL url;
            Log.v("IDCommerce",String.valueOf(idCommerce));
            url = new URL(MainActivity.chemin+"commerce/id/"+idCommerce);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String ligne=reader.readLine();
            reader.close();

            JSONObject jso=new JSONObject(ligne);
            //response=jso.getString("pseudo");

            Log.v("json", jso.toString());

            try {
                URL url2;
                url2 = new URL(MainActivity.chemin+"utilisateur/favoriCommerce/"+pseudoUser+"/"+idCommerce);

                Log.v("test",url2.toString());
                HttpURLConnection connection2 =
                        (HttpURLConnection)url2.openConnection();

                BufferedReader reader2 = new BufferedReader(
                        new InputStreamReader(connection2.getInputStream()));

                String ligne2=reader2.readLine();
                reader2.close();

                if (ligne2.equals("false")){
                    jso.put("favori", false);
                }else {
                    jso.put("favori", true);
                }

                Log.v("test",jso.toString());
                return jso;

            }catch(Exception e){
                return null;
            }

        }catch(Exception e){
            return null;
        }

    }
}
