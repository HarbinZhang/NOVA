package com.loonggg.alarmmanager.clock.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Harbin on 6/5/17.
 */

public class AlarmID {

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;

    private int ID;

    public AlarmID(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUserId = mFirebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final Query curtID = mDatabase.child("users").child(mUserId).child("curtID");

        String key = curtID.getRef().toString();
        mDatabase.child("users").child(mUserId).child("curtID").setValue(1);
        curtID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int id = dataSnapshot.getValue(Integer.class);
                ID = id;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int pollID(){
        return ID;
    }

}
