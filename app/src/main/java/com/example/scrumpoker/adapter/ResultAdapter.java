package com.example.scrumpoker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrumpoker.R;
import com.example.scrumpoker.model.Answer;
import com.example.scrumpoker.model.Group;
import com.example.scrumpoker.model.Question;
import com.example.scrumpoker.model.User;

import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultViewHolder> {
    private Context mContext;
    private Group mGroup;
    private int mQuestionId;
    private ArrayList<User> mMembers;

    public ResultAdapter(Context context, Group group, int questionId, ArrayList<User> members){
        this.mContext = context;
        this.mGroup = group;
        this.mQuestionId = questionId;
        this.mMembers = members;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_result_cardview, parent, false);
        return new ResultViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Question q = mGroup.getQuestions().get(mQuestionId);
        Answer item = q.getAnswers().get(position);
        holder.tv_result_number.setText(item.getContent());
        for(User user: mMembers) {
            if(user.getId() == item.getAnswerBy()) {
                holder.tv_result_name.setText(user.getUsername());
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mGroup.getQuestions().get(mQuestionId).getAnswers().size();
    }
}

class ResultViewHolder extends RecyclerView.ViewHolder {
    TextView tv_result_name, tv_result_number;

    public ResultViewHolder(View v){
        super(v);

        tv_result_name = v.findViewById(R.id.tv_name_res);
        tv_result_number = v.findViewById(R.id.tv_number_res);
    }
}
