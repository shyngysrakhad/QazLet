package com.a4devspirit.a1.qazlet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Literature extends AppCompatActivity {
    TextView description;
    ImageView photo;
    ListView list_works;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    DatabaseReference data = FirebaseDatabase.getInstance().getReference();
    String poet;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_literature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        poet = getIntent().getStringExtra("data");
        setTitle(poet);
        description = (TextView)findViewById(R.id.poet_description);
        photo = (ImageView)findViewById(R.id.poet_photo);
        list_works = (ListView)findViewById(R.id.list_works);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
        list_works.setAdapter(adapter);
        data.child("literature").child(poet).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                description.setText(dataSnapshot.child("description").getValue(String.class));
                url = dataSnapshot.child("photo").getValue(String.class);
                Picasso.with(Literature.this)
                        .load(url)
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                        .into(photo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        data.child("literature").child(poet).child("works").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                list.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        list_works.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String s = adapterView.getItemAtPosition(i).toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(Literature.this);
                builder.setTitle("Алфавит")
                        .setMessage("Выберите алфавит")
                        .setCancelable(true)
                        .setPositiveButton("Латинский",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Literature.this, Poems.class);
                                        intent.putExtra("poem",s);
                                        intent.putExtra("data", poet);
                                        intent.putExtra("version","latin");
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Кириллица",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Literature.this, Poems.class);
                                        intent.putExtra("data", poet);
                                        intent.putExtra("poem",s);
                                        intent.putExtra("version","cyrillic");
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
