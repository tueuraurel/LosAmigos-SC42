package losamigos.smartcity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

class Villes {
    String nom;
    String latitude;
    String longitude;
    boolean selected = false;

    public Villes(String nom, String latitude, String longitude) {
        this.nom = nom;
        this.latitude= latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

class VilleAdapter extends ArrayAdapter<Villes> {

    private List<Villes> villesList;
    private Context context;

    public VilleAdapter(List<Villes> villeList, Context context) {
        super(context, R.layout.single_listview_item_ville, villeList);
        this.villesList = villeList;
        this.context = context;
    }

    private static class VilleHolder {
        public TextView villeName;
        public CheckBox chkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        VilleHolder holder = new VilleHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_listview_item, null);

            holder.villeName = (TextView) v.findViewById(R.id.name);
            holder.chkBox = (CheckBox) v.findViewById(R.id.chk_box);
            holder.chkBox.setOnCheckedChangeListener((ChoixVilleActivity) context);


        } else {
            holder = (VilleHolder) v.getTag();
        }

        Villes p = villesList.get(position);
        holder.villeName.setText(p.getNom());
        holder.chkBox.setChecked(p.isSelected());
        holder.chkBox.setTag(p);
        return v;
    }
}

//bug à l'ouverture du clavier