package losamigos.smartcity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/* Premiere page de l'application, on y presente les 4 boutons principaux.
C'est ici également que l'on récupere les informations de l'utilisateur qui seront ensuite
passe d activite en activite (le pseudo et la ville)
 */
public class ActivitePrincipale extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Handler handler;
    String IDPLace;


    // handler permet de
    public ActivitePrincipale() {
        handler = new Handler();
    }


    @Override
    // A la creation de l'activite :
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updatePlaceData();

        final Intent intentPrecedent = getIntent();

        setContentView(R.layout.activite_principale);

        UtilisateurBDD maBaseUtilisateur = new UtilisateurBDD(this);
        maBaseUtilisateur.open();
        /* Ici il faudra une methode pour recuperer le login et le lieu de la personne automatiquement */
        final String pseudoUser = intentPrecedent.getStringExtra("PSEUDO");
        Log.d("pseudoActivitePri",pseudoUser);
        //final String pseudoUser = "Aurelien";
        final String lieuUser = intentPrecedent.getStringExtra("VILLE");
        Log.d("villeActivitePri",lieuUser);



        Button boutonReseau = findViewById(R.id.Reseaux);
        boutonReseau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitePrincipale.this,ListeReseauUtilisateurActivity.class);
                intent.putExtra("pseudoUser",pseudoUser);
                intent.putExtra("lieuUser",lieuUser);
                startActivity(intent);
            }
        });

        Button boutonActualite = findViewById(R.id.Actualites);
        boutonActualite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitePrincipale.this,ActualiteActivity.class);
                Intent intent2 = getIntent();
                intent.putExtra("pseudoUser",pseudoUser);
                //intent.putExtra("lieuUser",lieuUser);
                intent.putExtra("LATITUDE", intent2.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });

        Button boutonCommerce = findViewById(R.id.Commerces);
        boutonCommerce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitePrincipale.this,ChoixPartieCommerceActivity.class);
                Intent intent2 = getIntent();
                intent.putExtra("pseudoUser",pseudoUser);
                //intent.putExtra("lieuUser",lieuUser);
                intent.putExtra("LATITUDE", intent2.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });

        Button boutonPub = findViewById(R.id.pub);
        boutonPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitePrincipale.this,publiciteActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menusetting, menu);
        return super.onCreateOptionsMenu(menu);
    }

  /* public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPreference:
                              fragment=getFragmentManager().findFragmentById(R.id.prefe )
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void recupereIDPlace(JSONObject json) {
        try {
            JSONArray jsonResult = json.getJSONArray("results");
            for (int i = 0; i < jsonResult.length(); i++) {
                IDPLace = jsonResult.getJSONObject(i).getString("place_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updatePlaceData() {
        new Thread() {
            public void run() {
                Intent intent = getIntent();
                String latitude = intent.getStringExtra("LATITUDE");
                String longitude = intent.getStringExtra("LONGITUDE");
                Log.d("PlaceData ",latitude);
                Log.d("PlaceData ",longitude);
                final JSONObject json = RemoteFetchIDPlace.getJSON(latitude,longitude);
                Log.d("jsonPlaceData", json.toString());

                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(ActivitePrincipale.this, R.string.data_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            recupereIDPlace(json);
                            placePhotosTask();
                        }
                    });
                }
            }
        }.start();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "La connexion à Google Places API a échoué",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Charge la première photo pour l'ID d'un lieu provenant de l'API Geo Data
         * L'ID du lieu doit être le seul (et unique) paramètre
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            Log.d("ActiPrinciBackground",String.valueOf(params.length));
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            Log.d("param", params[0]);
            AttributedPhoto attributedPhoto = null;
            Log.d("tst",placeId);
            PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).await();
            Log.d("GoogleCrash",result.getStatus().toString());

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Obtenir la premiere photo et son attribution
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Charge une image redimensionnée pour cette photo
                    Bitmap bitImage = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, bitImage);
                    Log.d("att","test "+attributedPhoto.toString());


                }
                // Pour empêcher les fuites de mémoire
                photoMetadataBuffer.release();
            }
            else{
                Log.d("erreur", "Erreur de recuperation image");
            }
            return attributedPhoto;
        }

        /**
         * Classe représentant l'image et son attribution
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }

    private void placePhotosTask() {
        final ImageView image;
        image = (ImageView) findViewById(R.id.imageVille);

        final String placeId = IDPLace;
        Log.d("id place",placeId);

        // Crée une nouvelle tâche asynchrone qui affiche la bitmap une fois chargée
        new PhotoTask(image.getMaxWidth(), image.getMaxHeight()) {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // La photo est chargée, on l'affiche
                    image.setImageBitmap(attributedPhoto.bitmap);
                    RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), attributedPhoto.bitmap);
                    roundDrawable.setCircular(true);
                    image.setImageDrawable(roundDrawable);
                }

            }
        }.execute(placeId);
    }

}






 /* ici une liste d'exemple pour avoir une base pré-rempli pour les test sans
        synchronisation avec le serveur.

    //Création d'une instance de ma classe ReseauBDD et autre
    ReseauBDD maBaseReseau = new ReseauBDD(this);

    AnnonceBDD maBaseAnnonce = new AnnonceBDD(this);
    MessageBDD maBaseMessage = new MessageBDD(this);
    AdherentBDD maBaseAdherent = new AdherentBDD(this);

    //Création d'un reseau et autre
    Reseau reseau = new Reseau("Mon Premier Reseau", "Ceci est un test", "Aurelien", "Montpellier", 1);
    Reseau reseau2 = new Reseau("Mon Second Reseau", "Ceci est un test 2", "Aurelien", "Lille", 1);
    Reseau reseau3 = new Reseau("Mon Troisieme Reseau", "Ceci est un test 2", "Marine", "Montpellier", 1);

    Utilisateur utilisateur = new Utilisateur("aurelien", "123456", "05/04/96", 0, 181, 75);

    Annonce annonce = new Annonce("Une annonce", "Le contenu de mon annonce");

    Message message = new Message(0, "Ceci est un message 1", "Mon Premier Reseau", "aurelien");
    Message message1 = new Message(1, "Ceci est un message 2", "Mon Premier Reseau", "aurelien");
    Message message2 = new Message(2, "Ceci est un message 3", "Mon Premier Reseau", "aurelien");

//On ouvre les bases de données pour écrire dedans
        maBaseReseau.open();

        maBaseAnnonce.open();
        maBaseMessage.open();
        maBaseAdherent.open();

        //On insère le reseau et les autres objets que l'on vient de créer
        maBaseReseau.insertReseau(reseau);
        maBaseReseau.insertReseau(reseau2);
        maBaseReseau.insertReseau(reseau3);

        maBaseUtilisateur.insertUtilisateur(utilisateur);

        maBaseAnnonce.insertAnnonce(annonce);

        maBaseMessage.insertMessage(message);
        maBaseMessage.insertMessage(message1);
        maBaseMessage.insertMessage(message2);

        maBaseAdherent.insertAdherent(new Adherent("Aurelien", "Mon Troisieme Reseau"));

        //Pour vérifier que l'on a bien créé notre reseau dans la BDD
        //on extrait le reseau de la BDD grâce au titre du reseau que l'on a créé précédemment
        /*Reseau unReseauDeLaBase = maBaseReseau.getReseauWithPseudoAdmin(reseau.getPseudoAdmin());
        Utilisateur unUtilisateurDeLaBase = maBaseUtilisateur.getUtilisateurWithPseudo("aurelien");
        Annonce uneAnnonceDeLaBase = maBaseAnnonce.getAnnonceWithTitre("Une annonce");
        Message unMessageDeLaBase = maBaseMessage.getMessageWithId(0);
        //Si un reseau est retourné (donc si le reseau à bien été ajouté à la BDD)
        if(unReseauDeLaBase != null){
            //On affiche les infos du reseau dans un Toast
            Toast.makeText(this, unReseauDeLaBase.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, unUtilisateurDeLaBase.toString(), Toast.LENGTH_LONG).show();
            On modifie le titre du reseau
            unReseauDeLaBase.setDescription("J'ai modifié la description du reseau");
            Puis on met à jour la BDD
            maBaseReseau.updateReseau(unReseauDeLaBase.getSujet(), unReseauDeLaBase);
            Reseau DeuxReseauDeLaBase = maBaseReseau.getReseauWithPseudoAdmin(reseau.getPseudoAdmin());
            maBaseReseau.close();
        maBaseUtilisateur.close();
        maBaseAnnonce.close();
        maBaseMessage.close();
        maBaseAdherent.close();



            /*TextView texte1 = findViewById(R.id.texte1);
            texte1.setText(DeuxReseauDeLaBase.toString());

            TextView texte2 = findViewById(R.id.texte2);
            texte2.setText(unUtilisateurDeLaBase.toString());

            TextView texte3 = findViewById(R.id.texte3);
            texte3.setText(uneAnnonceDeLaBase.toString());

            TextView texte4 = findViewById(R.id.texte4);
            texte4.setText(unMessageDeLaBase.toString());

            TextView texte5 = findViewById(R.id.texte5);
            texte5.setText(uneAnnonceDeLaBase.toString());*/
//}