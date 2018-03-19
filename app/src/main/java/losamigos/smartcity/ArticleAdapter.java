package losamigos.smartcity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    //tweets est la liste des models à afficher
    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_article,parent, false);
        }

        ArticleViewHolder viewHolder = (ArticleViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ArticleViewHolder();
            viewHolder.titre = (TextView) convertView.findViewById(R.id.titre);
            viewHolder.auteur = (TextView) convertView.findViewById(R.id.auteur);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            //puis on sauvegarde le mini-controlleur dans la vue
            convertView.setTag(viewHolder);
        }

        Article article = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        if(!article.getTitre().equals("null")) {
            viewHolder.titre.setText(article.getTitre());
        }
        if(!article.getAuteur().equals("null")) {
            viewHolder.auteur.setText(article.getAuteur());
        }
        if(!article.getDescription().equals("null")) {
            viewHolder.description.setText(article.getDescription());
        }
        return convertView;
    }

    private class ArticleViewHolder{
        public TextView titre;
        public TextView auteur;
        public TextView description;
    }
}