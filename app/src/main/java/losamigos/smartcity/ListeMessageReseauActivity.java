package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ListeMessageReseauActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_message_reseaux);
    }

    protected void onResume(){
        super.onResume();
        final Intent intent = getIntent();
        TextView test = findViewById(R.id.test);
        ListView listeViewMessage = findViewById(R.id.listeViewMessageReseau);

        if (intent!=null) {
            Button boutonNouveauMessage = findViewById(R.id.boutonNouveauMessage);

            boutonNouveauMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent2 = new Intent(ListeMessageReseauActivity.this,NouveauMessageActivity.class);
                    intent2.putExtra("sujetReseau",intent.getStringExtra("sujetReseau"));
                    startActivity(intent2);
                }
            });

            String sujetReseau = intent.getStringExtra("sujetReseau");
            test.setText(intent.getStringExtra("sujetReseau"));
            
            MessageBDD maBaseMessage = new MessageBDD(this);
            maBaseMessage.open();

            ArrayList<Message> MesMessage;
            MesMessage = maBaseMessage.getAllMessageWithSujetReseau(sujetReseau);

            if (MesMessage!=null) {
                Log.d("ApresGETALL", "La taille est :" + MesMessage.size());
                String[] NomMessage = new String[MesMessage.size()];

                for (int i = 0; i < MesMessage.size(); i++) {
                    NomMessage[i] = MesMessage.get(i).getContenu();
                }
                //android.R.layout.simple_list_item_1 est une vue disponible de base dans le SDK android,
                //Contenant une TextView avec comme identifiant "@android:id/text1"

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListeMessageReseauActivity.this,
                        android.R.layout.simple_list_item_1, NomMessage);
                listeViewMessage.setAdapter(adapter);
                //Log.d("Synchro BDD",maBaseMessage.getLatestMessageWithSujetReseau(sujetReseau).getContenu());
                maBaseMessage.close();
            }
            else{
                Toast.makeText(this, "Il n'y a pas de message sur ce reseau"
                        , Toast.LENGTH_LONG).show();
            }
        }
        else {
            test.setText("ERREUR");
        }

    }
}