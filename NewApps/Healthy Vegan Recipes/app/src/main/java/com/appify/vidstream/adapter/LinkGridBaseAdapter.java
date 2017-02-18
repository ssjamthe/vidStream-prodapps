package com.appify.vidstream.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.appify.vidstream.app_16.R;
import com.appify.vidstream.control.AppController;
import com.appify.vidstream.model.LinksModel;

import java.util.List;

/**
 * Created by ams on 06-02-2017.
 */

public class LinkGridBaseAdapter extends BaseAdapter{

    private Activity activity;
    private List<LinksModel> linksModels;

    public LinkGridBaseAdapter (Activity activity, List<LinksModel> linksModels){
        this.activity = activity;
        this.linksModels = linksModels;
    }


    @Override
    public int getCount() {
        return linksModels.size();
    }

    @Override
    public Object getItem(int location) {
        return linksModels.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearLinkGrid() {
        linksModels.clear();
        notifyDataSetChanged();
    }

    private static class LinkGridViewHolder {
        TextView name;
        TextView id;
        TextView linkurl;
        NetworkImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            LinkGridViewHolder viewHolder = new LinkGridViewHolder();
            // getting model data for the row
            final LinksModel model = linksModels.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.link_grid_item, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.linkGridName);
                viewHolder.id = (TextView) convertView.findViewById(R.id.linkGridID);
                viewHolder.linkurl = (TextView) convertView.findViewById(R.id.linkGridURL);
                viewHolder.image = (NetworkImageView) convertView.findViewById(R.id.linkGridImage);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (LinkGridViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(model.getLinkName());
            viewHolder.id.setText(model.getLinkID());
            viewHolder.linkurl.setText(model.getLinkURL());
            try {
                viewHolder.image.setImageUrl(model.getLinkImage(), imageLoader);
            }catch (Exception ex){ex.printStackTrace();}
            System.out.println("Link Grid ImageURL: " + model.getLinkImage());
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
