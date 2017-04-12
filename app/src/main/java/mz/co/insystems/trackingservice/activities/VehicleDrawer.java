package mz.co.insystems.trackingservice.activities;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.adapter.CustomDrawerAdapter;
import mz.co.insystems.trackingservice.dao.CommandDAO;
import mz.co.insystems.trackingservice.dao.OwnerDAO;
import mz.co.insystems.trackingservice.dao.PlanDAO;
import mz.co.insystems.trackingservice.dao.VehicleDAO;
import mz.co.insystems.trackingservice.model.Command;
import mz.co.insystems.trackingservice.model.Owner;
import mz.co.insystems.trackingservice.model.User;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.sync.NetworkController;
import mz.co.insystems.trackingservice.sync.Url;

public class VehicleDrawer extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    List<Command> dataList;

    private VehicleDAO vehicleDao;
    private OwnerDAO ownerDao;
    private PlanDAO planDAO;
    private CommandDAO commandDAO;
    private Vehicle vehicle;
    private User user;

    CustomDrawerAdapter adapter;

    private static String 	SPACE = " ";
    private List<EditText> 	editTexts;
    private String commando = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_drawer);

         /*
		 * Setting vehicle and user data
		 */

        vehicle = new Vehicle();
        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");

        user = new User();
        user = (User) getIntent().getSerializableExtra("user");



        Bundle data = new Bundle();
        data.putSerializable("vehicle", vehicle);
        data.putSerializable("user", user);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setArguments(data);
        mTitle = getTitle();

        // Set up the drawer.


        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        vehicleDao 	= new VehicleDAO(this);
        ownerDao 	= new OwnerDAO(this);
        planDAO     = new PlanDAO(this);
        commandDAO 	= new CommandDAO(this);
        ownerDao	= new OwnerDAO(this);

        commandDAO.open();
        vehicleDao.open();
        planDAO.open();
        ownerDao.open();



        ImageView vehicleImage  = (ImageView) findViewById(R.id.imageVehicle_details);
        TextView makeModel 		= (TextView)  findViewById(R.id.textMake_model_details);
        TextView textOwner 		= (TextView)  findViewById(R.id.textOwner_details);
        TextView nrPlate 		= (TextView)  findViewById(R.id.textNr_plate_details);
        TextView IMEI 			= (TextView)  findViewById(R.id.textIMEI_details);




        ImageLoader imageLoader = NetworkController.getInstance().getImageLoader();
        imageLoader.get(Url.VEHICLE_IMAGE_GET + vehicle.getImage(), ImageLoader.getImageListener(
                vehicleImage, R.mipmap.ico_loading, R.mipmap.ic_launcher));

        Owner owner = ownerDao.get(vehicle.getOwner().getId(), this);
        makeModel.setText(vehicle.getMake()+" - "+ vehicle.getModel());
        String ownerSurname = (owner.getSurname()) != null? owner.getSurname() : null;
        textOwner.setText(owner.getName()+" "+ ownerSurname);
        nrPlate.setText(vehicle.getNrPlate());
        IMEI.setText(vehicle.getIMEI());


    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_vehicle_drawer, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((VehicleDrawer) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    protected void onPause() {
        commandDAO.close();
        vehicleDao.close();
        planDAO.close();
        ownerDao.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        commandDAO.open();
        vehicleDao.open();
        planDAO.open();
        ownerDao.open();
        super.onResume();
    }

}
