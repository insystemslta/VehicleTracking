package mz.co.insystems.trackingservice.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;


import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.sync.NetworkController;
import mz.co.insystems.trackingservice.sync.Url;
import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.util.CircleTransform;

/**
 * Created by voloide on 9/16/16.
 */
public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.MyViewHolder> {

    private List<Vehicle> vehicleList;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView marca, matricula, imei;
        public ImageView vehicle_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            marca = (TextView) itemView.findViewById(R.id.vehicle_list_marca_modelo);
            matricula = (TextView) itemView.findViewById(R.id.vehicle_list_number_plate);
            imei = (TextView) itemView.findViewById(R.id.vehicle_list_imei);
            vehicle_image = (ImageView) itemView.findViewById(R.id.vehicle_list_image);
        }
    }

    public VehicleListAdapter(List<Vehicle> vehicleList, Activity activity) {
        this.vehicleList = vehicleList;
        this.activity = activity;
    }

    @Override
    public VehicleListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VehicleListAdapter.MyViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.marca.setText(vehicle.getMake() +" "+ vehicle.getModel());
        holder.matricula.setText(vehicle.getNrPlate());
        holder.imei.setText(vehicle.getIMEI());

        String url = Url.VEHICLE_IMAGE_GET + vehicle.getImage();

       /* ImageLoader imageLoader = NetworkController.getInstance().getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(
                holder.vehicle_image, R.mipmap.ic_launcher, R.mipmap.ic_launcher));*/


        // Loading profile image
        Glide.with(this.activity).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this.activity))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.vehicle_image);
    }

    @Override
    public int getItemCount() {
        if (vehicleList != null) return vehicleList.size();
        return 0;
    }
}
