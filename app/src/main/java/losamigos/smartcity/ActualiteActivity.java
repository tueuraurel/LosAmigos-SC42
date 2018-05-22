package losamigos.smartcity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ActualiteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualite);


        final Button button = findViewById(R.id.buttonAlarme);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Intent intent = new Intent(ActualiteActivity.this, HorlogeActivity.class);
                startActivity(intent);*/
                //va chercher l'alarme du téléphone
                Intent i = getPackageManager().getLaunchIntentForPackage("com.android.deskclock");
                if (i != null)
                {
                    startActivity(i);
                }
            }
        });

        final Button button2 = findViewById(R.id.buttonMeteo);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = getIntent();
                Intent intent = new Intent(ActualiteActivity.this, MeteoActivity.class);
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });

        final Button button3 = findViewById(R.id.buttonActualite);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActualiteActivity.this, ActualitesActivity.class);
                Intent intent2 = getIntent();
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });

        final Button button4 = findViewById(R.id.buttonEtatTrafic);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent2 = getIntent();
                Intent intent = new Intent(ActualiteActivity.this, MapsActivity.class);
                intent.putExtra("LATITUDE",intent2.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                startActivity(intent) ;
            }
        });

        //on envoie sur l'agenda present sur le téléphone
        final Button button5 = findViewById(R.id.buttonAgenda);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.calendar");
                if (i != null)
                {
                    startActivity(i);
                }
            }
        });
    }


}
