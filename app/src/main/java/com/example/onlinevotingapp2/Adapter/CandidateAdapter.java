package com.example.onlinevotingapp2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinevotingapp2.Model.Candidate;
import com.example.onlinevotingapp2.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {

    private Context context;
    private List<Candidate> list;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.party.setText(list.get(position).getParty());
        holder.position.setText(list.get(position).getPosition());

        Glide.with(context).load(list.get(position).getImage()).into(holder.image);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView image;
        private TextView name,position,party;
        private Button voteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);
            position=itemView.findViewById(R.id.post);
            party=itemView.findViewById(R.id.party);
            voteBtn=itemView.findViewById(R.id.vote_btn);
        }
    }
}
