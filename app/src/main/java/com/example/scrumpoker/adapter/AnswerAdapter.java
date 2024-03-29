package com.example.scrumpoker.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.fragments.QuestionListFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Answer;
import com.example.scrumpoker.model.Group;

/*
    This adapter initializes the content of the RecyclerView inside the AnswerFragment
    Constructor params:
        1) mContext: Context - the context of the Activity
        2) mGroup: Group - the currently used group
        3) questionNum: int - the index of the selected question inside the list of questions
        4) numbers: int[] - the possible answers to the given question
        5) userId: int - the id of the logged user
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerViewHolder> {
    private Context mContext;
    private Group mGroup;
    private int questionNum;
    private int[] numbers;
    private int userId;

    public AnswerAdapter(Context context, Group group, int questionNum, int[] numbers, int userId){
        mContext = context;
        mGroup = group;
        this.questionNum = questionNum;
        this.numbers = numbers;
        this.userId = userId;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_answer_cardview, parent, false);
        return new AnswerViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, final int position) {
        holder.tv_number.setText(String.valueOf(numbers[position]));
        holder.tv_number.setClickable(true);
        // if the user clicks on  a number
        holder.tv_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new answer
                Answer answer = new Answer();
                answer.setAnswerBy(userId);
                answer.setContent(String.valueOf(numbers[position]));

                // save the answer to the database
                DatabaseTransactions.addAnswer(mContext, answer, questionNum);

                // navigate back to the QuestionFragment
                QuestionListFragment fragment = new QuestionListFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("item_selected_key", mGroup);
                bundle.putInt("selected_question", position);
                fragment.setArguments(bundle);

                if(mContext instanceof MainSectionActivity) {
                    MainSectionActivity mainSectionActivity = (MainSectionActivity) mContext;
                    mainSectionActivity.switchFragment(fragment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return numbers.length;
    }
}

class AnswerViewHolder extends RecyclerView.ViewHolder {
    TextView tv_number;

    public AnswerViewHolder(View v) {
        super(v);
        tv_number = (TextView) v.findViewById(R.id.tv_number);
    }

}
