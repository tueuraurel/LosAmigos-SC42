package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class publiciteActivity extends Activity {

    String titrePublicite = "Titre de la pub";
    String fournisseur = "Mon Commerce";
    String lienPub;
    Handler handler;

    publiciteActivity() {handler = new Handler();}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recuperation des préferecnes :
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Pour l'instant mise de l'url dans les preferences ici, a voir ensuite pour recuperation serveur
        SharedPreferences.Editor editor = preferences.edit();
        //editor.putString("lienPub","http://192.168.1.64/~aurelien/small.mp4");
        editor.putString("lienPub", "http://192.168.1.64/~aurelien/panda.jpg");
        editor.commit();


        // Recuperation du lien :
        //lienPub = preferences.getString("lienPub", "");

        // On recupere les informations d'une pub
        updatePubData();


    } // fin onCreate()


    public void affichagePub() {
        // Recuperation de l'extension :
        // l'appli est ici prepare pour des mp4 et des jpg, a voir pour la suite
        String extension = lienPub.substring(lienPub.length() - 3);
        Log.d("lienPub", extension);


        // Disposition du layout et recuperation des widgets
        setContentView(R.layout.publicite_image);
        VideoView video = (VideoView) findViewById(R.id.videoView);
        ImageView imageView = findViewById(R.id.pubImage);
        TextView titrePub = findViewById(R.id.titrePub);
        TextView commercePub = findViewById(R.id.commercePub);


        // Affichage du titre de la pub

        titrePub.setText(titrePublicite);

        // Affichage du commerce de la pub;

        commercePub.setText(fournisseur);


        // Debut difference entre une image ou une video

        if (extension.equals("mp4")) {

            video.setVisibility(VideoView.VISIBLE);
            imageView.setVisibility(ImageView.GONE);


            video.setMediaController(new MediaController(this));
            // Mise en place de la video
            video.setVideoURI(Uri.parse(lienPub));
            video.start();
        } else {
            if (extension.equals("jpg")) {
                //setContentView(R.layout.publicite_image);
                video.setVisibility(VideoView.GONE);
                imageView.setVisibility(ImageView.VISIBLE);
                this.setImage(lienPub, imageView);

            }
        }
    }

    // Cette methode modifie l'image d'un image view en allant chercher l'image sur internet
    private void setImage(final String url, final ImageView v) {

        // Comme on est en reseau on execute un thread different du thread UI
        new Thread(new Runnable() {
            // Lorsque le thread s execute
            public void run() {
                // Ce morceau de code est issu de : https://stackoverflow.com/questions/3681714/bad-bitmap-error-when-setting-uri le 29/04/18
                Bitmap bm = null;
                try {
                    URL aURL = new URL(url); // on cree une URL
                    URLConnection conn = aURL.openConnection(); // On ouvre une connection avec celle ci
                    conn.connect();
                    InputStream is = conn.getInputStream(); // On recupere le stream
                    BufferedInputStream bis = new BufferedInputStream(is); // Que l'on stocke
                    bm = BitmapFactory.decodeStream(bis); // Avant de le transformer en bitmap
                    bis.close();
                    is.close();
                    final Bitmap finalBm = bm;
                    // On demande ensuite au thread principal de changer l'image view
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            v.setImageBitmap(finalBm);
                        }
                    });

                } catch (IOException e) {
                    Log.d("getImageBitmap", "Error getting bitmap", e);

                }
            }
        }).start();
    } // fin setImage();


    // Cette fonction doit recuperer le json du serveur via recuperationPub et
    // mettre a jour les variables titre,fournisseur et lien
    private void updatePubData() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                final JSONObject json = RecuperationPub.getJSON("Montpellier");
                //final JSONArray json = RecuperationPub.getJSON(intent.getStringExtra("lieuUser"));
                Log.d("updateMessage","json: "+json);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(publiciteActivity.this, "Pas de publicite pour vous", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                titrePublicite=json.getString("titre");
                                fournisseur=json.getString("nomFournisseur");
                                lienPub=json.getString("lienPub");
                                affichagePub();
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }.start();

    }

    /*public JSONObject transformationJSONArrayToObject(JSONArray json) {
        try {
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonobject = json.getJSONObject(i);
                messages.add(new Message(jsonobject.getString("contenu"), jsonobject.getString("sujetReseau"), jsonobject.getString("pseudoAuteur")));
            }

            return messages;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}

    class RecuperationPub {

        // Recupere une publicite.
        public static JSONObject getJSON(String ville){
            try {
                URL url = new URL(MainActivity.chemin+"publicite/"+ville);

                Log.d("recupPub","URL  : "+url.toString());
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

