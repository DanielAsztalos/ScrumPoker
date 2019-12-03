package com.example.scrumpoker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.fragments.AnswerFragment;
import com.example.scrumpoker.fragments.ResultFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;
import com.example.scrumpoker.model.Question;
import com.example.scrumpoker.model.Role;

import java.util.ArrayList;
import java.util.Calendar;

/*
    This adapter initializes the RecyclerView inside the QuestionListFragment
    Constructor params:
        1) mContext: Context - the context of the Activity
        2) mGroup: Group - the currently selected group
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder> {
    private Context mContext;
    private Group mGroup;
    private Role role;
    private CountDownTimer timer;

    public QuestionAdapter(Context mContext, Group mGroups) {
        this.mContext = mContext;
        this.mGroup = mGroups;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        String rl = sharedPreferences.getString("role", "USER");
        if(rl.equals("ADMIN")) {
            role = Role.ADMIN;
        }
        else{
            // if the user has USER role than show only the active and expired questions
            filterActiveOrExpired();
            role = Role.USER;
        }

        // start a timer that checks every second if any of the questions has expired
        timer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                checkIfExpired();
            }
        }.start();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        // if user has ADMIN role
        if(role == Role.ADMIN) {
            // inflate admin view
            mView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_question_admin, parent, false);
        }
        else{
            // inflate user view
            mView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_question_user, parent, false);
        }
        return new QuestionViewHolder(mView, role);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, final int position) {
        final Question item = mGroup.getQuestions().get(position);
        String content;

        // display only 50 characters from the content of the question as a title
        if(item.getContent().length() > 50) {
            content = item.getContent().substring(0, 50) + "...";
        }
        else{
            content = item.getContent();
        }
        holder.mContent.setText(content);

        // if a question has expired
        if(System.currentTimeMillis() > item.getExpiration() && !item.isExpired()) {
            // set the isExpired property to true
            DatabaseTransactions.setExpired(mContext, position, true);
        }

        String status;
        // get the status of the question
        if(item.isActive() && !item.isExpired()) {
            // active
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getExpiration());

            status = "Active. Until " + calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) +
                        "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) +
                        ":" + calendar.get(Calendar.MINUTE);

            if(role == Role.ADMIN){
                holder.mActivate.setImageResource(R.drawable.ic_clear_black_24dp);
            }


        }
        else{
            if(!item.isExpired()){
                // Inactive
                status = "Inactive";
            }
            else{
                // Expired
                status = "Expired";
                // if the user clicks on the stats icon
                holder.mStats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // navigate to the ResultFragment
                        ResultFragment fragment = new ResultFragment();
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
        }
        holder.mStatus.setText(status);

        // if user has ADMIN privileges
        if(role == Role.ADMIN) {
            // the user can activate/disactivate a question by clicking on the tick/X icon
            holder.mActivate.setClickable(true);
            holder.mActivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(item.isExpired()){
                        // if question is expired it cannot be activated
                        Toast.makeText(mContext, R.string.q_expired, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(item.isActive()){
                        // if the item is active than disactivate it
                        ((ImageView) v).setImageResource(R.drawable.ic_check_black_24dp);
                        item.setActive(false);
                        DatabaseTransactions.setQuestionActive(position, false, mContext);
                    }
                    else{
                        // if it is not active
                        if(hasActiveQuestion(mGroup)){
                            // and there's another question that's active
                            // display a dialog and ask if they really want to activate it and disactivate the other
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.active_msg);
                            builder.setMessage(R.string.active_exists);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // if yes than set current question to active and the other one to inactive
                                    ((ImageView) v).setImageResource(R.drawable.ic_clear_black_24dp);
                                    item.setActive(true);
                                    DatabaseTransactions.setQuestionActive(position, true, mContext);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // else do nothing
                                }
                            });
                            builder.create().show();
                        }
                        else{
                            // just activate the question
                            ((ImageView) v).setImageResource(R.drawable.ic_clear_black_24dp);
                            item.setActive(true);
                            DatabaseTransactions.setQuestionActive(position, true, mContext);
                        }

                    }
                }
            });
        }
        else{

            // if item is active
            if(item.isActive()){
                // add onClick listener to the question title
                holder.mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // navigate to the AnswerFragment
                        AnswerFragment fragment = new AnswerFragment();
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
        }
    }

    @Override
    public int getItemCount() {
        return mGroup.getQuestions().size();
    }

    // checks if a group has any active questions that are not expired
    private static boolean hasActiveQuestion(Group group) {
        for(Question q: group.getQuestions()){
            if(q.isActive() && !q.isExpired()) {
                return true;
            }
        }
        return false;
    }

    // filters out the Inactive questions
    private void filterActiveOrExpired(){
        ArrayList<Question> qs = new ArrayList<>();
        for(Question q : this.mGroup.getQuestions()) {
            if(q.isActive() || q.isExpired()) {
                qs.add(q);
            }
        }
        this.mGroup.setQuestions(qs);
    }

    // checks if there are any questions that have expired and sets their isExpired property accordingly
    // and if there still are any active questions than set the timer again
    private void checkIfExpired(){
        boolean hasNotExpired = false;
        for(Question q: mGroup.getQuestions()) {
            if(!q.isExpired() && q.getExpiration() <= System.currentTimeMillis()) {
                DatabaseTransactions.setExpired(mContext, mGroup.getQuestions().indexOf(q), true);
            }
            else{
                if(!q.isExpired()) {
                    hasNotExpired = true;
                }
            }
        }
        if(hasNotExpired) {
            timer.start();
        }

    }
}

class QuestionViewHolder extends RecyclerView.ViewHolder {
    TextView mContent, mStatus;
    ImageView mActivate, mStats;

    public QuestionViewHolder(View v, Role role) {
        super(v);

        if(role == Role.ADMIN) {
            mContent = v.findViewById(R.id.tv_question_cont);
            mStatus = v.findViewById(R.id.tv_question_status);
            mActivate = v.findViewById(R.id.iv_activate);
            mStats = v.findViewById(R.id.iv_stats);
        }
        else{
            mContent = v.findViewById(R.id.tv_question_cont2);
            mStatus = v.findViewById(R.id.tv_question_status2);
            mStats = v.findViewById(R.id.iv_stats2);
        }
    }
}
