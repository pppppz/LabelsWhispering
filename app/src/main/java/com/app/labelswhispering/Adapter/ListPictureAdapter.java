package com.app.labelswhispering.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.labelswhispering.Model.Pictures;
import com.app.labelswhispering.R;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class ListPictureAdapter extends RecyclerView.Adapter<ListPictureAdapter.ViewHolder> {

    private List<Pictures> pictures;
    private Context mContext;
    private String TAG = ListPictureAdapter.class.getSimpleName();

    public ListPictureAdapter(Context context, List<Pictures> dataSet) {
        pictures = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_picture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Pictures pic = pictures.get(position);
        ParseFile parseFile = pic.getParseFile("file");
        String url = parseFile.getUrl();
        Glide.with(mContext).load(url).into(viewHolder.imgView);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;

        public ViewHolder(View view) {
            super(view);
            imgView = (ImageView) view.findViewById(R.id.imgPicture);

        }
    }
}