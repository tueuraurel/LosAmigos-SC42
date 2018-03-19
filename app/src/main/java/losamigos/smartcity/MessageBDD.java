package losamigos.smartcity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;



public class MessageBDD {

        private static final int VERSION_BDD = 1;
        private static final String NOM_BDD = "SmartCity.db";

        private static final String TABLE_MESSAGE = "Message";
        private static final String COL_ID = "id";
        private static final int NUM_COL_ID = 0;
        private static final String COL_CONTENU = "contenu";
        private static final int NUM_COL_CONTENU = 1;
        private static final String COL_SUJETRESEAU= "sujetReseau";
        private static final int NUM_COL_SUJETRESEAU = 2;
        private static final String COL_PSEUDOAUTEUR = "pseudoAuteur";
        private static final int NUM_COL_PSEUDOAUTEUR = 3;
 

        private SQLiteDatabase bdd;

        private MaBaseSQLite maBaseSQLite;

        public MessageBDD(Context context){
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

        public long insertMessage(Message message){
            //Création d'un ContentValues (fonctionne comme une HashMap)
            ContentValues values = new ContentValues();
            //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
            values.put(COL_ID, (String)null);
            values.put(COL_CONTENU, message.getContenu());
            values.put(COL_SUJETRESEAU, message.getSujetReseau());
            values.put(COL_PSEUDOAUTEUR, message.getPseudoAuteur());
            //on insère l'objet dans la BDD via le ContentValues
            return bdd.insert(TABLE_MESSAGE, null, values);
        }

        public int updateMessage(int id, Message message){
            //La mise à jour d'un message dans la BDD fonctionne plus ou moins comme une insertion
            //il faut simplement préciser quel message on doit mettre à jour grâce à l'ID
            ContentValues values = new ContentValues();
            values.put(COL_CONTENU, message.getContenu());
            values.put(COL_SUJETRESEAU, message.getSujetReseau());
            values.put(COL_PSEUDOAUTEUR, message.getPseudoAuteur());

            return bdd.update(TABLE_MESSAGE, values, COL_ID + " =\"" +id+"\"", null);
        }

        public int supprimerMessageAvecId(int id){
            //Suppression d'un message de la BDD grâce à l'ID
            return bdd.delete(TABLE_MESSAGE, COL_ID + " = " +id, null);
        }

        public Message getMessageWithId(int id)  {
            //Récupère dans un Cursor les valeurs correspondant à un message contenu dans la BDD (ici on sélectionne le message grâce à son titre)
            Cursor c = bdd.query(TABLE_MESSAGE, new String[] {COL_ID, COL_CONTENU, COL_SUJETRESEAU,COL_PSEUDOAUTEUR}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
            return cursorToMessage(c);
        }

    public ArrayList<Message> getAllMessageWithSujetReseau(String sujetReseau)  {
        //Récupère dans un Cursor les valeurs correspondant à un message contenu dans la BDD (ici on sélectionne le message grâce à son titre)
        Cursor c = bdd.query(TABLE_MESSAGE, new String[] {COL_ID, COL_CONTENU, COL_SUJETRESEAU,COL_PSEUDOAUTEUR}, COL_SUJETRESEAU + " LIKE \"" + sujetReseau +"\"", null, null, null, null);
        return cursorToArray(c);
    }


    //Cette méthode permet de convertir un cursor de message en ArrayList de message;
    private ArrayList<Message> cursorToArray(Cursor c) {
        ArrayList<Message> resultat = new ArrayList<>();
        Log.d("CursorToArray","La taille du curseur est : "+ c.getCount());
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        Log.d("CursorToArray","isAfterLast : "+ c.isAfterLast());
        while (!c.isAfterLast()){
            //On créé un livre
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            resultat.add(new Message(c.getInt(NUM_COL_ID),c.getString(NUM_COL_CONTENU),c.getString(NUM_COL_SUJETRESEAU),
                    c.getString(NUM_COL_PSEUDOAUTEUR)));

            c.moveToNext();
        }

        //On ferme le cursor
        c.close();

        //On retourne le tableau
        return resultat;
    }
        //Cette méthode permet de convertir un cursor en un message
        private Message cursorToMessage(Cursor c) {
            //si aucun élément n'a été retourné dans la requête, on renvoie null
            if (c.getCount() == 0)
                return null;

            //Sinon on se place sur le premier élément
            c.moveToFirst();
            //On créé un message
            Message message = new Message();
            //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
            message.setId(c.getInt(NUM_COL_ID));
            message.setContenu(c.getString(NUM_COL_CONTENU));
            message.setSujetReseau(c.getString(NUM_COL_SUJETRESEAU));
            message.setPseudoAuteur(c.getString(NUM_COL_PSEUDOAUTEUR));

            //On ferme le cursor
            c.close();

            //On retourne le message
            return message;
        }
    }

