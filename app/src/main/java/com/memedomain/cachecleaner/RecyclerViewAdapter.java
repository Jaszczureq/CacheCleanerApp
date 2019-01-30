package com.memedomain.cachecleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private List<String> list;
    private Context mContext;
    private Method getPackageSizeInfo;

    public RecyclerViewAdapter(List<String> list, Context mContext) {
        Log.d(TAG, "RecyclerViewAdapter: Constructor");
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sala, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String s = list.get(position);
        String[] temp = s.split(",");
        int x=Integer.parseInt(temp[2]);

        if (x > 1024) {
            x = x / 1024;
            if (x > 1024) {
                x = x / 1024;
                temp[2] = String.valueOf(x) + " MB";
            } else
                temp[2] = String.valueOf(x) + " KB";
        } else
            temp[2] = String.valueOf(x) + " B";

        holder.row_id.setText(temp[0]);
        holder.row_uuid.setText(temp[1]);
        holder.row_cache.setText(temp[2]);
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView row_id;
        TextView row_uuid;
        TextView row_cache;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: Contructor called");

            row_id = itemView.findViewById(R.id.row_id);
            row_uuid = itemView.findViewById(R.id.row_uuid);
            row_cache = itemView.findViewById(R.id.row_cache);
            relativeLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
