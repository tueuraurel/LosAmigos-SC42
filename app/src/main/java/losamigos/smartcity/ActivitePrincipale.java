package losamigos.smartcity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
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

/* Premiere page de l'application, on y presente les 4 boutons principaux.
C'est ici également que l'on récupere les informations de l'utilisateur qui seront ensuite
passe d activite en activite (le pseudo et la ville)
 */
public class ActivitePrincipale extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Handler handler;
    String IDPLace;

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
        final String pseudoUser = intentPrecedent.getStringExtra("PSEUDO");
        final String lieuUser = intentPrecedent.getStringExtra("VILLE");

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

      public boolean onOptionsItemSelected(MenuItem item) {
          switch (item.getItemId()) {
              case R.id.menu_setting:
                  Intent intent = getIntent();
                  Intent settings=new Intent(ActivitePrincipale.this,PreferencesActivity.class);
                  final String pseudo = intent.getStringExtra("PSEUDO");
                  settings.putExtra("LATITUDE", intent.getStringExtra("LATITUDE"));
                  settings.putExtra("LONGITUDE", intent.getStringExtra("LONGITUDE"));
                  settings.putExtra("VILLE", intent.getStringExtra("VILLE"));
                  settings.putExtra("PSEUDO",pseudo);
                  startActivity(settings);
                  break;
              case R.id.deconnexion:
                  Intent pageConnexion=new Intent(ActivitePrincipale.this,MainActivity.class);
                  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ActivitePrincipale.this);
                  SharedPreferences.Editor editor = preferences.edit();
                  editor.remove("pseudoUser");
                  editor.remove("mdp");
                  editor.commit();
                  startActivity(pageConnexion);
                  break;
              default:
                  return super.onOptionsItemSelected(item);
          }
          return false;
        }

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
                final JSONObject json = RemoteFetchIDPlace.getJSON(latitude,longitude);
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
                            mGoogleApiClient = new GoogleApiClient
                                    .Builder(ActivitePrincipale.this)
                                    .addApi(Places.GEO_DATA_API)
                                    .addApi(Places.PLACE_DETECTION_API)
                                    .enableAutoManage(ActivitePrincipale.this, ActivitePrincipale.this)
                                    .addOnConnectionFailedListener(ActivitePrincipale.this)
                                    .build();
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
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;
            PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).await();
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
                }
                // Pour empêcher les fuites de mémoire
                photoMetadataBuffer.release();
            }
            else{
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