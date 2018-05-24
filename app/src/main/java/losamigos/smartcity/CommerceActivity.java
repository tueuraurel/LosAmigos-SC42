package losamigos.smartcity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommerceActivity extends AppCompatActivity {

    private TextView textViewNomCommerce;
    private TextView textViewDescription;
    private TextView textViewAdresse;
    private TextView textViewTelephone;
    private TextView textViewHoraires;
    private String lienImage;
    private String nomCommerce;
    private ImageView logoCommerce;
    private Button boutonFavoriCommerce;
    private Button voirOffresCommerce;
    private Button voirMap;
    private Handler handler;
    private double longitude, latitude;

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
                Intent intent = new Intent(CommerceActivity.this, ActivitePrincipale.class );
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
                                textViewDescription = (TextView) findViewById(R.id.descriptionCommerce);
                                textViewTelephone = (TextView) findViewById(R.id.numeroTelephone);
                                textViewAdresse = (TextView) findViewById(R.id.adresse);
                                textViewHoraires = (TextView) findViewById(R.id.horaires);
                                logoCommerce = (ImageView) findViewById(R.id.imageCommerce);
                                voirMap = (Button) findViewById(R.id.voirMap);

                                textViewNomCommerce.setText(jsonCommerce.getString("nom"));
                                textViewDescription.setText(jsonCommerce.getString("description"));
                                textViewTelephone.setText(jsonCommerce.getString("numeroTelephone"));
                                textViewAdresse.setText(jsonCommerce.getString("adresse"));
                                textViewHoraires.setText(jsonCommerce.getString("horaires"));

                                lienImage = jsonCommerce.getString("lienImage");

                                if (!lienImage.equals("")) {
                                    new DownloadImageTask(logoCommerce)
                                            .execute(lienImage);
                                }

                                longitude = jsonCommerce.getDouble("longitude");
                                latitude = jsonCommerce.getDouble("latitude");
                                nomCommerce = jsonCommerce.getString("nom");

                                voirOffresCommerce = (Button) findViewById(R.id.voirOffresCommerce);
                                voirOffresCommerce.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intentIn = getIntent();
                                        Intent intent = new Intent(CommerceActivity.this, OffreCommerceActivity.class );
                                        intent.putExtra("idCommerce", intentIn.getIntExtra("idCommerce",0));
                                        intent.putExtra("nomCommerce", nomCommerce);
                                        intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                                        intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
                                        intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
                                        intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
                                        startActivity(intent);
                                    }
                                });

                                if (longitude != 0 && latitude != 0) {
                                    voirMap.setVisibility(View.VISIBLE);
                                    voirMap.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+ String.valueOf(latitude) +","+ String.valueOf(longitude) + "("+ nomCommerce +")");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);
                                            }
                                        }
                                    });
                                }


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
                Intent intent = getIntent();
                final int idCommerce = intent.getIntExtra("idCommerce",0);
                final String pseudoUser = intent.getStringExtra("pseudoUser");
                final String supprimerFavori = supprimerFavori(idCommerce, pseudoUser);

                if (supprimerFavori == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.erreur, Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (supprimerFavori.equals("1")){
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(CommerceActivity.this, R.string.commerceSupprimeFavori, Toast.LENGTH_LONG).show();
                            boutonFavoriCommerce.setText(R.string.ajoutFavori);
                            boutonFavoriCommerce.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    ajouterFavoriThread();
                                }
                            });
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

    public static String supprimerFavori(int idCommerce, String pseudoUser) {
        try {
            URL url = new URL(MainActivity.chemin+"utilisateur/favoriCommerce/supprimer/"+pseudoUser+"/"+idCommerce);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer reponse = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                reponse.append(tmp).append("\n");
            reader.close();

            String resultat = reponse.toString();

            resultat = resultat.replaceAll("\\s+","");

            return resultat;

        } catch (IOException e) {
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
                            Toast.makeText(CommerceActivity.this, R.string.commerceAjouteFavori, Toast.LENGTH_LONG).show();
                            boutonFavoriCommerce.setText(R.string.supprimerFavori);
                            boutonFavoriCommerce.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    supprimerFavoriThread();
                                }
                            });
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
        InputStream inputStream;

        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(MainActivity.chemin+"utilisateur/favoriCommerce/ajout");

            String json;

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("pseudo", pseudoUser);
            jsonObject.accumulate("idCommerce", idCommerce);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

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

            return out.toString();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

class RecuperationInfosCommerce {

    public static JSONObject getJSON(int idCommerce, String pseudoUser){

        try {
            URL url;
            url = new URL(MainActivity.chemin+"commerce/id/"+idCommerce);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String ligne=reader.readLine();
            reader.close();

            JSONObject jso=new JSONObject(ligne);

            try {
                URL url2;
                url2 = new URL(MainActivity.chemin+"utilisateur/favoriCommerce/"+pseudoUser+"/"+idCommerce);

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

                return jso;

            }catch(Exception e){
                return null;
            }

        }catch(Exception e){
            return null;
        }

    }
}

