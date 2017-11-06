package com.ammonia.catchapp.ui_utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.structures.UserProfile;

import java.util.List;

/**
 * Created by dlolpez on 5/10/17.
 */

public class FriendCheckboxAdapter extends MyAbstractRecyclerViewAdapter {
    /**
     * This interface must be implemented by the class making use of this adapter so we know what to
     * do with the checked items.
     */
    public interface OnItemCheckListener {
        void onItemCheck(UserProfile friend);
        void onItemUncheck(UserProfile friend);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    public FriendCheckboxAdapter(Context mContext, List<Item> friends, @NonNull OnItemCheckListener onItemClick) {
        super(mContext, friends);
        this.onItemClick = onItemClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_selection, parent, false);
        return new FriendHolder(view);
    }

    /**
     * When the view is bind to the data, a click listener is attached to keep track of the checked
     * items.
     */

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FriendHolder) {
            final UserProfile currentFriend = (UserProfile) mList.get(position);
            ((FriendHolder) holder).bind(currentFriend);
            ((FriendHolder) holder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FriendHolder) holder).checkbox.setChecked(
                            !((FriendHolder) holder).checkbox.isChecked());
                    if (((FriendHolder) holder).checkbox.isChecked()) {
                        onItemClick.onItemCheck(currentFriend);
                    } else {
                        onItemClick.onItemUncheck(currentFriend);
                    }
                }
            });
        }
    }

    static class FriendHolder extends RecyclerView.ViewHolder {
        View view;
        CheckBox checkbox;
        TextView name;

        public FriendHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.text_friend_name);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox_select_friend);
            checkbox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            view.setOnClickListener(onClickListener);
        }

        void bind(UserProfile friend){
            name.setText(friend.getFirstName() + " " + friend.getLastName());
        }
    }
}
