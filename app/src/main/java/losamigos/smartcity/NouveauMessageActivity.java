package losamigos.smartcity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



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
                MessageBDD maBaseMessage = new MessageBDD(NouveauMessageActivity.this);
                maBaseMessage.open();
                /*maBaseMessage.insertMessage(new Message(nouveauMessage.getText()),
                        intent.getStringExtra(pseudoUser),intent.getStringExtra(sujetReseau));*/
                Log.d("NouveauMessageActivity",String.valueOf(nouveauMessage.getText()));
                Log.d("NouveauMessageActivity",intent.getStringExtra("sujetReseau"));
                maBaseMessage.insertMessage(new Message(nouveauMessage.getText().toString(),intent.getStringExtra("sujetReseau"),"Aurelien"));
                maBaseMessage.close();
                finish();
                /*Intent intent2 = new Intent(NouveauMessageActivity.this,ListeMessageReseau.class);
                intent2.putExtra("sujetReseau",intent.getStringExtra("sujetReseau"));
                startActivity(intent2);*/
            }
        });

    }
}