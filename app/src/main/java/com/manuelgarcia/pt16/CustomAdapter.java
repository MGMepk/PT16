package com.manuelgarcia.pt16;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "tempAdapter";

    private List<Bloc> mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView data;
        private final TextView tempe;
        private final ImageView imatge;


        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            data = v.findViewById(R.id.firstLine);
            tempe = v.findViewById(R.id.secondLine);
            imatge = v.findViewById(R.id.icon);


        }

        public TextView getTextView() {
            // return textView;
            return null;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    CustomAdapter(List<Bloc> dataSet) {
        mDataSet = dataSet;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.example_item, viewGroup, false);
        Log.d("test", "onCreateViewHolder: ");

        return new ViewHolder(v);
    }

    private void remove(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        final Bloc name = mDataSet.get(position);
        holder.data.setText(name.getData());
        holder.data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });
        if (name.getImatge().equals("cold")) {
            {
            }

            holder.imatge.setImageResource(R.drawable.ic_action_cold);
        } else
            holder.imatge.setImageResource(R.drawable.ic_action_sun);

        String line = "Temp: " + name.getTempe() + "ªC, humidity:" + name.getHumidity();
        holder.tempe.setText(line);
        Log.d("test", "onBindViewHolderCA: " + position);

    }

    @Override
    public int getItemCount() {

        return mDataSet.size();
    }
}

