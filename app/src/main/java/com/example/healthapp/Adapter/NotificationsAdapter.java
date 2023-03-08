package com.example.healthapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapp.Model.Notification;
import com.example.healthapp.Model.User;
import com.example.healthapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationsAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView notification_username, notification_comment, notification_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_username = itemView.findViewById(R.id.notification_username);
            notification_comment = itemView.findViewById(R.id.notification_comment);
            notification_date = itemView.findViewById(R.id.notification_date);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = mNotification.get(position);

        holder.notification_comment.setText(notification.getText());
        holder.notification_date.setText("On: "+notification.getDate());

        getUserInfo(holder.notification_username ,notification.getUserid());

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    private void getUserInfo(final TextView notification_username, String publisherid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                notification_username.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}
