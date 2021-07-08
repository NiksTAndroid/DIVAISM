package com.prometteur.divaism.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.divaism.Activities.RequestFormActivity;
import com.prometteur.divaism.Activities.StylistInformationActivity;
import com.prometteur.divaism.databinding.RecycleBodiesIconsBinding;
import com.prometteur.divaism.databinding.RecyclerStylistListBinding;

import java.util.ArrayList;

public class BodyTypeListAdapter extends RecyclerView.Adapter<BodyTypeListAdapter.ViewHolder> {
    Context nContext;
    ArrayList<Uri> imguris;
    int index=-1;

    public BodyTypeListAdapter(Context nContext,ArrayList<Uri> imguris ) {
        this.nContext = nContext;
        this.imguris=imguris;
    }

    @NonNull
    @Override
    public BodyTypeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RecycleBodiesIconsBinding.inflate(((Activity) nContext).getLayoutInflater(),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BodyTypeListAdapter.ViewHolder holder, int position) {

        holder.binding.ivBodyIcon.setImageURI(imguris.get(position));

        if(RequestFormActivity.setSelectedBodyType()>=0){
            index=RequestFormActivity.setSelectedBodyType();
            //notifyDataSetChanged();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index=position;
                RequestFormActivity.getSelectedBodyType(index);
                notifyDataSetChanged();
            }
        });
        if(index==position){
            holder.binding.ivBodyIcon.getLayoutParams().height=140;
            holder.binding.ivBodyIcon.getLayoutParams().width=80;

        }else{
            holder.binding.ivBodyIcon.getLayoutParams().height=100;
            holder.binding.ivBodyIcon.getLayoutParams().width=60;

        }

    }

    @Override
    public int getItemCount() {
        return imguris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecycleBodiesIconsBinding binding;
        public ViewHolder(@NonNull RecycleBodiesIconsBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
    }
}
