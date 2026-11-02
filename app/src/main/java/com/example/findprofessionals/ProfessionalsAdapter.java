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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfessionalsAdapter extends RecyclerView.Adapter<ProfessionalsAdapter.ViewHolder> {

    private final ArrayList<User> professionals;
    private StorageReference storageReference;

    public ProfessionalsAdapter(ArrayList<User> professionals) {
        this.professionals = new ArrayList<>(professionals);
        storageReference = FirebaseStorage.getInstance().getReference("profile_img");
    }

    @NonNull
    @Override
    public ProfessionalsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_professional, parent, false);

        return new ProfessionalsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProfessionalsAdapter.ViewHolder holder, int position) {
        User professional = professionals.get(position);

        /// /// ///  Init data of professional on card
        // set image
        storageReference.child(professional.getUID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext()).load(uri).into(holder.getProfessionalImage());
            }
        });

        holder.getProfessionalName().setText(professional.getName() + " " + professional.getLastName());
        double averageScore = professional.getScoreTimes() == 0 ? 0.0 : (double) professional.getScore() / professional.getScoreTimes();
        String ratingFormatted = String.format(java.util.Locale.US, "%.1f", averageScore);

        holder.getProfessionalRating().setText(
                "★ " + ratingFormatted + " - " + professional.getScoreTimes() + " reviews"
        );

        holder.getProfessionalCategories().setText(professional.getTags());
        holder.getProfessionalAddress().setText(professional.getAddress());
        holder.getPostAuthorPhone().setText(professional.getPhone());

        holder.itemView.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return professionals.size();
    }

    public void updateData(ArrayList<User> newProfessionals) {
        professionals.clear();
        professionals.addAll(newProfessionals);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView professionalImage;
        private TextView professionalName;
        private TextView professionalRating;
        private TextView professionalCategories;
        private TextView professionalAddress;

        private TextView postAuthorPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            professionalImage = itemView.findViewById(R.id.professionalImage);
            professionalName = itemView.findViewById(R.id.professionalName);
            professionalRating = itemView.findViewById(R.id.professionalRating);
            professionalCategories = itemView.findViewById(R.id.professionalCategories);
            professionalAddress = itemView.findViewById(R.id.professionalAddress);
            postAuthorPhone = itemView.findViewById(R.id.postAuthorPhone);
        }

        public TextView getPostAuthorPhone() {
            return postAuthorPhone;
        }

        public void setPostAuthorPhone(TextView postAuthorPhone) {
            this.postAuthorPhone = postAuthorPhone;
        }

        public CircleImageView getProfessionalImage() {
            return professionalImage;
        }

        public void setProfessionalImage(CircleImageView professionalImage) {
            this.professionalImage = professionalImage;
        }

        public TextView getProfessionalName() {
            return professionalName;
        }

        public void setProfessionalName(TextView professionalName) {
            this.professionalName = professionalName;
        }

        public TextView getProfessionalRating() {
            return professionalRating;
        }

        public void setProfessionalRating(TextView professionalRating) {
            this.professionalRating = professionalRating;
        }

        public TextView getProfessionalCategories() {
            return professionalCategories;
        }

        public void setProfessionalCategories(TextView professionalCategories) {
            this.professionalCategories = professionalCategories;
        }

        public TextView getProfessionalAddress() {
            return professionalAddress;
        }

        public void setProfessionalAddress(TextView professionalAddress) {
            this.professionalAddress = professionalAddress;
        }
    }
}
