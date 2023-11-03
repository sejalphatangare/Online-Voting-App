package com.example.onlinevotingapp2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinevotingapp2.Activities.AllCandidateActivity;
import com.example.onlinevotingapp2.Activities.ResultActivity;
import com.example.onlinevotingapp2.Activities.ResultActivityElection;
import com.example.onlinevotingapp2.Model.Election;
import com.example.onlinevotingapp2.R;

import java.util.List;

import javax.xml.transform.Result;

public class ElectionResultAdapter extends RecyclerView.Adapter<ElectionResultAdapter.ViewHolder>{

    private Context context;
    private List<Election> electionList;
    public ElectionResultAdapter(Context context, List<Election> electionList) {
        this.context = context;
        this.electionList = electionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.election_result_layout, parent, false);
        return new ElectionResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.electionNameTextView.setText(electionList.get(position).getElectionName());
        holder.startDateTextView.setText(electionList.get(position).getStartDate());
        holder.endDateTextView.setText(electionList.get(position).getEndDate());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ResultActivity.class);
                intent.putExtra("ele_name",electionList.get(position).getElectionName());
                intent.putExtra("start_date",electionList.get(position).getStartDate());
                intent.putExtra("end_date",electionList.get(position).getEndDate());
                intent.putExtra("electionId",electionList.get(position).getId());

                context.startActivity(intent);
//                Activity activity=(Activity) context;
//                activity.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return electionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView electionNameTextView;
        private TextView startDateTextView;
        private TextView endDateTextView;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            electionNameTextView = itemView.findViewById(R.id.electionNameTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);
            cardView=itemView.findViewById(R.id.electionCardView);
        }
    }
}
