package com.example.healthapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthapp.Model.User;
import com.example.healthapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllAppUsersActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private RecyclerView allDoctorsRecyclerView;
    private TextView allDoctorsCount;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_app_users);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("All Users");

        allDoctorsCount = findViewById(R.id.allDoctorsCount);

        allDoctorsRecyclerView = findViewById(R.id.allDoctorsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //allInvestorsRecyclerView.setHasFixedSize(true);
        allDoctorsRecyclerView.setLayoutManager(layoutManager);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numberOfUsers = snapshot.getChildrenCount();
                int noOfInvestors = (int) numberOfUsers;
                allDoctorsCount.setText("Total Users: " +noOfInvestors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllAppUsersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(usersRef, User.class).build();

        FirebaseRecyclerAdapter<User, MyViewHolder> adapter = new FirebaseRecyclerAdapter<User, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull User model) {

                holder.userName.setText(model.getName());
                holder.userEmail.setText(model.getEmail());
                holder.userTypeBtn.setText(model.getType());
                Glide.with(getApplicationContext()).load(model.getProfilepictureurl()).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String userid = getRef(position).getKey();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userid);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String usertype = snapshot.child("type").getValue(String.class);
                                Intent intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
                                intent.putExtra("userid", userid);
                                intent.putExtra("usertype", usertype);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AllAppUsersActivity.this).inflate(R.layout.all_users_display_layout,parent,false);
                return new MyViewHolder(view);
            }
        };

        allDoctorsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userEmail;
        CircleImageView profileImage;
        Button userTypeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userEmail = itemView.findViewById(R.id.user_email);
            userTypeBtn = itemView.findViewById(R.id.userTypeBtn);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}