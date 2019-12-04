package com.example.scrumpoker.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrumpoker.MainSectionActivity;
import com.example.scrumpoker.R;
import com.example.scrumpoker.adapter.GroupAdapter;
import com.example.scrumpoker.adapter.QuestionAdapter;
import com.example.scrumpoker.adapter.ResultAdapter;
import com.example.scrumpoker.fragments.LoginFragment;
import com.example.scrumpoker.model.Answer;
import com.example.scrumpoker.model.Group;
import com.example.scrumpoker.model.Question;
import com.example.scrumpoker.model.Role;
import com.example.scrumpoker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class contains functions that realize the connection to the database
 * and transactions needed by other classes
 */

public class DatabaseTransactions {

    /**
     * This class checks if a user can be saved to the database and if so it calls the function
     * that it to the db
     * @param user
     * @param context
     * @param transaction
     */
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

    /**
     * This function saves the user to the database and if that is successful it takes the user
     * to the login fragment else it writes an error message
     * @param user
     * @param context
     * @param transaction
     */
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

    /**
     * This function checks if login credentials are correct and if so it takes the user to the
     * GroupList fragment of the MainSectionActivity
     * @param username
     * @param pass
     * @param context
     */
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
                            editor.putInt("id", user.getId());
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
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(context, context.getText(R.string.wrong),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /**
     * This function is responsible to generate a 6 character long code for a group
     * @return key
     */
    public static String generateKey() {
        StringBuilder key = new StringBuilder();
        Random randnum = new Random();
        int n;
        for (int i = 0; i < 6; i++) {
            n = randnum.nextInt(10);
            while(i == 0 && n == 0){
                n = randnum.nextInt(10);
            }
            key.append(n);
        }
        return key.toString();
    }

    /**
     * This function saves a given Group to the database if successful it updates the list of the groups
     * of the user
     * @param group
     * @param context
     * @param userId
     */
    public static void saveGroup(final Group group, final Context context, final int userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference groupIds = db.collection("counters").document("group_ids");
        final CollectionReference groups = db.collection("groups");
        final DocumentReference user = db.collection("users").document(String.valueOf(userId));
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                List<String> ids = (List<String>) transaction.get(groupIds).get("ids");
                User user1 = transaction.get(user).toObject(User.class);
                Log.d("CHECK", user1.getUsername() + " " + userId + " " + ids.toString());
                String key;
                boolean keyOK = true;

                do{
                    key = DatabaseTransactions.generateKey();
                    if(ids.contains(key)) {
                        keyOK = false;
                    }
                }while(!keyOK);

                ids.add(key);
                Log.d("LOL", ids.toString());
                transaction.update(groupIds, "ids", ids);

                group.setCode(key);
                groups.document(key).set(group);

                user1.addGroupId(key);

                transaction.update(user, "groupIds", user1.getGroupIds());

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MainSectionActivity mainSectionActivity = (MainSectionActivity) context;
                mainSectionActivity.updateGroupAdapter();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FAIL", e.getMessage());
            }
        });
    }

    /**
     * This function gets the groups that a specific user is part of and creates an adapter containing these
     * groups
     * @param context
     * @param rv_groups
     * @param adapter
     */
    public static void getGroups(final Context context, final RecyclerView rv_groups, final GroupAdapter adapter) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        final DocumentReference user = db.collection("users").document(String.valueOf(
                sharedPreferences.getInt("id", -1)
        ));
        final List<Group> groups = new ArrayList<Group>();

        user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot){
                final User u = documentSnapshot.toObject(User.class);
                List<String> gIds = new ArrayList<>();
                for(String id : u.getGroupIds()) {
                    gIds.add(String.valueOf(id));
                }
                if(!gIds.isEmpty()) {
                    db.collection("groups").whereIn("code", gIds).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(DocumentSnapshot ds: queryDocumentSnapshots) {
                                        groups.add(ds.toObject(Group.class));
                                    }
                                    Log.d("HALF_SUCCESS", groups.toString());
                                    GroupAdapter adapter = new GroupAdapter(context, groups);
                                    rv_groups.setAdapter(adapter);
                                }
                            });
                }
                else{
                    GroupAdapter adapter = new GroupAdapter(context, groups);
                    rv_groups.setAdapter(adapter);
                }
            }
        });


    }

    /**
     * This function deletes a selected group from the database and updates the group adapter
     * @param group
     * @param context
     */
    public static void deleteGroup(final Group group, final Context context) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference groupCounters = db.collection("counters").document("group_ids");
        final DocumentReference groupRef = db.collection("groups").document(group.getCode());
        db.collection("users").whereArrayContains("groupIds", group.getCode()).get().addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                List<User> usersWithinGroup = (List<User>) queryDocumentSnapshots.toObjects(User.class);
                                List<String> groupIds = (List<String>)transaction.get(groupCounters).get("ids");

                                groupIds.remove(group.getCode());
                                transaction.update(groupCounters, "ids", groupIds);

                                for(User u : usersWithinGroup) {
                                    u.getGroupIds().remove(group.getCode());
                                    transaction.update(db.collection("users").document(String.valueOf(u.getId())),
                                            "groupIds", u.getGroupIds());
                                }


                                transaction.delete(groupRef);

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainSectionActivity mainSectionActivity = (MainSectionActivity) context;
                                mainSectionActivity.updateGroupAdapter();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TRANSACTION", e.getMessage());
                            }
                        });
                    }
                }
        );

    }

    /**
     * This function adds a user to a group if a group with the given code can be found
     * @param gId
     * @param context
     */
    public static void joinGroup(final String gId, final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        final DocumentReference userRef = db.collection("users")
                .document(String.valueOf(sharedPreferences.getInt("id", -1)));
        final DocumentReference groupRef = db.collection("groups").document(gId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                User user = transaction.get(userRef).toObject(User.class);
                Group group;
                if(transaction.get(groupRef).exists()) {
                    user.addGroupId(gId);

                }
                group = transaction.get(groupRef).toObject(Group.class);
                group.addUser(user.getId());
                transaction.update(groupRef, "users", group.getUsers());
                transaction.update(userRef, "groupIds", user.getGroupIds());
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, context.getText(R.string.successJoin), Toast.LENGTH_LONG).show();

                MainSectionActivity mainSectionActivity = (MainSectionActivity) context;
                mainSectionActivity.updateGroupAdapter();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, context.getText(R.string.failJoin), Toast.LENGTH_LONG).show();
                Log.d("JOING", e.getMessage());
            }
        });
    }

    /**
     * This function saves a question to the database
     * @param question
     * @param context
     */
    public static void saveQuestion(final Question question, final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference questionIds = db.collection("counters").document("question_ids");
        SharedPreferences sharedPreferences = context.getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        final DocumentReference groupRef = db.collection("groups").document(sharedPreferences.getString("gId", ""));
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                List<String> ids = (List<String>) transaction.get(questionIds).get("ids");
                Group group = transaction.get(groupRef).toObject(Group.class);
                String key;
                boolean keyOK = true;

                do{
                    key = DatabaseTransactions.generateKey();
                    if(ids.contains(key)) {
                        keyOK = false;
                    }
                }while(!keyOK);

                ids.add(key);
                question.setId(key);
                group.addQuestion(question);

                transaction.update(questionIds, "ids", ids);
                transaction.update(groupRef, "questions", group.getQuestions());

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, context.getText(R.string.question_saved), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Q_FAIL", e.getMessage());
            }
        });

    }

    /**
     * This function sets a Question's active property in the database
     * @param position
     * @param status
     * @param context
     */
    public static void setQuestionActive(final int position, final boolean status, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        final DocumentReference groupRef = db.collection("groups").document(sharedPreferences.getString("gId", ""));
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Group group = transaction.get(groupRef).toObject(Group.class);
                if(status) {
                    for(int i = 0; i < group.getQuestions().size(); i++){
                        if(group.getQuestions().get(i).isActive()) {
                            group.getQuestions().get(i).setActive(false);
                        }
                    }
                }

                group.getQuestions().get(position).setActive(status);

                transaction.update(groupRef, "questions", group.getQuestions());

                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SETACTIVE", e.getMessage());
            }
        });
    }

    /**
     * This function creates a Database listener that listens to changes made to a question
     * and if so it updates the question RecyclerView
     * @param context
     * @param recyclerView
     * @return
     */
    public static ListenerRegistration addGroupListener(final Context context, final RecyclerView recyclerView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        DocumentReference groupRef = db.collection("groups").document(sharedPreferences.getString("gId", ""));
        ListenerRegistration registration = groupRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e == null && documentSnapshot != null && documentSnapshot.exists()) {
                    Group group = documentSnapshot.toObject(Group.class);
                    QuestionAdapter questionAdapter = new QuestionAdapter(context, group);
                    recyclerView.setAdapter(questionAdapter);
                }
            }
        });
        return registration;

    }

    /**
     * This function sets the expired property of a question
     * @param context
     * @param position
     * @param status
     */
    public static void setExpired(final Context context, final int position, final boolean status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        final DocumentReference groupRef = db.collection("groups").document(sharedPreferences.getString("gId", ""));
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Group group = transaction.get(groupRef).toObject(Group.class);
                group.getQuestions().get(position).setExpired(status);

                transaction.update(groupRef, "questions", group.getQuestions());
                return null;
            }
        });
    }

    /**
     * This function saves an answer to a specific question to the database
     * @param context
     * @param answer
     * @param qIndex
     */
    public static void addAnswer(final Context context, final Answer answer, final int qIndex) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        final DocumentReference groupRef = db.collection("groups").document(sharedPreferences.getString("gId", ""));
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Group group = transaction.get(groupRef).toObject(Group.class);
                group.getQuestions().get(qIndex).addAnswer(answer);
                if(group.getQuestions().get(qIndex).getAnswers().size() == group.getUsers().size()) {
                    group.getQuestions().get(qIndex).setActive(false);
                    group.getQuestions().get(qIndex).setExpired(true);
                }

                transaction.update(groupRef, "questions", group.getQuestions());
                return null;
            }
        });
    }

    /**
     * Gets the group and it's members in order to display the results of a question
     * @param context
     * @param qIndex
     * @param recyclerView
     */
    public static void getResultGroupAndUsers(final Context context, final int qIndex, final RecyclerView recyclerView){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences("GROUP", Context.MODE_PRIVATE);
        DocumentReference groupRef = db.collection("groups").document(sharedPreferences.getString("gId", ""));
        groupRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Group group = documentSnapshot.toObject(Group.class);
                db.collection("users").whereIn("id", group.getUsers()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<User> members = new ArrayList<>();
                                for (DocumentSnapshot documentReference: queryDocumentSnapshots){
                                    members.add(documentReference.toObject(User.class));
                                }

                                ResultAdapter adapter = new ResultAdapter(context, group, qIndex, members);
                                recyclerView.setAdapter(adapter);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
