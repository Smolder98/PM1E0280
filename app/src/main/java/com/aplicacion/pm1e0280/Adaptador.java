package com.aplicacion.pm1e0280;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import modelos.Contacto;


public class Adaptador extends BaseAdapter {

    private Context context;
    private ArrayList<Contacto> listItem;
    private ArrayList<Contacto> filterlist;
    private CustomFilter filter;



    public Adaptador(Context context, ArrayList<Contacto> listItem) {
        this.context = context;
        this.listItem = listItem;

        this.filterlist = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Contacto item = (Contacto) getItem(i);

        view = LayoutInflater.from(context).inflate(R.layout.item, null);


        TextView titulo =(TextView) view.findViewById(R.id.itemTitulo);
        TextView descripcion =(TextView) view.findViewById(R.id.itemNota);
        TextView id =(TextView) view.findViewById(R.id.itemObjetId);

        titulo.setText(item.toString());
        descripcion.setText(item.getNota());
        id.setText(item.getId()+"");



        return view;
    }

    //Filter
    /**********************************************************************************/

     class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();

            if(charSequence != null && charSequence.length()>0){

                charSequence = charSequence.toString().toUpperCase();

                ArrayList<Contacto> filters = new ArrayList<Contacto>();

                for(int i = 0;i < filterlist.size(); i++){

                    if(filterlist.get(i).getNombre().toUpperCase().contains(charSequence)){

                        filters.add(filterlist.get(i));
                    }
                }

                filterResults.count = filters.size();
                filterResults.values = filters;

            }else {

                filterResults.count = filterlist.size();
                filterResults.values = filterlist;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            listItem = (ArrayList<Contacto>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public Filter getFilter(){

         if(filter == null){
             filter = new CustomFilter();
         }

        return filter;
    }

    public ArrayList<Contacto> getFilterlist(){
         return filterlist;
    }
}
