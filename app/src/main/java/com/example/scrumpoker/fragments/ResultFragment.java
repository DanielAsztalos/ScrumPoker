package com.example.scrumpoker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.adapter.ResultAdapter;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Answer;
import com.example.scrumpoker.model.Group;
import com.example.scrumpoker.model.User;

import java.util.ArrayList;

/**
 * This fragment is responsible for displaying the results to a question that has expired
 * This fragment expects the following argument to be sent to:
 *         1) item_selected_key - Group - the selected group
 *         2) selected_question - int - the index of the selected question
 */
public class ResultFragment extends Fragment {
    private RecyclerView resultRecyclerView;
    private RecyclerView.LayoutManager resultLayoutManager;
    private RecyclerView.Adapter resultAdapter;


    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get group from fragment arguments
        final Group group = getArguments().getParcelable("item_selected_key");
        // get the selected questions index from fragment arguments
        final int questionIndex = getArguments().getInt("selected_question");

        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_result, container, false);
        // initialize the RecyclerView's LayoutManager and Adapter
        resultRecyclerView = rootview.findViewById(R.id.rv_result);
        resultLayoutManager = new GridLayoutManager(rootview.getContext(), 2);
        resultRecyclerView.setLayoutManager(resultLayoutManager);
        resultAdapter = new ResultAdapter(rootview.getContext(), group, questionIndex, new ArrayList<User>());
        resultRecyclerView.setAdapter(resultAdapter);

        // display the question content
        ((TextView) rootview.findViewById(R.id.tv_content)).setText(group.getQuestions().get(questionIndex).getContent());

        // get all answers and Users that responded to the question
        DatabaseTransactions.getResultGroupAndUsers(rootview.getContext(), questionIndex, resultRecyclerView);

        // put current fragment name in shared prefs
        SharedPreferences fragmentPrefs = getContext().getSharedPreferences("FRAGMENT", Context.MODE_PRIVATE);
        fragmentPrefs.edit().putString("current", "result").commit();

        return rootview;
    }

}
