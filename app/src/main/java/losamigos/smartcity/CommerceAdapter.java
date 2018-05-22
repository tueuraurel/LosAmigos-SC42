package losamigos.smartcity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CommerceAdapter extends ArrayAdapter<Commerce> {

    private List<Commerce> commerceList;
    private Context context;

    public CommerceAdapter(List<Commerce> commerceList, Context context) {
        super(context, R.layout.single_textview_item, commerceList);
        this.commerceList = commerceList;
        this.context = context;
    }

    private static class CommerceHolder {
        public TextView nom;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        CommerceHolder holder = new CommerceHolder();

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_textview_item, null);

            holder.nom = (TextView) v.findViewById(R.id.textView);
            v.setTag(holder);
        } else {
            holder = (CommerceHolder) v.getTag();
        }

        Commerce p = commerceList.get(position);
        holder.nom.setText(p.getNom());

        return v;
    }
}