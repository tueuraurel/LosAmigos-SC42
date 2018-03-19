package losamigos.smartcity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class AnnonceBDD {
    

        private static final int VERSION_BDD = 1;
        private static final String NOM_BDD = "SmartCity.db";

        private static final String TABLE_ANNONCE = "Annonce";
        private static final String COL_TITRE = "titre";
        private static final int NUM_COL_TITRE = 0;
        private static final String COL_CONTENU = "contenu";
        private static final int NUM_COL_CONTENU = 1;


        private SQLiteDatabase bdd;

        private MaBaseSQLite maBaseSQLite;

        public AnnonceBDD(Context context){
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

        public long insertAnnonce(Annonce annonce){
            //Création d'un ContentValues (fonctionne comme une HashMap)
            ContentValues values = new ContentValues();
            //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
            values.put(COL_TITRE, annonce.getTitre());
            values.put(COL_CONTENU, annonce.getContenu());
            //on insère l'objet dans la BDD via le ContentValues
            return bdd.insert(TABLE_ANNONCE, null, values);
        }

        public int updateAnnonce(String titre, Annonce annonce){
            //La mise à jour d'un annonce dans la BDD fonctionne plus ou moins comme une insertion
            //il faut simplement préciser quel annonce on doit mettre à jour grâce à l'ID
            ContentValues values = new ContentValues();
            values.put(COL_CONTENU, annonce.getContenu());
            return bdd.update(TABLE_ANNONCE, values, COL_TITRE + " =\"" +titre+"\"", null);
        }

        public int supprimerAnnonceAvecTitre(String titre){
            //Suppression d'un annonce de la BDD grâce à l'ID
            return bdd.delete(TABLE_ANNONCE, COL_TITRE + " = " +titre, null);
        }

        public Annonce getAnnonceWithTitre(String titre)  {
            //Récupère dans un Cursor les valeurs correspondant à un annonce contenu dans la BDD (ici on sélectionne le annonce grâce à son titre)
            Cursor c = bdd.query(TABLE_ANNONCE, new String[] {COL_TITRE, COL_CONTENU}, COL_TITRE + " LIKE \"" + titre +"\"", null, null, null, null);
            return cursorToAnnonce(c);
        }

        //Cette méthode permet de convertir un cursor en un annonce
        private Annonce cursorToAnnonce(Cursor c) {
            //si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0)
                return null;

            //Sinon on se place sur le premier élément
            c.moveToFirst();
            //On créé un annonce
            Annonce annonce = new Annonce();
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            annonce.setTitre(c.getString(NUM_COL_TITRE));
            annonce.setContenu(c.getString(NUM_COL_CONTENU));

            //On ferme le cursor
            c.close();

            //On retourne le annonce
            return annonce;
        }
    }

