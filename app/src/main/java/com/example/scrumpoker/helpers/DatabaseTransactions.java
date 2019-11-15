package com.example.scrumpoker.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.fragments.LoginFragment;
import com.example.scrumpoker.model.Role;
import com.example.scrumpoker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTransactions {

    public static void checkBeforeSave(final User user, final Context context,
                                       final FragmentTransaction transaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", user.getUsername()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot q = task.getResult();
                            if(q.size() != 0) {
                                Toast.makeText(context, context.getText(R.string.usernameUsed),
                                        Toast.LENGTH_LONG).show();
                                transaction.commit();
                            }
                            else{
                                DatabaseTransactions.registerUser(user, context, transaction);
                            }
                        }
                    }
                });
    }

    public static void registerUser(final User user, final Context context,
                                    final FragmentTransaction transaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference counter = db.collection("counters").document("user_id");
        final CollectionReference users = db.collection("users");
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(counter);

                int currentId = snapshot.getLong("current").intValue();
                DocumentReference reff = users.document(String.valueOf(currentId));
                user.setId(currentId);
                currentId++;
                transaction.update(counter, "current", currentId);
                transaction.set(reff, user);


                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, context.getText(R.string.congrats),
                        Toast.LENGTH_LONG).show();
                LoginFragment nextFragment = new LoginFragment();
                transaction.replace(R.id.fragment_container, nextFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FAILURE", e.getMessage());
                Toast.makeText(context, context.getText(R.string.fail),
                        Toast.LENGTH_LONG).show();
                transaction.commit();
            }
        });
    }

    public static void userLogin(final String username, final String pass,
                                 final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", username)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();

                    if( snapshot.size() > 0 ) {
                        User user = snapshot.toObjects(User.class).get(0);
                        String encodedPass = Encrypt.md5(pass);

                        if(encodedPass.equals(user.getPassword())) {
                            String role;
                            if(user.getRole() == Role.ADMIN) {
                                role = "ADMIN";
                            }
                            else{
                                role = "USER";
                            }

                            SharedPreferences shared = context.getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("username", user.getUsername());
                            editor.putString("email", user.getEmail());
                            editor.putString("role", role);
                            editor.commit();

                            Intent toMainSection = new Intent(context, MainSectionActivity.class);
                            toMainSection.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(toMainSection);
                        }
                        else{
                            Toast.makeText(context, context.getText(R.string.wrong),
                                    Toast.LENGTH_LONG);
                        }
                    }
                    else{
                        Toast.makeText(context, context.getText(R.string.wrong),
                                Toast.LENGTH_LONG);
                    }
                }
            }
        });
    }
}
