package com.example.task_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Adapter.RecyclerViewClickListner listner;
    ArrayList<RecycleViewClass>  list;

   // private String name = getIntent().getStringExtra("name");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recycle_list1);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               list = new ArrayList<>();
                for (DataSnapshot image : snapshot.getChildren()) {

                    list.add(image.getValue(RecycleViewClass.class));
                }


                Adapter adapter = new Adapter(list,getApplicationContext(),listner);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity2.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        listner=new Adapter.RecyclerViewClickListner() {
            @Override
            public void onClick(View v, int position) {

                int s=Integer.parseInt(list.get(position).getStatus());
                if(s == 0){

                    DatabaseReference d = FirebaseDatabase.getInstance().getReference().child(list.get(position).getName());
                    d.child("status").setValue("1");
                    d.child("likes").setValue(String.valueOf(Integer.parseInt(list.get(position).getLikes())+1));

                  // Toast.makeText(MainActivity2.this,"done",Toast.LENGTH_SHORT).show();
                }
                else{
                    DatabaseReference d = FirebaseDatabase.getInstance().getReference().child(list.get(position).getName());
                    d.child("status").setValue("0");
                    d.child("likes").setValue(String.valueOf(Integer.parseInt(list.get(position).getLikes())-1));
                }


            }
        };
    }
}