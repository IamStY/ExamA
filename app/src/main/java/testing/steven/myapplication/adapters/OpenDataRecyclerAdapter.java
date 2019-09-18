package testing.steven.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import testing.steven.myapplication.R;
import testing.steven.myapplication.datamodels.OpenDataModel;

public class OpenDataRecyclerAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<OpenDataModel> dataModelArrayList = new ArrayList<>();
    public OpenDataRecyclerAdapter (   ArrayList<OpenDataModel> dataModelArrayList ) {
        this.dataModelArrayList.addAll(dataModelArrayList);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View cellView = LayoutInflater.from(context).inflate(R.layout.adapter_items, parent, false);
        return new ViewHolder(cellView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder  ;
        TextView tv_places =   myHolder.tv_places  ;
        OpenDataModel openDataModel = dataModelArrayList.get(position);
        if(openDataModel!=null){
            tv_places.setText(openDataModel.getAnimal_place()+"");
        }
    }
    public void setDataArrayList(ArrayList<OpenDataModel> openDataModels) {
        this.dataModelArrayList.clear();
        this.dataModelArrayList.addAll(openDataModels);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() { return dataModelArrayList.size();}
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_places;
        TextView tv_sub_id;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_places = (TextView)itemView.findViewById(R.id.tv_places);

        }

    }
}
