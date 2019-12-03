package com.example.scrumpoker.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.scrumpoker.R;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.model.Group;

import java.util.ArrayList;

public class CreateGroupDialogFragment extends DialogFragment {
    private void saveGroup(Dialog alert){
        Group group = new Group();
        group.setUsers(new ArrayList<Integer>());
        group.setGroupName(((EditText) getDialog().findViewById(R.id.et_group_name)).getText().toString());
        SharedPreferences preferences = getActivity().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        int userId = preferences.getInt("id", -1);
        DatabaseTransactions.saveGroup(group, getContext(), userId);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_create_group, null))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveGroup(builder.create());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CreateGroupDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
