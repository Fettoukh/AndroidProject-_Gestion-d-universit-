package com.example.tp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecordListAdapter extends BaseAdapter implements Filterable {


    private Context context ;
    private int layout;
    private ArrayList<Model> recordList;
    ValueFilter valueFilter;
    List<Model> mStringFilterList;

    public RecordListAdapter(Context context, int layout, ArrayList<Model> recordList) {
        this.context = context;
        this.layout = layout;
        this.recordList = recordList;
        mStringFilterList = recordList;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Model> filterList = new ArrayList<Model>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase()).startsWith(constraint.toString().toUpperCase())) {

                        Model bean = new Model(mStringFilterList.get(i)
                                .getId(), mStringFilterList.get(i)
                                .getName(),mStringFilterList.get(i)
                                .getAdresse(),mStringFilterList.get(i)
                                .getPhone(),mStringFilterList.get(i)
                                .getFormation(),mStringFilterList.get(i)
                                .getSpecialite(),mStringFilterList.get(i)
                                .getImage());


                        filterList.add(bean);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recordList = (ArrayList<Model>) results.values;
            notifyDataSetChanged();
        }
    }



        @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }



    private class ViewHolder{
        ImageView imageView;
        TextView txtName , txtAdresse , txtPhone , txtFormation , txtSpecialite;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();
        if(row==null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout , null );
            holder.txtName = row.findViewById(R.id.txtName);
            holder.txtAdresse = row.findViewById(R.id.txtAdresse);
            holder.txtPhone = row.findViewById(R.id.txtPhone);
            holder.txtFormation = row.findViewById(R.id.txtFormation);
            holder.txtSpecialite = row.findViewById(R.id.txtSpecialite);
            holder.imageView = row.findViewById(R.id.imgIcon);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }
        Model model = recordList.get(i);
        holder.txtName.setText(model.getName());
        holder.txtAdresse.setText(model.getAdresse());
        holder.txtPhone.setText(model.getPhone());
        holder.txtFormation.setText(model.getFormation());
        holder.txtSpecialite.setText(model.getSpecialite());
        byte[] recordImage = model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage , 0 , recordImage.length);
        holder.imageView.setImageBitmap(bitmap);
        return row;
    }
}
