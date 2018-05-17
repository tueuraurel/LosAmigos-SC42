package losamigos.smartcity;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class ReseauAdapter extends ArrayAdapter<Reseau> {

    private List<Reseau> reseauList;
    private Context context;

    public ReseauAdapter(List<Reseau> reseauList, Context context) {
        super(context, R.layout.single_listview_item, reseauList);
        this.reseauList = reseauList;
        this.context = context;
    }

    private static class ReseauHolder {
        public TextView sujet;
        public TextView description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("getView",String.valueOf(position));
        View v = convertView;

        ReseauHolder holder = new ReseauHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_reseau_item, null);

            holder.sujet = (TextView) v.findViewById(R.id.sujet);
            holder.description = (TextView) v.findViewById(R.id.description);
            v.setTag(holder);
        } else {
            holder = (ReseauHolder) v.getTag();
        }


        Reseau p = reseauList.get(position);
        Log.d("ReseauAdapterList",p.toString());
        Log.d("ReseauAdapterSujet",p.getSujet());
        Log.d("ReseauAdapterDesc",p.getDescription());
        holder.sujet.setText(p.getSujet());
        holder.description.setText(p.getDescription());

        return v;
    }
}

