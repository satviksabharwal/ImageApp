package com.example.satvi.googleauthentication;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Upload> {

    Context context;
    int layoutResourceId;
    List<Upload> data= null;

    static class DataHolder{

        ImageView ivImage;
        TextView titleView;
        TextView descriptionText;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DataHolder holder = null;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId,parent,false);
            holder = new DataHolder();
            holder.ivImage = (ImageView)convertView.findViewById(R.id.pictureImageView);
            holder.titleView = (TextView)convertView.findViewById(R.id.titleTextView);
            holder.descriptionText = (TextView)convertView.findViewById(R.id.descriptionTextView);

            convertView.setTag(holder);
        }else{
            holder = (DataHolder)convertView.getTag();
        }

        Upload dataItem = data.get(position);
        Picasso.get().load(dataItem.getImageUrl()).fit().into(holder.ivImage);
        holder.titleView.setText(dataItem.getTitle());
        holder.descriptionText.setText(dataItem.getDescription());

        return convertView;


    }

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Upload> objects) {
        super(context, resource, objects);

        this.context = context;
        this.layoutResourceId = resource;
        this.data = objects;

    }
}
