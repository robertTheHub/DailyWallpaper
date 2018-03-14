package com.example.root.dailywallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Rob on 13/03/18.
 */

public class ImageAdapter extends BaseAdapter {
    Context context;
    Bitmap bitmaps[];
    LayoutInflater inflter;
    ImageView pics;

    public ImageAdapter(Context applicationContext, Bitmap[] bitmaps) {
        this.context = applicationContext;
        this.bitmaps = bitmaps;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return bitmaps.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.picture_grid, null); // inflate the layout
        pics = (ImageView) view.findViewById(R.id.scroll_image); // get the reference of ImageView
        pics.setImageBitmap(bitmaps[i]); // set logo images
        return view;
    }
}