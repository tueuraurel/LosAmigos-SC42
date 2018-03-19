package losamigos.smartcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InscriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        Button btn = (Button) findViewById(R.id.inscrbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent  = new Intent(InscriptionActivity.this, ChoixThemeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void buttonclicked(View view) {
    }
}
