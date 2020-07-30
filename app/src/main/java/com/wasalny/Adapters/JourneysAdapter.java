package com.wasalny.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wasalny.Activities.Driver.DisplayJourneyActivity;
import com.wasalny.Fonts.Comforta;
import com.wasalny.Model.Journey;
import com.wasalny.R;

import java.util.ArrayList;

public class JourneysAdapter extends RecyclerView.Adapter<JourneysAdapter.MyViewHolder>{

    ArrayList<Journey> journeys;
    Context context;
    Intent intent;
    String typeOfDiapLayedJourneys;

    private static final String TAG = "JourneysAdapter";

    public JourneysAdapter(ArrayList<Journey> journeys, Context context, String typeOfDiapLayedJourneys) {
        this.journeys = journeys;
        this.context = context;
        this.typeOfDiapLayedJourneys = typeOfDiapLayedJourneys;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.journey_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {

        holder.descriptionTV.setText(journeys.get(i).getDescription());
        holder.fromTV.setText("From : " +journeys.get(i).getFrom());
        holder.toTV.setText("To : " +journeys.get(i).getTo());
        holder.expectedBudgetTV.setText("Expected Budget : "+journeys.get(i).getExpectedBudget());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, DisplayJourneyActivity.class);
                intent.putExtra("journey",journeys.get(i));
                intent.putExtra("new",typeOfDiapLayedJourneys);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return journeys.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Comforta descriptionTV, fromTV, toTV, expectedBudgetTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            fromTV = itemView.findViewById(R.id.fromTV);
            toTV = itemView.findViewById(R.id.toTV);
            expectedBudgetTV = itemView.findViewById(R.id.expectedBudgetTV);
        }
    }
}
