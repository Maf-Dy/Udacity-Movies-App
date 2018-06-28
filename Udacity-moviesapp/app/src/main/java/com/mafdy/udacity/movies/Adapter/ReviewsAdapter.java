package com.mafdy.udacity.movies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import android.support.v7.widget.LinearLayoutManager;

import com.mafdy.udacity.movies.Object.MyReview;
import com.mafdy.udacity.movies.R;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<MyReview> modelList;


    public ReviewsAdapter(Context context, ArrayList<MyReview> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<MyReview> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_list_reviews, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final MyReview model = getItem(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;

            genericViewHolder.itemTxtAuthor.setText(model.getAuthor());
            genericViewHolder.itemTxtReview.setText(model.getContent());


        }
    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }


    private MyReview getItem(int position) {
        return modelList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUser;
        private TextView itemTxtAuthor;
        private TextView itemTxtReview;



        public ViewHolder(final View itemView) {
            super(itemView);


            this.imgUser = (ImageView) itemView.findViewById(R.id.img_user);
            this.itemTxtAuthor = (TextView) itemView.findViewById(R.id.item_txt_author);
            this.itemTxtReview = (TextView) itemView.findViewById(R.id.item_txt_review);


        }
    }

}

