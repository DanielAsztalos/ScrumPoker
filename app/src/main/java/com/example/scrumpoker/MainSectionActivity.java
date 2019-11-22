package com.example.scrumpoker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.scrumpoker.adapter.GroupAdapter;
import com.example.scrumpoker.dialogs.CreateGroupDialogFragment;
import com.example.scrumpoker.dialogs.JoinGroupDialogFragment;
import com.example.scrumpoker.fragments.GroupListFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;

public class MainSectionActivity extends AppCompatActivity {

    private RecyclerView groupRecycleView;
    private GroupAdapter groupAdapter;
    private RecyclerView.LayoutManager groupLayoutManager;

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

//            groupRecycleView = (RecyclerView) findViewById(R.id.rv_group);
//            groupLayoutManager = new LinearLayoutManager(this);
//            groupRecycleView.setLayoutManager(groupLayoutManager);
//
//            DatabaseTransactions.getGroups(getApplicationContext(), groupRecycleView);
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

    public void switchFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_section_fragment, fragment, "id");
        ft.addToBackStack(null);
        ft.commit();
    }

    public void updateGroupAdapter(){
        DatabaseTransactions.getGroups(this, (RecyclerView) findViewById(R.id.rv_group), groupAdapter);
    }


}
