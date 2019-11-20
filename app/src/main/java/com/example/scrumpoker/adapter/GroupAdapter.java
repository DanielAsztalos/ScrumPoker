package com.example.scrumpoker.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.dialogs.CreateGroupDialogFragment;
import com.example.scrumpoker.fragments.GroupListFragment;
import com.example.scrumpoker.fragments.QuestionListFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private Context mContext;
    private List<Group> mGroups;

    public  GroupAdapter(Context context, List<Group> groups){
        this.mContext = context;
        this.mGroups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_group_row, parent, false);
        return new GroupViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder holder, int position) {

        final Group item = mGroups.get(position);
        holder.tv_group_name.setText(mGroups.get(position).getGroupName());
        holder.tv_group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentJump(item);
            }
        });
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogForEditing(item, v);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseTransactions.deleteGroup(item, mContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    private void fragmentJump(Group mItemSelected) {
        QuestionListFragment fragment = new QuestionListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("item_selected_key", mItemSelected);
        fragment.setArguments(bundle);

        if(mContext instanceof MainSectionActivity) {
            MainSectionActivity mainSectionActivity = (MainSectionActivity) mContext;
            mainSectionActivity.switchFragment(fragment);
        }
    }

    private void  openDialogForEditing(Group selectedGroup, View v) {
        if(mContext instanceof MainSectionActivity) {
            MainSectionActivity mainSectionActivity = (MainSectionActivity) mContext;
            mainSectionActivity.openDialog(v);
        }
    }
}

class GroupViewHolder extends RecyclerView.ViewHolder {
    TextView tv_group_name;
    ImageView ivEdit, ivDelete;

    public GroupViewHolder(View v) {
        super(v);
        tv_group_name = v.findViewById(R.id.tv_group_name);
        ivEdit = v.findViewById(R.id.iv_edit);
        ivDelete = v.findViewById(R.id.iv_delete);

    }
}
