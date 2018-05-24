package losamigos.smartcity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class UtilisateurBDD {

        private static final int VERSION_BDD = 1;
        private static final String NOM_BDD = "SmartCity.db";
        private static final String TABLE_UTILISATEUR = "Utilisateur";
        private static final String COL_PSEUDO = "pseudo";
        private static final int NUM_COL_PSEUDO = 0;
        private static final String COL_MDP = "MDP";
        private static final int NUM_COL_MDP = 1;
        private static final String COL_DATENAISSANCE= "dateNaissance";
        private static final int NUM_COL_DATENAISSANCE = 2;
        private static final String COL_SEXE = "sexe";
        private static final int NUM_COL_SEXE = 3;
        private static final String COL_TAILLE = "taille";
        private static final int NUM_COL_TAILLE = 4;
        private static final String COL_POIDS= "poids";
        private static final int NUM_COL_POIDS = 5;
        private SQLiteDatabase bdd;
        private MaBaseSQLite maBaseSQLite;

        public UtilisateurBDD(Context context){
            //On crée la BDD et ses tables
            maBaseSQLite = new MaBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
        }

        public void open(){
            //on ouvre la BDD en écriture
            bdd = maBaseSQLite.getWritableDatabase();
        }

        public void close(){
            //on ferme l'accès à la BDD
            bdd.close();
        }

        public SQLiteDatabase getBDD(){
            return bdd;
        }

        public long insertUtilisateur(Utilisateur utilisateur){
            //Création d'un ContentValues (fonctionne comme une HashMap)
            ContentValues values = new ContentValues();
            //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
            values.put(COL_PSEUDO, utilisateur.getPseudo());
            values.put(COL_MDP, utilisateur.getMDP());
            values.put(COL_DATENAISSANCE, utilisateur.getDateNaissance());
            values.put(COL_SEXE, utilisateur.getSexe());
            values.put(COL_TAILLE, utilisateur.getTaille());
            values.put(COL_POIDS, utilisateur.getPoids());
            //on insère l'objet dans la BDD via le ContentValues
            return bdd.insert(TABLE_UTILISATEUR, null, values);
        }

        public int updateUtilisateur(String sujet, Utilisateur utilisateur){
            //La mise à jour d'un reseau dans la BDD fonctionne plus ou moins comme une insertion
            //il faut simplement préciser quel reseau on doit mettre à jour grâce à l'ID
            ContentValues values = new ContentValues();
            values.put(COL_MDP, utilisateur.getMDP());
            values.put(COL_DATENAISSANCE, utilisateur.getDateNaissance());
            values.put(COL_SEXE, utilisateur.getSexe());
            values.put(COL_TAILLE, utilisateur.getTaille());
            values.put(COL_POIDS, utilisateur.getPoids());
            return bdd.update(TABLE_UTILISATEUR, values, COL_PSEUDO + " =\"" +sujet+"\"", null);
        }

        public int supprimerUtilisateurAvecPseudo(String pseudo){
            //Suppression d'un utilisateur de la BDD grâce à l'ID
            return bdd.delete(TABLE_UTILISATEUR, COL_PSEUDO + " = " +pseudo, null);
        }

        public Utilisateur getUtilisateurWithPseudo(String pseudo)  {
            //Récupère dans un Cursor les valeurs correspondant à un reseau contenu dans la BDD (ici on sélectionne le reseau grâce à son titre)
            Cursor c = bdd.query(TABLE_UTILISATEUR, new String[] {COL_PSEUDO, COL_MDP, COL_DATENAISSANCE,COL_SEXE,COL_TAILLE,COL_POIDS}, COL_PSEUDO + " LIKE \"" + pseudo +"\"", null, null, null, null);
            return cursorToUtilisateur(c);
        }

        //Cette méthode permet de convertir un cursor en un reseau
        private Utilisateur cursorToUtilisateur(Cursor c) {
            //si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0)
                return null;

            //Sinon on se place sur le premier élément
            c.moveToFirst();
            //On créé un reseau
            Utilisateur utilisateur = new Utilisateur();
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            utilisateur.setPseudo(c.getString(NUM_COL_PSEUDO));
            utilisateur.setMDP(c.getString(NUM_COL_MDP));
            utilisateur.setDateNaissance(c.getString(NUM_COL_DATENAISSANCE));
            utilisateur.setSexe(c.getInt(NUM_COL_SEXE));
            utilisateur.setTaille(c.getInt(NUM_COL_TAILLE));
            utilisateur.setPoids(c.getInt(NUM_COL_POIDS));

            //On ferme le cursor
            c.close();

            //On retourne le reseau
            return utilisateur;
        }
    }
