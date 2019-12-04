package com.example.scrumpoker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.adapter.AnswerAdapter;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Answer;
import com.example.scrumpoker.model.Group;

/**
    This fragment is responsible for displaying the possible answers to the selected question
    This fragment expects the following arguments to be sent to:
        1) item_selected_key - Group - the selected group
        2) selected_question - int - the index of the selected question
 */
public class AnswerFragment extends Fragment {
    private RecyclerView answerRecyclerView;
    private RecyclerView.LayoutManager answerLayoutManager;
    private RecyclerView.Adapter answerAdapter;
    private CountDownTimer countDownTimer;


    public AnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get current group from arguments of the fragment
        final Group group = this.getArguments().getParcelable("item_selected_key");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gId", group.getCode());
        editor.commit();

        // get the selected questions index from fragment arguments
        final int qNum = this.getArguments().getInt("selected_question");

        SharedPreferences prefs = getContext().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        int uid = prefs.getInt("id", 0);

        if(!validate(group, uid, qNum)){
            fragmentJump(group);
        }

        // get the type of the possible answers and add them accordingly
        int[] numbers;
        if(group.getQuestions().get(qNum).getType() == 0) {
            numbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        }
        else{
            numbers = new int[]{1, 2, 3, 5, 8, 13, 20, 40, 70, 100};
        }

        // get user and fragment preferences
        SharedPreferences userPrefs = getContext().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        SharedPreferences fragmentPrefs = getContext().getSharedPreferences("FRAGMENT" , Context.MODE_PRIVATE);
        fragmentPrefs.edit().putString("current", "answer").commit();

        // initialize the RecyclerView's LayoutManager and Adapter
        View rootview = inflater.inflate(R.layout.fragment_answer, container, false);
        answerRecyclerView = (RecyclerView) rootview.findViewById(R.id.rv_answer);
        answerLayoutManager = new GridLayoutManager(container.getContext(), 2);
        answerRecyclerView.setLayoutManager(answerLayoutManager);
        answerAdapter = new AnswerAdapter(container.getContext(), group, qNum, numbers, userPrefs.getInt("id", 0));
        answerRecyclerView.setAdapter(answerAdapter);

        // get time until question expires
        long timeUntil = System.currentTimeMillis() - group.getQuestions().get(qNum).getExpiration();
        final View rootviewCopy = rootview;

        // display countdown timer
        countDownTimer = new CountDownTimer(-timeUntil, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("SECS", String.valueOf(millisUntilFinished / 1000));
                long days = (millisUntilFinished / (1000 * 60 * 60 * 24));
                long hours = (millisUntilFinished - days * (24 * 60 * 60 * 1000)) / (1000 * 60 * 60);
                long minutes = (millisUntilFinished - (days * (24 * 60 * 60 * 1000) + hours * (60 * 60 * 1000))) / (60 * 1000);
                long seconds = (millisUntilFinished - (days * (24 * 60 * 60 * 1000) + hours * (60 * 60 * 1000) + minutes * (60 * 1000))) / (1000);

                String hourS = (String) (hours > 9 ? String.valueOf(hours) : "0" + hours);
                String minuteS = (String) (minutes > 9 ? String.valueOf(minutes) : "0" + minutes);
                String secondS = (String) (seconds > 9 ? String.valueOf(seconds) : "0" + seconds);

                ((TextView) rootviewCopy.findViewById(R.id.tv_clock)).setText(
                        days + " days, " + hourS + ":" + minuteS + ":" + secondS
                );
            }

            @Override
            public void onFinish() {
                // when time's over go back to QuestionFragment
                QuestionListFragment fragment = new QuestionListFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("item_selected_key", group);
                fragment.setArguments(bundle);

                DatabaseTransactions.setExpired(getContext(), qNum, true);

                if(getActivity() instanceof MainSectionActivity){
                    ((MainSectionActivity) getActivity()).switchFragment(fragment);
                }


            }
        };
        countDownTimer.start();

        // display question content
        ((TextView) rootview.findViewById(R.id.tv_q_content)).setText(group.getQuestions().get(qNum).getContent());

        return rootview;
    }

    @Override
    public void onStop() {
        countDownTimer.cancel();
        super.onStop();
    }

    private void fragmentJump(Group mItemSelected) {
        QuestionListFragment fragment = new QuestionListFragment();
        Bundle bundle = new Bundle();
        // Put the selected group as an argument to the new fragment
        bundle.putParcelable("item_selected_key", mItemSelected);
        fragment.setArguments(bundle);

        if(getContext() instanceof MainSectionActivity) {
            MainSectionActivity mainSectionActivity = (MainSectionActivity) getContext();
            mainSectionActivity.switchFragment(fragment);
        }
    }

    private boolean validate(Group group, int id, int qNum) {
        if(group.getQuestions().get(qNum).getExpiration() <= System.currentTimeMillis()) {
            return false;
        }
        for(Answer a : group.getQuestions().get(qNum).getAnswers()) {
            if(a.getAnswerBy() == id) {
                return false;
            }
        }
        return true;
    }
}
