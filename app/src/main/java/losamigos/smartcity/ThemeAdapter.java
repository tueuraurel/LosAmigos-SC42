package losamigos.smartcity;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

class Theme {
    String nom;
    int id;
    boolean selected = false;

    public Theme(String nom, int id) {
        this.nom = nom;
        this.id= id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

class ThemeAdapter extends ArrayAdapter<Theme>{

    private List<Theme>  themeList;
    private Context context;

    public ThemeAdapter(List<Theme> themeList, Context context) {
        super(context, R.layout.single_listview_item, themeList);
        this.themeList = themeList;
        this.context = context;
    }

    private static class ThemeHolder {
        public TextView themeName;
        public CheckBox chkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        ThemeHolder holder = new ThemeHolder();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_listview_item, null);
            holder.themeName = (TextView) v.findViewById(R.id.name);
            holder.chkBox = (CheckBox) v.findViewById(R.id.chk_box);
            holder.chkBox.setOnCheckedChangeListener((ChoixThemeActivity) context);
            v.setTag(holder);
        } else {
            holder = (ThemeHolder) v.getTag();
        }

        Theme p = themeList.get(position);
        holder.themeName.setText(p.getNom());
        holder.chkBox.setChecked(p.isSelected());
        holder.chkBox.setTag(p);

        return v;
    }
}