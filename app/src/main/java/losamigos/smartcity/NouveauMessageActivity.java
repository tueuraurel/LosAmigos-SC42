package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.HashMap;

public class NouveauMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nouveau_message_layout);
        final EditText nouveauMessage = findViewById(R.id.EditTextNouveauMessage);
        Button valider = findViewById(R.id.BoutonValider);

        final Intent intent = getIntent();

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> parametres = new HashMap<String, String>();
                parametres.put("contenu", nouveauMessage.getText().toString());
                parametres.put("pseudoAuteur", intent.getStringExtra("pseudoUser"));
                parametres.put("sujetReseau",intent.getStringExtra("sujetReseau"));
                new exportMessageServeur().execute(parametres);
                MessageBDD maBaseMessage = new MessageBDD(NouveauMessageActivity.this);
                maBaseMessage.open();
                maBaseMessage.insertMessage(new Message(nouveauMessage.getText().toString(),intent.getStringExtra("sujetReseau"),"Aurelien"));
                maBaseMessage.close();
                finish();
            }
        });

    }
}