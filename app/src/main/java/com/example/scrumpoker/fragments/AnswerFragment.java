package com.example.scrumpoker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.scrumpoker.R;
import com.example.scrumpoker.adapter.AnswerAdapter;
import com.example.scrumpoker.adapter.QuestionAdapter;
import com.example.scrumpoker.model.Group;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnswerFragment extends Fragment {
    private RecyclerView answerRecyclerView;
    private RecyclerView.LayoutManager answerLayoutManager;
    private RecyclerView.Adapter answerAdapter;


    public AnswerFragment() {
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

        int qNum = this.getArguments().getInt("selected_question");
        int[] numbers;
        if(group.getQuestions().get(qNum).getType() == 0) {
            numbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        }
        else{
            numbers = new int[]{1, 2, 3, 5, 8, 13, 20, 40, 70, 100};
        }

        SharedPreferences userPrefs = getContext().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);

        View rootview = inflater.inflate(R.layout.fragment_answer, container, false);
        answerRecyclerView = (RecyclerView) rootview.findViewById(R.id.rv_answer);
        answerLayoutManager = new GridLayoutManager(container.getContext(), 2);
        answerRecyclerView.setLayoutManager(answerLayoutManager);
        answerAdapter = new AnswerAdapter(container.getContext(), group, qNum, numbers, userPrefs.getInt("id", 0));
        answerRecyclerView.setAdapter(answerAdapter);

        ((TextView) rootview.findViewById(R.id.tv_q_content)).setText(group.getQuestions().get(qNum).getContent());

        // Inflate the layout for this fragment
        return rootview;
    }

}
