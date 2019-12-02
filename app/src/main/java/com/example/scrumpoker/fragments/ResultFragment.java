package com.example.scrumpoker.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.adapter.ResultAdapter;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;
import com.example.scrumpoker.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
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
        final Group group = getArguments().getParcelable("item_selected_key");
        final int questionIndex = getArguments().getInt("selected_question");

        View rootview = inflater.inflate(R.layout.fragment_result, container, false);
        resultRecyclerView = rootview.findViewById(R.id.rv_result);
        resultLayoutManager = new GridLayoutManager(rootview.getContext(), 2);
        resultRecyclerView.setLayoutManager(resultLayoutManager);
        resultAdapter = new ResultAdapter(rootview.getContext(), group, questionIndex, new ArrayList<User>());
        resultRecyclerView.setAdapter(resultAdapter);

        ((TextView) rootview.findViewById(R.id.tv_content)).setText(group.getQuestions().get(questionIndex).getContent());

        DatabaseTransactions.getResultGroupAndUsers(rootview.getContext(), questionIndex, resultRecyclerView);


        return rootview;
    }

}
