package com.example.scrumpoker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.scrumpoker.R;
import com.example.scrumpoker.adapter.GroupAdapter;
import com.example.scrumpoker.adapter.QuestionAdapter;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
    This fragment is responsible for displaying the group code and the list of the questions
    that belong to that group
    This fragment expects the following argument to be sent to:
        1) item_selected_key - Group - the selected group
 */
public class QuestionListFragment extends Fragment {
    private String code;
    private RecyclerView questionRecyclerView;
    private RecyclerView.LayoutManager questionLayoutManager;
    private QuestionAdapter questionAdapter;
    private ListenerRegistration registration;

    public QuestionListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get group from fragment arguments
        Group group = this.getArguments().getParcelable("item_selected_key");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gId", group.getCode());
        editor.commit();

        // put current fragment name in shared prefs
        SharedPreferences fragmentPrefs = getContext().getSharedPreferences("FRAGMENT", Context.MODE_PRIVATE);
        fragmentPrefs.edit().putString("current", "question").commit();

        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_question_list, container, false);
        // initialize the RecyclerView's LayoutManager and Adapter
        questionRecyclerView = (RecyclerView) rootview.findViewById(R.id.rv_questions);
        questionLayoutManager = new LinearLayoutManager(container.getContext());
        questionRecyclerView.setLayoutManager(questionLayoutManager);
        questionAdapter = new QuestionAdapter(container.getContext(), group);
        questionRecyclerView.setAdapter(questionAdapter);

        // get user preferences
        SharedPreferences userPrefs = getContext().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        if(userPrefs.getString("role", "USER").equals("USER")){
            // if user doesn't have ADMIN privileges hide "+" button
            ((FloatingActionButton) rootview.findViewById(R.id.fa_question)).hide();
        }

        // register a listener for every change made to the group
        registration = DatabaseTransactions.addGroupListener(getContext(), questionRecyclerView);

        // get group code
        this.code = group.getCode();

        return rootview;
    }

    @Override
    public void onResume() {
        ((TextView) getActivity().findViewById(R.id.tv_code_content)).setText(this.code);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        this.registration.remove();
        super.onDestroy();
    }
}
