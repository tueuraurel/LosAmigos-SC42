package losamigos.smartcity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MaBaseSQLite extends SQLiteOpenHelper {
//ceci est un commentaire
    private static final String TABLE_RESEAU = "Reseau";

    private static final String TABLE_UTILISATEUR = "Utilisateur";

    private static final String TABLE_ANNONCE = "Annonce";

    private static final String TABLE_MESSAGE = "Message";

    private static final String TABLE_ADHERENT = "Adherent";


    private static final String CREATE_TABLE_RESEAU = "CREATE TABLE Reseau(sujet TEXT " +
            "PRIMARY KEY, description TEXT,pseudoAdmin TEXT,localisation TEXT, visibilite INTEGER);";

    private static final String CREATE_TABLE_UTILISATEUR = "CREATE TABLE Utilisateur(pseudo TEXT " +
            "PRIMARY KEY, MDP TEXT,dateNaissance TEXT,sexe INTEGER, taille REAL" +
            ",poids REAL);";

    private static final String CREATE_TABLE_ANNONCE = "CREATE TABLE Annonce(titre TEXT " +
            "PRIMARY KEY, contenu TEXT);";

    private static final String CREATE_TABLE_MESSAGE = "CREATE TABLE Message(id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "contenu TEXT, sujetReseau TEXT,pseudoAuteur TEXT);";

    private static final String CREATE_TABLE_ADHERENT = "CREATE TABLE Adherent(pseudo TEXT," +
            "sujetReseau TEXT, PRIMARY KEY (pseudo,sujetReseau));";


    public MaBaseSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on crée la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_TABLE_RESEAU);
        db.execSQL(CREATE_TABLE_UTILISATEUR);
        db.execSQL(CREATE_TABLE_ANNONCE);
        db.execSQL(CREATE_TABLE_MESSAGE);
        db.execSQL(CREATE_TABLE_ADHERENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut faire ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0

        db.execSQL("DROP TABLE " + TABLE_RESEAU + ";");
        db.execSQL("DROP TABLE " + TABLE_UTILISATEUR + ";");
        db.execSQL("DROP TABLE " + TABLE_ANNONCE + ";");
        db.execSQL("DROP TABLE " + TABLE_MESSAGE + ";");
        db.execSQL("DROP TABLE " + TABLE_ADHERENT + ";");
        onCreate(db);
    }

}
