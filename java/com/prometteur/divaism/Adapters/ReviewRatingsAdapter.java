package com.prometteur.divaism.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.divaism.Activities.ClientStylingActivity;
import com.prometteur.divaism.PojoModels.ReviewRatingResponse;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.databinding.RecycleRatingreviewsBinding;

import java.util.List;

import static com.prometteur.divaism.Utils.ConstantStrings.stylist;

public class ReviewRatingsAdapter extends RecyclerView.Adapter<ReviewRatingsAdapter.ViewHolder> {
    private static final String TAG = "ReviewRatingsAdapter";
    Context nContext;
    String userType;
    List<ReviewRatingResponse.Result> responseData;
    String userID;

    public ReviewRatingsAdapter(Context nContext,String userType,String userID,List<ReviewRatingResponse.Result> responseData) {
        this.nContext = nContext;
        this.userType = userType;
        this.userID = userID;
        this.responseData = responseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RecycleRatingreviewsBinding.inflate(((Activity) nContext).getLayoutInflater(),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.tvUserName.setText(responseData.get(position).getUserName());
        holder.binding.tvComments.setText(responseData.get(position).getRevText());
        holder.binding.rbRating.setRating(Float.parseFloat(responseData.get(position).getRevRating()));
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equalsIgnoreCase(stylist) && userID.equalsIgnoreCase(ConstantStrings.SavedUserID)) {
                    nContext.startActivity(new Intent(nContext, ClientStylingActivity.class)
                            .putExtra(ConstantStrings.FormReplyID,responseData.get(position).getRepId())
                            .putExtra(ConstantStrings.stylistId,userID));


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return responseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RecycleRatingreviewsBinding binding;

        public ViewHolder(@NonNull RecycleRatingreviewsBinding itemView) {

            super(itemView.getRoot());
            binding=itemView;
        }
    }
}
