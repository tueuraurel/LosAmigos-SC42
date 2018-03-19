package losamigos.smartcity;

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
                Intent intent = new Intent(ActualiteActivity.this, HorlogeActivity.class);
                startActivity(intent);
            }
        });

        final Button button2 = findViewById(R.id.buttonMeteo);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActualiteActivity.this, MeteoActivity.class);
                startActivity(intent);
            }
        });

        final Button button3 = findViewById(R.id.buttonActualite);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ActualiteActivity.this, ActualitesActivity.class);
                startActivity(intent);
            }
        });
    }


}
