package com.twyst.app.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twyst.app.android.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by tushar on 21/01/16.
 */
public class RedeemRVAdapter extends RecyclerView.Adapter<RedeemRVAdapter.RedeemViewHolder> {

    private List<CardItemRedeem> cardItemList;

    public RedeemRVAdapter(List<CardItemRedeem> cardItemList) {
        this.cardItemList = cardItemList;
    }

    public int getItemCount() {
        return cardItemList.size();
    }

    @Override
    public RedeemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.redeem_grid_item, parent, false);
        return new RedeemViewHolder(itemView);
    }

    /**
     * Bind all views to the items to be displayed
     *
     * @param viewHolder
     * @param i
     */
    public void onBindViewHolder(RedeemViewHolder viewHolder, int i) {
        final CardItemRedeem ci = cardItemList.get(i);
        viewHolder.itemText.setText(ci.getTextBelowImage());
        viewHolder.itemImage.setImageResource(ci.getImageId());
    }

    /**
     * View Holder Class for the adapter
     */
    public static class RedeemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemText;
        private ImageView itemImage;
        private View v;

        public RedeemViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            itemImage = (ImageView) v.findViewById(R.id.iv_redeem_grid);
            itemText = (TextView) v.findViewById(R.id.tv_redeem_grid);
        }

        @Override
        public void onClick(View view) {
            String text = ((TextView) view.findViewById(R.id.tv_redeem_grid)).getText().toString();
            Toast.makeText(view.getContext(), text, Toast.LENGTH_SHORT).show();
            // Log.d(TAG, "onClick " + getPosition() + " " + mItem);
            /*                switch (ci.getTextBelowImage()){
                    Toast.makeText(this, )
                    case "FOOD OFFERS":

                        break;
                    case "RECHARGE" :

                        break;
                    case "SHOPPING" :

                        break;

                    case "COMING SOON" :

                        break;
                }*/
        }
    }

    public static class CardItemRedeem {

        private String textBelowImage;
        private int imageId;

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        public String getTextBelowImage() {
            return textBelowImage;
        }

        public void setTextBelowImage(String textBelowImage) {
            this.textBelowImage = textBelowImage;
        }
    }
}
