package losamigos.smartcity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;



public class AdherentBDD {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "SmartCity.db";
    private static final String TABLE_ADHERENT = "Adherent";
    private static final String COL_PSEUDO = "pseudo";
    private static final int NUM_COL_PSEUDO = 0;
    private static final String COL_RESEAU = "sujetReseau";
    private static final int NUM_COL_RESEAU = 1;
    private SQLiteDatabase bdd;
    private MaBaseSQLite maBaseSQLite;

    public AdherentBDD(Context context){
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

    public long insertAdherent(Adherent adherent){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_PSEUDO, adherent.getPseudo());
        values.put(COL_RESEAU, adherent.getSujetReseau());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_ADHERENT, null, values);
    }

    public int updateAdherent(String sujet, Adherent adherent){
        //La mise à jour d'un adherent dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel adherent on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_RESEAU, adherent.getSujetReseau());
        return bdd.update(TABLE_ADHERENT, values, COL_PSEUDO + " =\"" +sujet+"\"", null);
    }

    public int supprimerAdherentAvecSujetReseau(String sujetReseau){
        //Suppression d'un adherent de la BDD grâce à l'ID
        return bdd.delete(TABLE_ADHERENT, COL_RESEAU + " = " +sujetReseau, null);
    }

    public ArrayList<Adherent> getAllAdherentWithPseudo(String pseudo)  {
        //Récupère dans un Cursor les valeurs correspondant à un adherent contenu dans la BDD (ici on sélectionne le adherent grâce à son titre)
        Cursor c = bdd.query(TABLE_ADHERENT, new String[] {COL_PSEUDO, COL_RESEAU}, COL_PSEUDO + " LIKE \"" + pseudo +"\"", null, null, null, null);
        return cursorToArray(c);
    }

    public ArrayList<Adherent> getAllAdherentWithSujetReseau(String sujetReseau)  {
        //Récupère dans un Cursor les valeurs correspondant à tout les adherentx contenu dans la BDD (ici on sélectionne les adherentx grâce à l'utilisateur)
        Cursor c = bdd.query(TABLE_ADHERENT, new String[] {COL_PSEUDO, COL_RESEAU}, COL_RESEAU + " LIKE \"" + sujetReseau +"\"", null, null, null, null);
        return cursorToArray(c);
    }


    //Cette méthode permet de convertir un cursor de adherent en ArrayList de adherent;
    private ArrayList<Adherent> cursorToArray(Cursor c) {
        ArrayList<Adherent> resultat = new ArrayList<>();
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        //Sinon on se place sur le premier élément
        c.moveToFirst();
        while (!c.isAfterLast()){
            //On créé un adherent
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            c.moveToNext();
        }

        //On ferme le cursor
        c.close();

        //On retourne le tableau
        return resultat;
    }

    //Cette méthode permet de convertir un cursor en un adherent
    private Adherent cursorToAdherent(Cursor c) {
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un adherent
        Adherent adherent = new Adherent();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        adherent.setPseudo(c.getString(NUM_COL_PSEUDO));
        adherent.setSujetReseau(c.getString(NUM_COL_RESEAU));


        //On ferme le cursor
        c.close();

        //On retourne le adherent
        return adherent;
    }
}
