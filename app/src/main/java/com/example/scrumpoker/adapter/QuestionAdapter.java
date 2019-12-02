package com.example.scrumpoker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.fragments.AnswerFragment;
import com.example.scrumpoker.fragments.QuestionListFragment;
import com.example.scrumpoker.fragments.ResultFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;
import com.example.scrumpoker.model.Question;
import com.example.scrumpoker.model.Role;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder> {
    private Context mContext;
    private Group mGroup;
    private Role role;
    private ArrayList<CountDownTimer> timers = new ArrayList<>();

    public QuestionAdapter(Context mContext, Group mGroups) {
        this.mContext = mContext;
        this.mGroup = mGroups;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        String rl = sharedPreferences.getString("role", "USER");
        if(rl.equals("ADMIN")) {
            role = Role.ADMIN;
        }
        else{
            filterActiveOrExpired();
            role = Role.USER;
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView;
        if(role == Role.ADMIN) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_question_admin, parent, false);
        }
        else{
            mView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_question_user, parent, false);
        }
        return new QuestionViewHolder(mView, role);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, final int position) {
        final Question item = mGroup.getQuestions().get(position);
        String content;
        if(item.getContent().length() > 50) {
            content = item.getContent().substring(0, 50) + "...";
        }
        else{
            content = item.getContent();
        }
        holder.mContent.setText(content);

        if(System.currentTimeMillis() > item.getExpiration() && !item.isExpired()) {
            DatabaseTransactions.setExpired(mContext, position, true);
        }

        String status;
        if(item.isActive() && !item.isExpired()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getExpiration());

            status = "Active. Until " + calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) +
                        "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) +
                        ":" + calendar.get(Calendar.MINUTE);

            if(role == Role.ADMIN){
                holder.mActivate.setImageResource(R.drawable.ic_clear_black_24dp);
            }

//            CountDownTimer timer = new CountDownTimer(item.getExpiration() - System.currentTimeMillis(),
//                    item.getExpiration() - System.currentTimeMillis()) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @Override
//                public void onFinish() {
//                    DatabaseTransactions.setExpired(mContext, position, true);
//                }
//            };
//            timer.start();
//            timers.add(timer);

        }
        else{
            if(!item.isExpired()){
                status = "Inactive";
            }
            else{
                status = "Expired";
                holder.mStats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

        if(role == Role.ADMIN) {
            holder.mActivate.setClickable(true);
            holder.mActivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(item.isExpired()){
                        Toast.makeText(mContext, R.string.q_expired, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(item.isActive()){
                        ((ImageView) v).setImageResource(R.drawable.ic_check_black_24dp);
                        item.setActive(false);
                        DatabaseTransactions.setQuestionActive(position, false, mContext);
                    }
                    else{
                        if(hasActiveQuestion(mGroup)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.active_msg);
                            builder.setMessage(R.string.active_exists);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ImageView) v).setImageResource(R.drawable.ic_clear_black_24dp);
                                    item.setActive(true);
                                    DatabaseTransactions.setQuestionActive(position, true, mContext);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();
                        }
                        else{
                            ((ImageView) v).setImageResource(R.drawable.ic_clear_black_24dp);
                            item.setActive(true);
                            DatabaseTransactions.setQuestionActive(position, true, mContext);
                        }

                    }
                }
            });
        }
        else{
            if(item.isActive()){
                holder.mContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

    private static boolean hasActiveQuestion(Group group) {
        for(Question q: group.getQuestions()){
            if(q.isActive() && !q.isExpired()) {
                return true;
            }
        }
        return false;
    }

    private void filterActiveOrExpired(){
        ArrayList<Question> qs = new ArrayList<>();
        for(Question q : this.mGroup.getQuestions()) {
            if(q.isActive() || q.isExpired()) {
                qs.add(q);
            }
        }
        this.mGroup.setQuestions(qs);
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
