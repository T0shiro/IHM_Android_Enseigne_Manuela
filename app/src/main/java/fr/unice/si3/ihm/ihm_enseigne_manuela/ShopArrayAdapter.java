package fr.unice.si3.ihm.ihm_enseigne_manuela;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.unice.si3.ihm.ihm_enseigne_manuela.model.Shop;

/**
 * Created by user on 07/05/2017.
 */

public class ShopArrayAdapter extends ArrayAdapter {
    public ShopArrayAdapter(@NonNull Context context, @LayoutRes int resource, List<Shop> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shop_layout, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(((Shop) getItem(position)).getName());
        TextView address = (TextView) convertView.findViewById(R.id.address);
        Shop currentShop = ((Shop) getItem(position));
        address.setText(currentShop.getFullAddress());
        TextView phoneNumber = (TextView) convertView.findViewById(R.id.phoneNumber);
        phoneNumber.setText(((Shop) getItem(position)).getPhoneNumber());
        return convertView;
    }
}
