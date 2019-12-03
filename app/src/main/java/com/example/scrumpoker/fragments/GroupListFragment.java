package com.example.scrumpoker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scrumpoker.R;
import com.example.scrumpoker.adapter.GroupAdapter;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;

import java.util.ArrayList;

/**
    This fragment is responsible for displaying the list of groups that a user is member of
 */
public class GroupListFragment extends Fragment {

    private RecyclerView groupRecycleView;
    private GroupAdapter groupAdapter;
    private RecyclerView.LayoutManager groupLayoutManager;

    public GroupListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_group_list, container, false);
        // initialize the RecyclerView's LayoutManager and Adapter
        groupRecycleView = (RecyclerView) rootview.findViewById(R.id.rv_group);
        groupLayoutManager = new LinearLayoutManager(container.getContext());
        groupRecycleView.setLayoutManager(groupLayoutManager);
        groupAdapter = new GroupAdapter(container.getContext(), (new ArrayList<Group>()));

        // get the list
        DatabaseTransactions.getGroups(container.getContext(), groupRecycleView, groupAdapter);

        // set current fragment name in shared prefs
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FRAGMENT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("current", "group");
        editor.commit();

        return rootview;
    }

}
