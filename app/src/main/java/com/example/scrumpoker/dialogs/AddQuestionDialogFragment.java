package com.example.scrumpoker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.scrumpoker.R;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Answer;
import com.example.scrumpoker.model.Question;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/*
    This dialog contains the Question creation form and form validation
 */

public class AddQuestionDialogFragment extends DialogFragment {
    View mainView;

    // this function prepares the question to be saved and starts the save transaction
    private void saveQuestion() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);
        // Create new Question
        Question question = new Question();
        // Get content
        String content = ((EditText) mainView.findViewById(R.id.et_question_content)).getText().toString();
        // Get date and time from shared preferences and prepare it
        String date = sharedPreferences.getString("date", "");
        String time = sharedPreferences.getString("time", "");
        // add prepared date to calendar object
        Calendar dateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        try{
            dateTime.setTime(sdf.parse(date + " " + time));
        }catch (Exception e){
            Log.d("EXCEPTION", e.getMessage());
        }
        // get question type
        int type = ((Spinner) mainView.findViewById(R.id.sp_cards)).getSelectedItemPosition();
        // initialize question properties
        question.setContent(content);
        question.setExpiration(dateTime.getTimeInMillis());
        question.setType(type);
        question.setActive(false);
        question.setExpired(false);
        question.setAnswers(new ArrayList<Answer>());
        // begin transaction
        DatabaseTransactions.saveQuestion(question, getContext());
        // remove date and time from shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("date");
        editor.remove("time");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // create dialog builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        mainView = layoutInflater.inflate(R.layout.dialog_add_question, null);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);

        // set negative and positive buttons
        builder.setView(mainView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(((EditText) mainView.findViewById(R.id.et_question_content)).getText().toString().length() > 0){
                            saveQuestion();
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getContext(), "Didn't save Question because the Question content was empty!", Toast.LENGTH_LONG).show();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("date");
                editor.remove("time");
                AddQuestionDialogFragment.this.getDialog().cancel();
            }
        });

        // attach listener to shared preferences so that time and date can be displayed
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("date")) {
                    ((TextView) mainView.findViewById(R.id.tv_date)).setText(sharedPreferences.getString("date", ""));
                } else {
                    if (key.equals("time")) {
                        ((TextView) mainView.findViewById(R.id.tv_time)).setText(sharedPreferences.getString("time", ""));
                    }
                }
            }
        });
        return builder.create();
    }
}
