package com.prometteur.divaism.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.divaism.Activities.HomePage;
import com.prometteur.divaism.Activities.StylistInformationActivity;
import com.prometteur.divaism.PojoModels.StylistListResponse;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.databinding.RecyclerStylistListBinding;

import java.util.List;

import static com.prometteur.divaism.Utils.CommonMethods.loadImage;

public class StylistListAdapter extends RecyclerView.Adapter<StylistListAdapter.ViewHolder> {
    Context nContext;
    String userType;
    List<StylistListResponse.Result> stylistList;
    public StylistListAdapter(Context nContext,String userType,List<StylistListResponse.Result> stylistList) {
        this.nContext = nContext;
        this.userType = userType;
        this.stylistList = stylistList;
    }

    @NonNull
    @Override
    public StylistListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RecyclerStylistListBinding.inflate(((Activity) nContext).getLayoutInflater(),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StylistListAdapter.ViewHolder holder, int position) {

        loadImage(holder.binding.civStylistImage, stylistList.get(position).getUserImgurl());

        holder.binding.civStylistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nContext.startActivity(new Intent(nContext, StylistInformationActivity.class)
                        .putExtra(ConstantStrings.usertype,userType)
                        .putExtra(ConstantStrings.stylistId,stylistList.get(position).getUserId())
                .putExtra("DATA",stylistList.get(position)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return stylistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerStylistListBinding binding;
        public ViewHolder(@NonNull RecyclerStylistListBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
    }
}
