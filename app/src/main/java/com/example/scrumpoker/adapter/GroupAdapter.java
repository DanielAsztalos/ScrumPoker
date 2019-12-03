package com.example.scrumpoker.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.fragments.QuestionListFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;
import com.example.scrumpoker.model.Role;

import java.util.List;

/*
    This adapter initializes the RecyclerView inside the GroupListFragment
    Constructor params:
        1) mContext: Context - the context of the Activity
        2) mGroups: ArrayList<Group> - the list of the groups that the user is part of
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private Context mContext;
    private List<Group> mGroups;
    private Role role;

    public  GroupAdapter(Context context, List<Group> groups){
        this.mContext = context;
        this.mGroups = groups;
        SharedPreferences sharedPreferences = context.getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("role", "USER").equals("ADMIN")) {
            role = Role.ADMIN;
        }
        else{
            role = Role.USER;
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        // if an admin is logged in
        if(role == Role.ADMIN){
            // inflate the admin view
            mView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_group_row, parent, false);
        }
        else{
            // inflate the user view
            mView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_group_row_user, parent, false);
        }
        return new GroupViewHolder(mView, role);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder holder, int position) {

        final Group item = mGroups.get(position);
        holder.tv_group_name.setText(mGroups.get(position).getGroupName());
        // if a user clicks on the name of the group
        holder.tv_group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to the QuestionListFragment
                fragmentJump(item);
            }
        });

        // if the user has ADMIN role display a delete icon
        if(role == Role.ADMIN) {
            // if the delete icon was clicked
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete the group
                    DatabaseTransactions.deleteGroup(item, mContext);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    // This function manages the navigation to the QuestionListFragment
    // params:
    //      1) mItemSelected: Group - the selected group
    private void fragmentJump(Group mItemSelected) {
        QuestionListFragment fragment = new QuestionListFragment();
        Bundle bundle = new Bundle();
        // Put the selected group as an argument to the new fragment
        bundle.putParcelable("item_selected_key", mItemSelected);
        fragment.setArguments(bundle);

        if(mContext instanceof MainSectionActivity) {
            MainSectionActivity mainSectionActivity = (MainSectionActivity) mContext;
            mainSectionActivity.switchFragment(fragment);
        }
    }
}

class GroupViewHolder extends RecyclerView.ViewHolder {
    TextView tv_group_name;
    ImageView ivDelete;

    public GroupViewHolder(View v, Role role) {
        super(v);
        if(role == Role.ADMIN) {
            tv_group_name = v.findViewById(R.id.tv_group_name);
            ivDelete = v.findViewById(R.id.iv_delete);
        }
        else{
            tv_group_name = v.findViewById(R.id.tv_group_name_2);
        }


    }
}
