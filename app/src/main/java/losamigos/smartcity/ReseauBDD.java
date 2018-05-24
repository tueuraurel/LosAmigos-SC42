package losamigos.smartcity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;


public class ReseauBDD {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "SmartCity.db";
    private static final String TABLE_RESEAU = "Reseau";
    private static final String COL_SUJET = "sujet";
    private static final int NUM_COL_SUJET = 0;
    private static final String COL_DESCRIPTION = "description";
    private static final int NUM_COL_DESCRIPTION = 1;
    private static final String COL_PSEUDOADMIN = "pseudoAdmin";
    private static final int NUM_COL_PSEUDOADMIN = 2;
    private static final String COL_LOCALISATION = "localisation";
    private static final int NUM_COL_LOCALISATION = 3;
    private static final String COL_VISIBILITE = "visibilite";
    private static final int NUM_COL_VISIBILITE = 4;
    private SQLiteDatabase bdd;
    private MaBaseSQLite maBaseSQLite;

    public ReseauBDD(Context context){
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

    public long insertReseau(Reseau reseau){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_SUJET, reseau.getSujet());
        values.put(COL_DESCRIPTION, reseau.getDescription());
        values.put(COL_PSEUDOADMIN, reseau.getPseudoAdmin());
        values.put(COL_LOCALISATION, reseau.getLocalisation());
        values.put(COL_VISIBILITE, reseau.getVisibilite());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_RESEAU, null, values);
    }

    public int updateReseau(String sujet, Reseau reseau){
        //La mise à jour d'un reseau dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel reseau on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, reseau.getDescription());
        values.put(COL_PSEUDOADMIN, reseau.getPseudoAdmin());
        values.put(COL_LOCALISATION, reseau.getLocalisation());
        values.put(COL_VISIBILITE, reseau.getVisibilite());
        return bdd.update(TABLE_RESEAU, values, COL_SUJET + " =\"" +sujet+"\"", null);
    }

    public int supprimerReseauAvecSujet(String sujet){
        //Suppression d'un reseau de la BDD grâce à l'ID
        return bdd.delete(TABLE_RESEAU, COL_SUJET + " = " +sujet, null);
    }

    public Reseau getReseauWithPseudoAdmin(String admin)  {
        //Récupère dans un Cursor les valeurs correspondant à un reseau contenu dans la BDD (ici on sélectionne le reseau grâce à son titre)
        Cursor c = bdd.query(TABLE_RESEAU, new String[] {COL_SUJET, COL_DESCRIPTION, COL_PSEUDOADMIN,COL_LOCALISATION,COL_VISIBILITE}, COL_PSEUDOADMIN + " LIKE \"" + admin +"\"", null, null, null, null);
        return cursorToReseau(c);
    }

    public ArrayList<Reseau> getAllReseauWithPseudoUser(String utilisateur)  {
        //Récupère dans un Cursor les valeurs correspondant à tout les reseaux contenu dans la BDD (ici on sélectionne les reseaux grâce à l'utilisateur)
        Cursor c = bdd.query(TABLE_RESEAU, new String[] {COL_SUJET, COL_DESCRIPTION, COL_PSEUDOADMIN,COL_LOCALISATION,COL_VISIBILITE}, COL_PSEUDOADMIN + " LIKE \"" + utilisateur +"\"", null, null, null, null);
        return cursorToArray(c);
    }

    public ArrayList<Reseau> getAllReseauAccessUser(String utilisateur)  {
        //Récupère dans un Cursor les valeurs correspondant à tout les reseaux contenu dans la BDD (ici on sélectionne les reseaux grâce à l'utilisateur)
        Cursor c = bdd.query(TABLE_RESEAU, new String[] {COL_SUJET, COL_DESCRIPTION, COL_PSEUDOADMIN,COL_LOCALISATION,COL_VISIBILITE}, COL_PSEUDOADMIN + " LIKE \"" + utilisateur +"\" or " + COL_SUJET + " in (SELECT sujetReseau from Adherent where pseudo =\""+utilisateur +"\")" , null, null, null, null);
        return cursorToArray(c);
    }

    public ArrayList<Reseau> getAllReseauWithLieuEtVisibilite(String localisation, int visibilite)  {
        //Récupère dans un Cursor les valeurs correspondant à tout les reseaux contenu dans la BDD (ici on sélectionne les reseaux grâce à l'utilisateur)
        Cursor c = bdd.query(TABLE_RESEAU, new String[] {COL_SUJET, COL_DESCRIPTION, COL_PSEUDOADMIN,COL_LOCALISATION,COL_VISIBILITE}, COL_LOCALISATION + " LIKE \"" + localisation +"\" and " +COL_VISIBILITE + " LIKE \"" + visibilite +"\"", null, null, null, null);
        return cursorToArray(c);
    }

    //Cette méthode permet de convertir un cursor de reseaux en ArrayList de reseau;
    private ArrayList<Reseau> cursorToArray(Cursor c) {
        ArrayList<Reseau> resultat = new ArrayList<>();
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        while (!c.isAfterLast()){
            //On créé un reseau
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            resultat.add(new Reseau(c.getString(NUM_COL_SUJET),c.getString(NUM_COL_DESCRIPTION),c.getString(NUM_COL_PSEUDOADMIN),
                    c.getString(NUM_COL_LOCALISATION),c.getInt(NUM_COL_VISIBILITE)));
            c.moveToNext();
        }

        //On ferme le cursor
        c.close();

        //On retourne le tableau
        return resultat;
    }

    //Cette méthode permet de convertir un cursor en un reseau
    private Reseau cursorToReseau(Cursor c) {
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un reseau
        Reseau reseau = new Reseau();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        reseau.setSujet(c.getString(NUM_COL_SUJET));
        reseau.setDescription(c.getString(NUM_COL_DESCRIPTION));
        reseau.setPseudoAdmin(c.getString(NUM_COL_PSEUDOADMIN));
        reseau.setLocalisation(c.getString(NUM_COL_LOCALISATION));
        reseau.setVisibilite(c.getInt(NUM_COL_VISIBILITE));

        //On ferme le cursor
        c.close();

        //On retourne le reseau
        return reseau;
    }
}
