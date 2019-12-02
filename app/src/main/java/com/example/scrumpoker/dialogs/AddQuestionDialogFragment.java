package com.example.scrumpoker.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Answer;
import com.example.scrumpoker.model.Question;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddQuestionDialogFragment extends DialogFragment {
    View mainView;

    private void saveQuestion() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);

        Question question = new Question();
        String content = ((EditText) mainView.findViewById(R.id.et_question_content)).getText().toString();
        String date = sharedPreferences.getString("date", "");
        String[] ds = date.split("/");
        Log.d("SPLIT", date + ds.length);
        String time = sharedPreferences.getString("time", "");
        String[] ts = time.split(":");
        Log.d("SPLIT", time + ts.length);
        Calendar dateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
        try{
            dateTime.setTime(sdf.parse(date + " " + time));
        }catch (Exception e){
            Log.d("EXCEPTION", e.getMessage());
        }

//        Date date1 = new Date(Integer.parseInt(ds[0]), Integer.parseInt(ds[1]), Integer.parseInt(ds[2]), Integer.parseInt(ts[0]), Integer.parseInt(ts[1]));
//        dateTime.set(Integer.parseInt(ds[0]), Integer.parseInt(ds[1]) - 1, Integer.parseInt(ds[2]) - 1,
//                Integer.parseInt(ts[0]), Integer.parseInt(ts[1]));

        int type = ((Spinner) mainView.findViewById(R.id.sp_cards)).getSelectedItemPosition();

        question.setContent(content);
        question.setExpiration(dateTime.getTimeInMillis());
        question.setType(type);
        question.setActive(false);
        question.setExpired(false);
        question.setAnswers(new ArrayList<Answer>());

        DatabaseTransactions.saveQuestion(question, getContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("date");
        editor.remove("time");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        mainView = layoutInflater.inflate(R.layout.dialog_add_question, null);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);

        builder.setView(mainView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveQuestion();
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

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("ENTER", "SHP");
                if (key.equals("date")) {
                    //SharedPreferences.Editor editor = sharedPreferences.edit();
                    ((TextView) mainView.findViewById(R.id.tv_date)).setText(sharedPreferences.getString("date", ""));
                    //editor.remove("date");
                    Log.d("BYE", "EMERY");
                } else {
                    if (key.equals("time")) {
                        //SharedPreferences.Editor editor = sharedPreferences.edit();
                        ((TextView) mainView.findViewById(R.id.tv_time)).setText(sharedPreferences.getString("time", ""));
                        //editor.remove("time");
                        Log.d("LOL", "XD");
                    }
                }
            }
        });
        return builder.create();
    }
}
