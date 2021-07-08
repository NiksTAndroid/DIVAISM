package com.prometteur.divaism.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.divaism.Activities.ClientStylingActivity;
import com.prometteur.divaism.Activities.FilledRequestFormActivity;
import com.prometteur.divaism.Activities.StylistInformationActivity;
import com.prometteur.divaism.PojoModels.MessageResponse;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.databinding.RecyclerMessagerListBinding;
import com.prometteur.divaism.databinding.RecyclerStylistListBinding;

import java.util.List;

public class MessagerListAdapter extends RecyclerView.Adapter<MessagerListAdapter.ViewHolder> {
    Context nContext;
    String userType;
    List<MessageResponse.Result> mResponse;

    public MessagerListAdapter(Context nContext, String userType, List<MessageResponse.Result> mResponse) {
        this.nContext = nContext;
        this.userType = userType;
        this.mResponse=mResponse;

    }

    @NonNull
    @Override
    public MessagerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RecyclerMessagerListBinding.inflate(((Activity) nContext).getLayoutInflater(),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagerListAdapter.ViewHolder holder, int position) {

        holder.binding.tvMsgUserName.setText(mResponse.get(position).getUserName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equalsIgnoreCase(ConstantStrings.client)) {
                    nContext.startActivity(new Intent(nContext, ClientStylingActivity.class)
                            .putExtra(ConstantStrings.FormReplyID,mResponse.get(position).getReplyId())
                            .putExtra(ConstantStrings.stylistId,mResponse.get(position).getReqUserId()));
                }else{
                    /*if(mResponse.get(position).getReqCreateBy().equalsIgnoreCase(ConstantStrings.SavedUserID)|| mResponse.get(position).getReplyId()!=null){
                        nContext.startActivity(new Intent(nContext, ClientStylingActivity.class)
                                .putExtra(ConstantStrings.FormReplyID,mResponse.get(position).getReplyId())
                                .putExtra(ConstantStrings.stylistId,mResponse.get(position).getReqUserId()));
                    }else {
                        nContext.startActivity(new Intent(nContext, FilledRequestFormActivity.class)
                                .putExtra(ConstantStrings.usertype, userType)
                                .putExtra(ConstantStrings.toBeSaved, false)
                                .putExtra(ConstantStrings.FormId, mResponse.get(position).getReqId()));
                    }*/
                    nContext.startActivity(new Intent(nContext, FilledRequestFormActivity.class)
                            .putExtra(ConstantStrings.usertype, userType)
                            .putExtra(ConstantStrings.toBeSaved, false)
                            .putExtra(ConstantStrings.FormReplyID, mResponse.get(position).getReplyId())
                            .putExtra(ConstantStrings.FormId, mResponse.get(position).getReqId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResponse.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerMessagerListBinding binding;
        public ViewHolder(@NonNull RecyclerMessagerListBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
    }
}
