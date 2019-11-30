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
 * A simple {@link Fragment} subclass.
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
        Group group = this.getArguments().getParcelable("item_selected_key");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gId", group.getCode());
        editor.commit();

        View rootview = inflater.inflate(R.layout.fragment_question_list, container, false);
        questionRecyclerView = (RecyclerView) rootview.findViewById(R.id.rv_questions);
        questionLayoutManager = new LinearLayoutManager(container.getContext());
        questionRecyclerView.setLayoutManager(questionLayoutManager);
        questionAdapter = new QuestionAdapter(container.getContext(), group);
        questionRecyclerView.setAdapter(questionAdapter);

        if(sharedPreferences.getString("role", "USER").equals("USER")){
            ((FloatingActionButton) rootview.findViewById(R.id.fa_question)).hide();
        }

        registration = DatabaseTransactions.addGroupListener(getContext(), questionRecyclerView);

        this.code = group.getCode();
        // Inflate the layout for this fragment
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
