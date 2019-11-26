package com.example.scrumpoker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scrumpoker.R;
import com.example.scrumpoker.model.Group;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionListFragment extends Fragment {


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question_list, container, false);
    }

}
