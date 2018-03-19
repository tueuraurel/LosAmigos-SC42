package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/* Premiere page de l'application, on y presente les 4 boutons principaux.
C'est ici également que l'on récupere les informations de l'utilisateur qui serront ensuite
passe d activite en activite (le pseudo et la ville)
 */
public class ActivitePrincipale extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // a enlever après le dev
        this.deleteDatabase("SmartCity.db");

        setContentView(R.layout.activite_principale);

        UtilisateurBDD maBaseUtilisateur = new UtilisateurBDD(this);
        maBaseUtilisateur.open();
        /* Ici il faudra une methode pour recuperer le login et le lieu de la personne automatiquement */
        final String pseudoUser = "Aurelien";
        final String lieuUser = "Montpellier";

        /* ici une liste d'exemple pour avoir une base pré-rempli pour les test sans
        synchronisation avec le serveur.
         */

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
           */ maBaseReseau.close();
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
                intent.putExtra("pseudoUser",pseudoUser);
                intent.putExtra("lieuUser",lieuUser);
                startActivity(intent);
            }
        });
    }
}



