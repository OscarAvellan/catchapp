package com.ammonia.catchapp.ui_utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by dlolpez on 10/10/17.
 */

/**
 * The rest of recycler view adapters can abstract behaviour from this one.
 */

public abstract class MyAbstractRecyclerViewAdapter extends RecyclerView.Adapter {
    protected Context mContext;
    protected List<Item> mList;

    public MyAbstractRecyclerViewAdapter (Context mContext, List<Item> mList){
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);
    @Override
    public abstract void onBindViewHolder(final RecyclerView.ViewHolder holder, int position);
}
