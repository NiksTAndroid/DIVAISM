package com.prometteur.divaism.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.divaism.Activities.ClientStylingActivity;
import com.prometteur.divaism.Activities.FilledRequestFormActivity;
import com.prometteur.divaism.PojoModels.MessageResponse;
import com.prometteur.divaism.PojoModels.SavedFormDataPojo;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.databinding.RecyclerMessagerListBinding;

import java.io.Serializable;
import java.util.List;

import static com.prometteur.divaism.Utils.ConstantStrings.FORMDATA;

public class FormsListAdapter extends RecyclerView.Adapter<FormsListAdapter.ViewHolder> {
    Context nContext;
    List<SavedFormDataPojo> dataList;
    String userType;
    String STYLISTID;
    boolean sendform;
    public FormsListAdapter(Context nContext,List<SavedFormDataPojo> dataList,String userType,boolean sendform,String STYLISTID) {
        this.nContext = nContext;
        this.dataList=dataList;
        this.userType=userType;
        this.sendform=sendform;
        this.STYLISTID=STYLISTID;


    }

    @NonNull
    @Override
    public FormsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RecyclerMessagerListBinding.inflate(((Activity) nContext).getLayoutInflater(),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FormsListAdapter.ViewHolder holder, int position) {

        holder.binding.tvMsgUserName.setText("Form "+position);
        Log.e("Adapter", "onBindViewHolder: "+dataList.get(position).getId() );
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    nContext.startActivity(new Intent(nContext, FilledRequestFormActivity.class)
                            .putExtra(ConstantStrings.usertype,userType)
                            .putExtra(ConstantStrings.toBeSaved,sendform)
                            .putExtra(ConstantStrings.stylistId,STYLISTID)
                            .putExtra(ConstantStrings.FormId,String.valueOf(dataList.get(position).getId())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerMessagerListBinding binding;
        public ViewHolder(@NonNull RecyclerMessagerListBinding itemView) {
            super(itemView.getRoot());
            binding=itemView;
        }
    }

}
