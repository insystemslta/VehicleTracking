package mz.co.insystems.trackingservice.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.adapter.VehicleListAdapter;
import mz.co.insystems.trackingservice.dao.UserDAO;
import mz.co.insystems.trackingservice.dao.VehicleDAO;
import mz.co.insystems.trackingservice.model.User;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.sync.InSystemsSyncService;
import mz.co.insystems.trackingservice.sync.Sync;

public class VehicleList extends AppCompatActivity {

    private List<Vehicle> vehicleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private VehicleListAdapter vehicleListAdapter;

    private VehicleDAO vehicleDao;
    private UserDAO userDAO;

    private SharedPreferences userData;

    private User user;

    protected ProgressDialog syncProgress;

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            if (message.arg1 == RESULT_OK) {
                if (syncProgress != null) syncProgress.dismiss();
                reloadVehicleList();
            }
        };
    };

    protected void reloadVehicleList() {
        vehicleList = vehicleDao.getAll(user);
        if (vehicleList != null) {
            vehicleListAdapter = new VehicleListAdapter(vehicleList, this);
            recyclerView.setAdapter(vehicleListAdapter);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_list);

        setupToolbar();

        user = new User();
        user = (User) getIntent().getSerializableExtra("user");

        vehicleDao = new VehicleDAO(this);
        userDAO = new UserDAO(this);
        vehicleDao.open();
        userDAO.open();

        if (user.getId() > 0) vehicleList = vehicleDao.getAll(user);

        if (vehicleList == null){
            try {
                syncProgress = ProgressDialog.show(VehicleList.this, "",
                        getString(R.string.vehicle_data_sync_in_progress));
                syncProgress.setCancelable(false);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        startSyncService(Sync.FIRST_SYNC);
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            startSyncService(Sync.UPDATE_CHECK);
        }

        recyclerView = (RecyclerView) findViewById(R.id.vehicle_recycle_list);

        vehicleListAdapter = new VehicleListAdapter(vehicleList, this);

        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.colorPrimary, 1));
        recyclerView.setAdapter(vehicleListAdapter);



        recyclerView.addOnItemTouchListener(new VehicleRecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Vehicle vehicle = vehicleList.get(position);

                if (vehicle.isActive() || user.getType().isAdmin()) {
                    Intent it = new Intent(VehicleList.this, VehicleDetails.class);
                    it.putExtra("vehicle", vehicle);
                    it.putExtra("user", user);
                    startActivity(it);
                }else {
                    Toast.makeText(VehicleList.this,
                            getString(R.string.vehicle_disabled),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        toolbar.bringToFront();


        ActionBar actionbar = getSupportActionBar();
        actionbar.setIcon(R.mipmap.ic_launcher);
    }

    private void startSyncService(int syncType) {
        Intent insystemsSyncService = new Intent(VehicleList.this, InSystemsSyncService.class);
        Messenger messenger = new Messenger(handler);
        insystemsSyncService.putExtra("messenger", messenger);
        insystemsSyncService.putExtra("user", user);
        insystemsSyncService.putExtra("syncType", syncType);
        startService(insystemsSyncService);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void closeDAOS() {
        if (vehicleDao.isOpened()) vehicleDao.close();
        if (userDAO.isOpened()) userDAO.close();
    }

    @Override
    protected void onResume() {
        if (user == null) {
            Intent it = new Intent(VehicleList.this, LoginActivity.class);
            startActivity(it);
        }
        reOpenDAOS();
        super.onResume();
    }

    private void reOpenDAOS() {
        if (!vehicleDao.isOpened()) vehicleDao.open();
        if (!userDAO.isOpened()) userDAO.open();
    }

    private void openDAOS() {
        vehicleDao.open();
        userDAO.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vehicle_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.botao_sair) {
            sair();
        }else if (item.getItemId() == R.id.botao_about) {
            Intent aboutIntent = new Intent(VehicleList.this, About.class);
            startActivity(aboutIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sair() {
        Intent it = new Intent();
        it.setClass(this, LoginActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(it);
        finish();
    }

    @Override
    protected void onDestroy() {
        destroyUser();
        closeDAOS();
        super.onDestroy();
    }

    private void destroyUser() {
        user = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class VehicleRecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private VehicleList.ClickListener clickListener;

        public VehicleRecyclerTouchListener(Context context, final RecyclerView recyclerView, final VehicleList.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}