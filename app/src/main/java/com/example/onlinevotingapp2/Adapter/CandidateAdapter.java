package com.example.onlinevotingapp2.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinevotingapp2.Activities.AllCandidateActivity;
import com.example.onlinevotingapp2.Activities.VotingActivity;
import com.example.onlinevotingapp2.Model.Candidate;
import com.example.onlinevotingapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {

    private Context context;
    private List<Candidate> list;
    private FirebaseFirestore firebaseFirestore;
    private String candname;
    private String candid;

    public CandidateAdapter(Context context, List<Candidate> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.candidate_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.party.setText(list.get(position).getParty());
        holder.ele_name.setText(list.get(position).getEle_name());

        candname=list.get(position).getName();
        Glide.with(context).load(list.get(position).getImage()).into(holder.image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, VotingActivity.class);
                intent.putExtra("name",list.get(position).getName());
                intent.putExtra("party",list.get(position).getParty());
                intent.putExtra("image",list.get(position).getImage());
                intent.putExtra("id",list.get(position).getId());
                intent.putExtra("ele_name",list.get(position).getEle_name());

                context.startActivity(intent);
//                Activity activity=(Activity) context;
//                activity.finish();
            }
        });

        firebaseFirestore=FirebaseFirestore.getInstance();



    }

    private void loadCandidateForElection(String electionId) {
        if (electionId != null) {
            firebaseFirestore.collection("Candidate")
                    .whereEqualTo("name", candname)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                                    candid=snapshot.getId();
                                }
                            }else {
                                Toast.makeText(context.getApplicationContext(), "Id not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView image;
        private TextView name,party,ele_name;
        private CardView cardView;
        //        private Button voteBtn;
        private Button updateBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);
            party=itemView.findViewById(R.id.party);
//            voteBtn=itemView.findViewById(R.id.vote_btn);
            cardView=itemView.findViewById(R.id.card_view);
            ele_name=itemView.findViewById(R.id.ele_name);
            updateBtn=itemView.findViewById(R.id.update_btn);
            deleteBtn=itemView.findViewById(R.id.delete_btn);

        }
    }
}