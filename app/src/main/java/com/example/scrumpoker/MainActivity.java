package com.example.scrumpoker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.scrumpoker.fragments.RegisterFragment;
import com.example.scrumpoker.helpers.DatabaseTransactions;
import com.example.scrumpoker.helpers.Encrypt;
import com.example.scrumpoker.model.Role;
import com.example.scrumpoker.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import javax.crypto.SecretKey;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null) {
                return;
            }

            RegisterFragment firstFragment = new RegisterFragment();

            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
    }

    // Execute function on Register button click in Register fragment
    public void registerClicked(View view) {
        // Check if username field is not empty
        String username = ((EditText)findViewById(R.id.et_username)).getText().toString();
        if(username == null || username.length() == 0) {
            Toast.makeText(this, getText(R.string.usernameCheck), Toast.LENGTH_LONG).show();
            return;
        }

        // Check if email is not empty
        String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
        if(email == null || email.length() == 0) {
            Toast.makeText(this, getText(R.string.emailCheck), Toast.LENGTH_LONG).show();
            return;
        }

        // Check if password is not empty and has a minimal length of 6
        String password = ((EditText) findViewById(R.id.et_pass)).getText().toString();
        if(password == null || password.length() == 0) {
            Toast.makeText(this, getText(R.string.passCheck), Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length() < 6) {
            Toast.makeText(this, getText(R.string.passLength), Toast.LENGTH_LONG).show();
            return;
        }

        // Check if password and repeatPassword match
        String repeat = ((EditText) findViewById(R.id.et_repeat)).getText().toString();
        if(repeat == null || !repeat.equals(password)) {
            Toast.makeText(this, getText(R.string.passLength), Toast.LENGTH_LONG).show();
            return;
        }

        // Get selected role
        Role role;
        String roleInput = ((Spinner) findViewById(R.id.sp_role)).getSelectedItem().toString();
        if(roleInput.equals("Regular User")) {
            role = Role.USER;
        }
        else{
            role = Role.ADMIN;
        }

        // Create new User object
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        try{
            SecretKey generatedKey = Encrypt.generateKey();
            user.setPassword(Encrypt.encryptPass(password, generatedKey).toString());
        }catch (Exception e){
            Log.d("ENCRYPT", "Error happened while generating secret key" + e.getMessage());
        }

        user.setRole(role);


//        DatabaseTransactions.registerUser(user, getApplicationContext());
        DatabaseTransactions.checkBeforeSave(user, getApplicationContext(), getSupportFragmentManager().beginTransaction());
    }
}
