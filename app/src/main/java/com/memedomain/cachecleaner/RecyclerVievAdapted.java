package com.memedomain.cachecleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class RecyclerVievAdapted extends RecyclerView.Adapter<RecyclerVievAdapted.ViewHolder> {

    private List<AppStruct> list;
    private Context mContext;
    private PackageManager pm;

    RecyclerVievAdapted(List<AppStruct> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row, parent, false);

        pm = mContext.getPackageManager();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final AppStruct item = list.get(position);
        long temp = item.cacheSize;
        String s;
        if (temp > 1024) {
            temp = temp / 1024;
            if (temp > 1024) {
                temp = temp / 1024;
                s = String.valueOf(temp) + " MB";
            } else
                s = String.valueOf(temp) + " KB";
        } else
            s = String.valueOf(temp) + " B";

        holder.row_author.setText(s);
        holder.row_title.setText(item.appName);
        holder.row_image.setImageDrawable(item.icon);
    }

    List<AppStruct> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView row_image;
        public TextView row_title;
        public TextView row_author;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            row_image = itemView.findViewById(R.id.row_image);
            row_title = itemView.findViewById(R.id.row_title);
            row_author = itemView.findViewById(R.id.row_author);
            relativeLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
