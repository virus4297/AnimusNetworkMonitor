package com.example.animusnetworkmonitor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private int myDatasetcount;
    private ArrayList<String> ips = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private Context context;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public TextView ip,status;
        public View View;
        //public ImageView image;
        //public RelativeLayout recyclerLayout_iplist;
        public MyViewHolder(View v) {
            super(v);
            View=v;
//            image = v.findViewById(R.id.imageView_RecyclerView);
//            v.findViewById(R.id.textView1)ip=v.findViewById(R.id.textView1);
  //          status=v.findViewById(R.id.textView2);
//            recyclerLayout_iplist=itemView.findViewById(R.id.ip_RecyclerView);

        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter( Context context,ArrayList<String> ips,ArrayList<String> desc,ArrayList<String> status,ArrayList<String> images,int myDataset) {
    //public MyAdapter(String[] myDataset) {
        myDatasetcount=myDataset;
        this.desc=desc;
        this.status=status;
        this.context=context;
        this.images=images;
        this.ips=ips;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView st=holder.View.findViewById(R.id.textView_status);
        if (status.get(position).equals("UP"))
            st.setBackgroundColor(Color.GREEN);
        else
            st.setBackgroundColor(Color.RED);
        TextView d=holder.View.findViewById(R.id.textView_description);
                d.setText(desc.get(position));
        TextView i=holder.View.findViewById(R.id.textView_ip);
        i.setText(ips.get(position));

        ImageView p =holder.View.findViewById(R.id.imageView_RecyclerView);
        Glide.with(context)
                //.asBitmap()
                .load(images.get(position))
                .into(p);

        holder.View.findViewById(R.id.Recycler_LinearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Please Don't Touch!!",Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myDatasetcount;
        //return ips.size();
    }
}
