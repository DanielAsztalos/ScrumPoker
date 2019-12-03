package com.example.scrumpoker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.scrumpoker.R;
import com.example.scrumpoker.helpers.DatabaseTransactions;

/*
    This dialog fragment contains the join group form
 */

public class JoinGroupDialogFragment extends DialogFragment {

    // validate code and begin transaction
    private void joinGroup(Dialog alert){
        String code = ((EditText) getDialog().findViewById(R.id.et_group_code)).getText().toString();
        if(code.length() == 0) {
            Toast.makeText(alert.getContext(), "Please fill in the code field!", Toast.LENGTH_LONG).show();
        }
        else{
            DatabaseTransactions.joinGroup(code, getContext());
        }
    }

    public Dialog onCreateDialog(Bundle savedInstance) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_join_group, null))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        joinGroup(builder.create());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JoinGroupDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
