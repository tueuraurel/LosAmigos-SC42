package losamigos.smartcity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChoixPartieCommerceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_partie_commerce);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menucommerce, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.retourAccueil:
                Intent intentIn = getIntent();
                Intent intent = new Intent(ChoixPartieCommerceActivity.this, ActivitePrincipale.class );
                intent.putExtra("PSEUDO",intentIn.getStringExtra("pseudoUser"));
                intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // On recupere l'intent precedent pour avoir les donnees qu'il transporte
        final Intent intentIn = getIntent();

        Button listeFavoris = (Button) findViewById(R.id.listeFavoriCommerce);
        listeFavoris.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentIn = getIntent();
                Intent intent = new Intent(ChoixPartieCommerceActivity.this, ListeFavorisCommerceActivity.class );
                intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });

        Button rechercheDirecte = (Button) findViewById(R.id.rechercheDirecte);
        rechercheDirecte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentIn = getIntent();
                Intent intent = new Intent(ChoixPartieCommerceActivity.this, RechercheDirecteCommercesActivity.class );
                intent.putExtra("pseudoUser",intentIn.getStringExtra("pseudoUser"));
                intent.putExtra("LATITUDE", intentIn.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intentIn.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intentIn.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });

        Button boutonRecherche = findViewById(R.id.rechercheCommerces);
        boutonRecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoixPartieCommerceActivity.this,RechercheCommerceActivity.class);
                Intent intent2 = getIntent();
                intent.putExtra("pseudoUser", intent2.getStringExtra("pseudoUser"));
                //intent.putExtra("lieuUser",lieuUser);
                intent.putExtra("LATITUDE", intent2.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });

        Button boutonOffre = findViewById(R.id.annoncesCommerces);
        boutonOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChoixPartieCommerceActivity.this,OffreCommerceActivity.class);
                Intent intent2 = getIntent();
                intent.putExtra("pseudoUser", intent2.getStringExtra("pseudoUser"));
                //intent.putExtra("lieuUser",lieuUser);
                intent.putExtra("LATITUDE", intent2.getStringExtra("LATITUDE"));
                intent.putExtra("LONGITUDE", intent2.getStringExtra("LONGITUDE"));
                intent.putExtra("VILLE", intent2.getStringExtra("VILLE"));
                startActivity(intent);
            }
        });
    }
}
