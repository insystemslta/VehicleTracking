package mz.co.insystems.trackingservice.activities;

import android.app.NotificationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;


import com.android.volley.toolbox.ImageLoader;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.model.CommandResponse;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.sync.NetworkController;
import mz.co.insystems.trackingservice.sync.Url;

public class VehicleAlarmImage extends AppCompatActivity {
    private Vehicle vehicle = new Vehicle();
    private CommandResponse response = new CommandResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_alarm_image);

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        response = (CommandResponse) getIntent().getSerializableExtra("vehicleResponse");

        ImageView image = (ImageView) findViewById(R.id.image);

        ImageLoader imageLoader = NetworkController.getInstance().getImageLoader();
        imageLoader.get(Url.VEHICLE_IMAGE_GET + vehicle.getImage(), ImageLoader.getImageListener(
                image, R.mipmap.ic_launcher, R.mipmap.ic_launcher));


    }

}

