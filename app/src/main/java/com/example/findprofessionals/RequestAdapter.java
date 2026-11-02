package com.example.findprofessionals;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private final ArrayList<Request> requests;
    private StorageReference storageReference;

    public RequestAdapter(ArrayList<Request> requests) {
        this.requests = requests;
        storageReference = FirebaseStorage.getInstance().getReference("profile_img");

    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        Request request = requests.get(position);
        String writerID = request.getRID().split(":")[1];

        storageReference.child(writerID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext()).load(uri).into(holder.getPostAuthorImage());
            }
        });

        holder.getPostAuthorName().setText(request.getFullName());
        holder.getPostTitle().setText(request.getTitle());
        holder.getPostCategory().setText(request.getCategory());
        holder.getPostContent().setText(request.getDescription());
        holder.getPostAuthorPhone().setText(request.getPhone());


        holder.itemView.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView postAuthorImage;
        private TextView postAuthorName;
        private TextView postTitle;
        private TextView postCategory;
        private TextView postContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postAuthorImage = itemView.findViewById(R.id.postAuthorImage);
            postAuthorName = itemView.findViewById(R.id.postAuthorName);
            postTitle = itemView.findViewById(R.id.postTitle);
            postCategory = itemView.findViewById(R.id.postCategory);
            postContent = itemView.findViewById(R.id.postContent);
            postAuthorPhone = itemView.findViewById(R.id.postAuthorPhone);
        }

        public TextView getPostAuthorPhone() {
            return postAuthorPhone;
        }

        public void setPostAuthorPhone(TextView postAuthorPhone) {
            this.postAuthorPhone = postAuthorPhone;
        }

        private TextView postAuthorPhone;

        public CircleImageView getPostAuthorImage() {
            return postAuthorImage;
        }

        public void setPostAuthorImage(CircleImageView postAuthorImage) {
            this.postAuthorImage = postAuthorImage;
        }

        public TextView getPostAuthorName() {
            return postAuthorName;
        }

        public void setPostAuthorName(TextView postAuthorName) {
            this.postAuthorName = postAuthorName;
        }

        public TextView getPostTitle() {
            return postTitle;
        }

        public void setPostTitle(TextView postTitle) {
            this.postTitle = postTitle;
        }

        public TextView getPostCategory() {
            return postCategory;
        }

        public void setPostCategory(TextView postCategory) {
            this.postCategory = postCategory;
        }

        public TextView getPostContent() {
            return postContent;
        }

        public void setPostContent(TextView postContent) {
            this.postContent = postContent;
        }
    }

    public void updateData(ArrayList<Request> newPosts) {
        requests.clear();
        requests.addAll(newPosts);
        notifyDataSetChanged();
    }
}
