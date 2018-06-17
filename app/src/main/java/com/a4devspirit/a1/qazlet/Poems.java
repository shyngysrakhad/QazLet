package com.a4devspirit.a1.qazlet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Poems extends AppCompatActivity {
    TextView text;
    String data;
    String poem;
    String version;
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poems);
        poem = getIntent().getStringExtra("poem");
        data = getIntent().getStringExtra("data");
        version = getIntent().getStringExtra("version");
        text = (TextView)findViewById(R.id.text_poem);
        firebase.child("literature").child(data).child("works").child(poem).child(version).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                text.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
