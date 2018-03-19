package losamigos.smartcity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ThemeAdapter extends ArrayAdapter<Theme> {

    public ThemeAdapter(Context context, List<Theme> articles) {
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_theme,parent, false);
        }

        ThemeViewHolder viewHolder = (ThemeViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ThemeViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nom);
            //puis on sauvegarde le mini-controlleur dans la vue
            convertView.setTag(viewHolder);
        }

        Theme theme = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        if(!theme.getNom().equals("null")) {
            viewHolder.nom.setText(theme.getNom());
        }
        return convertView;
    }

    private class ThemeViewHolder{
        public TextView nom;
    }
}
