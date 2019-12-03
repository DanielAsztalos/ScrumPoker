package com.example.scrumpoker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.scrumpoker.adapter.GroupAdapter;
import com.example.scrumpoker.dialogs.AddQuestionDialogFragment;
import com.example.scrumpoker.dialogs.CreateGroupDialogFragment;
import com.example.scrumpoker.dialogs.JoinGroupDialogFragment;
import com.example.scrumpoker.fragments.GroupListFragment;
import com.example.scrumpoker.fragments.QuestionListFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainSectionActivity extends AppCompatActivity {

    private RecyclerView groupRecycleView;
    private GroupAdapter groupAdapter;
    private RecyclerView.LayoutManager groupLayoutManager;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_section);

        if(findViewById(R.id.main_section_fragment) != null) {
            if(savedInstanceState != null) {
                return;
            }

            GroupListFragment firstFragment = new GroupListFragment();

            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.main_section_fragment, firstFragment, "start").commit();
            ((MainSectionActivity) this).getSupportActionBar().setTitle(R.string.groups);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logOut() {
        SharedPreferences sharedPreferences = getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("id");
        editor.remove("username");
        editor.remove("email").remove("role");
        editor.commit();

        Intent backToLogin = new Intent(this, MainActivity.class);
        startActivity(backToLogin);
    }

    public void openDialog(View view) {
        SharedPreferences preferences = getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        if(preferences.contains("role") && preferences.getString("role", "USER").equals("ADMIN")) {
            DialogFragment newFragment = new CreateGroupDialogFragment();
            newFragment.show(getSupportFragmentManager(), "group");
        }
        else{
            DialogFragment newFragment = new JoinGroupDialogFragment();
            newFragment.show(getSupportFragmentManager(), "group");
        }

    }

    public void openQuestionDialog(View view) {
        DialogFragment newFragment = new AddQuestionDialogFragment();
        newFragment.show(getSupportFragmentManager(), "question");
    }

    public void openDatePicker(View view) {
        new DatePickerDialog(MainSectionActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void openTimePicker(View view) {
        new TimePickerDialog(MainSectionActivity.this, time, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        }
    };

    public void updateLabel(){
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        SharedPreferences sharedPreferences = getSharedPreferences("DATE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("date", sdf.format(myCalendar.getTime()));
        editor.commit();
    }

    public void updateTimeLabel() {
        SharedPreferences sharedPreferences = getSharedPreferences("DATE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time", myCalendar.get(Calendar.HOUR_OF_DAY) + ":" + myCalendar.get(Calendar.MINUTE));
        editor.commit();
    }

    public void switchFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_section_fragment, fragment, "id");
        ft.addToBackStack(null);
        ft.commit();
    }

    public void updateGroupAdapter(){
        DatabaseTransactions.getGroups(this, (RecyclerView) findViewById(R.id.rv_group), groupAdapter);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("FRAGMENT", Context.MODE_PRIVATE);
        String fragmentName = sharedPreferences.getString("current", "");
        switch (fragmentName) {
            case "question":
                GroupListFragment fragment = new GroupListFragment();
                switchFragment(fragment);
                break;
            default:
                super.onBackPressed();
        }
    }
}
