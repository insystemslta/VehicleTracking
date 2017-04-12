package mz.co.insystems.trackingservice.activities;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.util.HashMap;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.adapter.VehicleNotificationStateAdapter;
import mz.co.insystems.trackingservice.model.CommandResponse;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.sync.NetworkController;
import mz.co.insystems.trackingservice.sync.Url;
import mz.co.insystems.trackingservice.util.AlarmType;

public class VehicleStateActivity extends ListActivity {

    ImageView vehicleImage;
    private TextView stickyView;
    private View stickyViewSpacer;
    private Vehicle vehicle = new Vehicle();
    private CommandResponse response = new CommandResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_state);
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        response = (CommandResponse) getIntent().getSerializableExtra("vehicleResponse");

        HashMap<String, HashMap<String, String>> stateMap = new HashMap<>();

        stateMap.put("Portas", new HashMap<String, String>());
        stateMap.get("Portas").put((response.isDoor()) ? "Abertas" : "Fechadas", "ic_door");

        stateMap.put("Corrente", new HashMap<String, String>());
        stateMap.get("Corrente").put((response.isPower()) ? "Ligada" : "Desligada", "ic_power");


        if (isResponseFromState(response)) {
            stateMap.put("GPS", new HashMap<String, String>());
            stateMap.get("GPS").put((response.isGps()) ? "Ligado" : "Desligado", "ic_gps");
        }

        if (isResponseFromState(response)) {
            stateMap.put("GPRS", new HashMap<String, String>());
            stateMap.get("GPRS").put((response.isGprs()) ? "Ligado" : "Desligado", "ic_gprs");
        }

        stateMap.put("Ignição", new HashMap<String, String>());
        stateMap.get("Ignição").put((response.isAcc()) ? "Ligada" : "Desligada", "ic_ignition");

        if (isResponseFromState(response)) {
            stateMap.put("Estado do sistema", new HashMap<String, String>());
            stateMap.get("Estado do sistema").put((response.isArm()) ? "Armado" : "Desarmado", "ic_arm_state");
        }

        if (isResponseFromState(response)) {
            stateMap.put("Velocidade", new HashMap<String, String>());
            stateMap.get("Velocidade").put( String.valueOf(response.getSpeed())+"Km/h", "ic_speed");
        }

        if (isResponseFromState(response)) {
            stateMap.put("Nível da bateria", new HashMap<String, String>());
            stateMap.get("Nível da bateria").put(response.getBat(), "ic_battery");
        }

        if (isResponseFromState(response) && !response.getOil().contains("0.00%")) {
            stateMap.put("Nível de combustivel", new HashMap<String, String>());
            stateMap.get("Nível de combustivel").put(response.getOil(), "ic_oil");
        }
        if (isResponseFromState(response) && response.getOdo() > 0) {
            stateMap.put("Distância percorrida", new HashMap<String, String>());
            stateMap.get("Distância percorrida").put(String.valueOf(response.getOdo())+"Km", "ic_road");
        }

        vehicleImage = (ImageView) findViewById(R.id.imageVehicle_notification);
        stickyView = (TextView) findViewById(R.id.stickyView);

        //Setting number plate to the list header
        stickyView.setText(vehicle.getNrPlate());

        if (responseHasLocation(response)){
            stickyView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                    mapIntent.putExtra("vehicleResponse", response);
                    mapIntent.putExtra("vehicle", vehicle);
                    startActivity(mapIntent);
                }
            });
        }else {
            Toast.makeText(VehicleStateActivity.this,
                    getString(R.string.location_not_set),
                    Toast.LENGTH_LONG).show();
        }


		/* Inflate list header layout */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater.inflate(R.layout.list_header, null);
        stickyViewSpacer = listHeader.findViewById(R.id.stickyViewPlaceholder);

        getListView().addHeaderView(listHeader);

		/*
		 * loading vehicle image
         */
        ImageLoader imageLoader = NetworkController.getInstance().getImageLoader();
        imageLoader.get(Url.VEHICLE_IMAGE_GET + vehicle.getImage(), ImageLoader.getImageListener(
                vehicleImage, R.mipmap.ico_loading, R.mipmap.ic_launcher));

        VehicleNotificationStateAdapter myAdapter = new VehicleNotificationStateAdapter(stateMap, getApplicationContext());
        setListAdapter(myAdapter);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                //parallax(vehicleImage);
                if (getListView().getFirstVisiblePosition() == 0) {
                    View firstChild = getListView().getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }

                    int heroTopY = stickyViewSpacer.getTop();
                    stickyView.setY(Math.max(0, heroTopY + topY));

                    /* Set the image to scroll half of the amount that of ListView */
                    vehicleImage.setY(topY * 0.5f);
                }
            }
        });
    }

    private boolean responseHasLocation(CommandResponse response) {
        return (response.getLatitude() != 0 && response.getLongitude() != 0);
    }

    private boolean isResponseFromState(CommandResponse vehicleResponse) {
        return vehicleResponse.getHeader().contains(AlarmType.STATE_ALARM);
    }

}
