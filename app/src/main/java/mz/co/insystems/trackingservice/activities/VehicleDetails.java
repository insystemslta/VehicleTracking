package mz.co.insystems.trackingservice.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.dao.CommandDAO;
import mz.co.insystems.trackingservice.dao.OwnerDAO;
import mz.co.insystems.trackingservice.dao.PlanDAO;
import mz.co.insystems.trackingservice.dao.PlanItemDAO;
import mz.co.insystems.trackingservice.dao.VehicleDAO;
import mz.co.insystems.trackingservice.model.Command;
import mz.co.insystems.trackingservice.model.Owner;
import mz.co.insystems.trackingservice.model.Plan;
import mz.co.insystems.trackingservice.model.PlanItem;
import mz.co.insystems.trackingservice.model.User;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.sync.NetworkController;
import mz.co.insystems.trackingservice.sync.Url;
import mz.co.insystems.trackingservice.util.AppConfig;
import mz.co.insystems.trackingservice.util.CommandPartType;

public class VehicleDetails extends AppCompatActivity{


    private VehicleDAO  vehicleDao;
    private OwnerDAO    ownerDao;
    private PlanDAO     planDAO;
    private CommandDAO  commandDAO;

    private Vehicle vehicle;
    private User    user;
    private Command command;

    private DrawerLayout            mDrawerLayout;
    private ActionBarDrawerToggle   mDrawerToggle;
    private NavigationView          mNavigationView;

    private static String   space = " ";
    private List<EditText> 	editTexts;
    private String          commando = "";

    private static final String TAG = "VehicleDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_command);

        /*
		 * Setting vehicle and user data from Intent
		 */

        vehicle = new Vehicle();
        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");

        user = new User();
        user = (User) getIntent().getSerializableExtra("user");

        prepareDataSource();
        setupToolbar();
        initNavigationDrawer();


        ImageView vehicleImage  = (ImageView) findViewById(R.id.imageVehicle_details);
        TextView makeModel 		= (TextView)  findViewById(R.id.textMake_model_details);
        TextView textOwner 		= (TextView)  findViewById(R.id.textOwner_details);
        TextView nrPlate 		= (TextView)  findViewById(R.id.textNr_plate_details);
        TextView IMEI 			= (TextView)  findViewById(R.id.textIMEI_details);


        /*
		 * loading vehicle image from web
         */

        ImageLoader imageLoader = NetworkController.getInstance().getImageLoader();
        imageLoader.get(Url.VEHICLE_IMAGE_GET + vehicle.getImage(), ImageLoader.getImageListener(
                vehicleImage, R.mipmap.ico_loading, R.drawable.tracking_icon_hr));
        ownerDao.getAll(getApplicationContext());

        Owner owner = ownerDao.get(vehicle.getOwner().getId(), this);
        makeModel.setText(vehicle.getMake()+" - "+ vehicle.getModel());
        String ownerSurname = (owner.getSurname() != null) ? owner.getSurname() : null;
        textOwner.setText(owner.getName()+" "+ ownerSurname);
        nrPlate.setText(vehicle.getNrPlate());
        IMEI.setText(vehicle.getIMEI());

    }

    private void prepareDataSource() {
        vehicleDao 	= new VehicleDAO(this);
        ownerDao 	= new OwnerDAO(this);
        planDAO = new PlanDAO(this);
        commandDAO 	= new CommandDAO(this);
        ownerDao	= new OwnerDAO(this);

        commandDAO.open();
        vehicleDao.open();
        planDAO.open();
        ownerDao.open();
    }

    @NonNull
    private Toolbar setupToolbar() {
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        toolbar.bringToFront();


        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        return toolbar;
    }

    private void initNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        setupActionBarDrawerToogle();
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    /**
     * In case if you require to handle drawer open and close states
     */
    private void setupActionBarDrawerToogle() {

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                Snackbar.make(view, R.string.drawer_close, Snackbar.LENGTH_SHORT).show();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                Snackbar.make(drawerView, R.string.drawer_open, Snackbar.LENGTH_SHORT).show();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void setupDrawerContent(NavigationView navigationView) {

        addItemsRunTime(navigationView);

        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //menuItem.setChecked(true);

                        command = commandDAO.get(menuItem.getItemId());
                        if (command.getCommandParts().get(0).getPart().equalsIgnoreCase("call")) {
                            beginCall(vehicle.getCallNumber());
                        }else{
                            openPasswordDialog(command);
                        }
                        //Log.d(TAG, "place 1 - Selected Command ID " + menuItem.getItemId());
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    private void addItemsRunTime(NavigationView navigationView) {

        //loading basic plan commands
        List<Command> basicCommandList = loadVehicleBasicItems();

        //loading extra items commands
        List<Command> extraCommandList = null;
        if (vehicleHasExtraItem()) extraCommandList = loadVehicleExtraItems();

        //loading ADMIN commands
        List<Command> adminCommandList = null;
        if (user.getType().isAdmin()) adminCommandList = loadAdminCommands();

        //adding basic commands to the menu
        final Menu menu = navigationView.getMenu();



        final SubMenu basicCommandsSubMenu = menu.addSubMenu(R.string.menu_basic_item_section);
        int s = 0;
        do {
            Log.d(TAG, "loading basic command " + s);
            Resources resources = getResources();
            final int iconId = resources.getIdentifier(basicCommandList.get(s).getIcon(), "mipmap", getPackageName());
            basicCommandsSubMenu.add(Menu.NONE, basicCommandList.get(s).getId(), Menu.NONE, basicCommandList.get(s).getName())
                .setIcon(getResources().getDrawable(iconId));
            s++;
        } while (s < basicCommandList.size());


        if (extraCommandList != null) {
            if (extraCommandList.size() > 0) {
                // adding menu_extra_item_section  and items into it
                final SubMenu extraItemSubMenu = menu.addSubMenu(getString(R.string.menu_extra_item_section));
                s = 0;
                do {
                    Log.d(TAG, "loading extra command " + s);
                    Resources resources = getResources();
                    final int iconId = resources.getIdentifier(extraCommandList.get(s).getIcon(), "mipmap", getPackageName());
                    extraItemSubMenu.add(Menu.NONE, extraCommandList.get(s).getId(), Menu.NONE, extraCommandList.get(s).getName())
                            .setIcon(getResources().getDrawable(iconId));
                    s++;
                } while (s < extraCommandList.size());
            }
        }
        if (adminCommandList != null) {
            // adding menu_admin_item_section  and items into it
            final SubMenu adminItemSubMenu = menu.addSubMenu(getString(R.string.menu_admin_item_section));
            s = 0;
            do {
                Log.d(TAG, "loading admin command " + s);
                Resources resources = getResources();
                final int iconId = resources.getIdentifier(adminCommandList.get(s).getIcon(), "mipmap", getPackageName());
                adminItemSubMenu.add(Menu.NONE, adminCommandList.get(s).getId(), Menu.NONE, adminCommandList.get(s).getName())
                        .setIcon(getResources().getDrawable(iconId));
                s++;
            } while (s < adminCommandList.size());
        }

        // refreshing navigation drawer adapter
        for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++) {
            final View child = mNavigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }



    private boolean vehicleHasExtraItem(){
        return vehicleDao.hasExtraItem(vehicle.getId());
    }

    private List<Command> loadAdminCommands() {
        Plan p = new Plan();
        p.setId(1000);
        Plan adminPlan = planDAO.getLoadedPlan(p, this, false);

        List<Command> adminCommandList = new ArrayList<>();

        for (int i = 0; i < adminPlan.getPlanItemList().size(); i++) {
            for (int j = 0; j < adminPlan.getPlanItemList().get(i).getCommandList().size(); j++) {
                if (adminPlan.getPlanItemList().get(i).getCommandList().get(j).getTarget() == user.APP_ADMIN) {
                    adminCommandList.add(adminPlan.getPlanItemList().get(i).getCommandList().get(j));
                }
            }
        }

        return adminCommandList;
    }

    private List<Command> loadVehicleExtraItems() {
        List<PlanItem> extraItemList = new ArrayList<PlanItem>();
        PlanItemDAO itemDao = new PlanItemDAO(this);
        itemDao.open();
        PlanItem item;

        for (int i = 0; i < vehicle.getExtraPlanItems().size(); i++) {
            item = itemDao.get(vehicle.getExtraPlanItems().get(i));
            extraItemList.add(item);
        }

        List<Command> extraCommandList = new ArrayList<>();

        for (int k = 0; k < extraItemList.size(); k++) {
            if (commandDAO.planItemHasCommands(extraItemList.get(k).getCode())){
                extraCommandList.addAll(commandDAO.getCommandByItemCode(extraItemList.get(k).getCode()));
            }
        }
        itemDao.close();

        return extraCommandList;
    }

    private List<Command> loadVehicleBasicItems() {
        Plan plan = planDAO.getLoadedPlan(vehicle.getPlan(), this, false);

        List<Command> basicCommandList = new ArrayList<>();

        for (int i = 0; i < plan.getPlanItemList().size(); i++) {
            for (int j = 0; j < plan.getPlanItemList().get(i).getCommandList().size(); j++) {
                if (plan.getPlanItemList().get(i).getCommandList().get(j).getTarget() == user.APP_USER) {
                    basicCommandList.add(plan.getPlanItemList().get(i).getCommandList().get(j));
                }
            }
        }

        return basicCommandList;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    private void beginCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+ number));
        if (AppConfig.checkPermitionForOperation(Manifest.permission.CALL_PHONE, getApplicationContext()))
            startActivity(intent);
        else
            AppConfig.requestMissingPermition(Manifest.permission.CALL_PHONE, AppConfig.MY_PERMISSIONS_REQUEST_CALL_PHONE, this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (AppConfig.requestPermission(requestCode, permissions, grantResults)){
            case AppConfig.MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (command.getCode().equals("SIM_RECHARGE")) {
                    beginCall(commando);
                }else{
                    beginCall(vehicle.getCallNumber());
                }
                break;
            case AppConfig.MY_PERMISSIONS_REQUEST_SEND_SMS: sendCommandBySms();
                break;
            default:
                if (permissions[0].equals(Manifest.permission.CALL_PHONE))
                    Toast.makeText(this, getString(R.string.call_permission_denied), Toast.LENGTH_LONG).show();
                if (permissions[0].equals(Manifest.permission.SEND_SMS))
                    Toast.makeText(this, getString(R.string.send_sms_permission_denied), Toast.LENGTH_LONG).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    private void sendCommandBySms() {
        try {
            if (command.getCode().equals("PASSWORD_CHANGE")) vehicleDao.updatePassword(vehicle);
            sendSmsByManager(commando.trim(), vehicle.getCallNumber(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void sendSmsByManager(String message, String phoneNumber, Context conext) {
        String phoneNo = phoneNumber;
        String msg = message;
        try {

            String SENT = "sent";
            String DELIVERED = "delivered";

            Intent sentIntent = new Intent(SENT);
			/*Create Pending Intents*/
            PendingIntent sentPI = PendingIntent.getBroadcast(
                    conext, 0, sentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent deliveryIntent = new Intent(DELIVERED);

            PendingIntent deliverPI = PendingIntent.getBroadcast(
                    conext, 0, deliveryIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
			/* Register for SMS send action */
            registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String result = "";

                    switch (getResultCode()) {

                        case Activity.RESULT_OK:
                            result = "Comando enviado com sucesso";
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            result = "Fallha ao enviar comando, tente novamente";
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            result = "Sem rede móvel";
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            result = "No PDU defined";
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            result = "Sem serviço";
                            break;
                    }

                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_SHORT).show();
                }

            }, new IntentFilter(SENT));
			/* Register for Delivery event */
            registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.command_in_progress), Toast.LENGTH_SHORT).show();
                }
            }, new IntentFilter(DELIVERED));

			/*Send SMS*/
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, sentPI,
                    deliverPI);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),
                    ex.getMessage().toString(), Toast.LENGTH_LONG)
                    .show();
            ex.printStackTrace();
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

    private boolean isToGenerateField(String partType) {
        if (partType.equals(CommandPartType.PASSWORD))  return true;
        if (partType.equals(CommandPartType.NUMBER))    return true;
        if (partType.equals(CommandPartType.TEXT))      return true;
        if (partType.equals(CommandPartType.TIME))      return true;
        return false;
    }


    private void generateFields(final Command command, LinearLayout layout, ViewGroup.LayoutParams layoutParams, int i) {
        EditText valuePartInput = new EditText(this);
        valuePartInput.setLayoutParams(layoutParams);
        valuePartInput.setId(command.getCommandParts().get(i).getId());
        valuePartInput.setPadding(getPadding(10), getPadding(10), getPadding(10), getPadding(10));
        valuePartInput.setTextSize(18);
        valuePartInput.setBackgroundResource(R.drawable.edit_text_bottom_border);
        valuePartInput.setTextAppearance(this, R.style.login_edit_text);
        valuePartInput.setHint(command.getCommandParts().get(i).getPart());

        if (command.getCommandParts().get(i).getType().equals(CommandPartType.PASSWORD)) {
            valuePartInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            valuePartInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        if (command.getCommandParts().get(i).getType().equals(CommandPartType.NUMBER) || command.getCommandParts().get(i).getType().equals(CommandPartType.TIME)) {
            valuePartInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        layout.addView(valuePartInput);
        editTexts.add(valuePartInput);
    }

    private int getPadding(int padding){
        final float scale = getResources().getDisplayMetrics().density;
        int paddingInDp = (int) (padding * scale + 0.5f);
        return paddingInDp;
    }

    private void openPasswordDialog(final Command command){
        final Dialog dialog = new Dialog(VehicleDetails.this);

        //CREATING THE DIALOG LAYOUT
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(getPadding(10), getPadding(10), getPadding(10), getPadding(10));
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTexts = new ArrayList<EditText>();

        for (int i = 0; i < command.getCommandParts().size(); i++) {
            if (isToGenerateField(command.getCommandParts().get(i).getType())) {
                generateFields(command, layout, layoutParams, i);
            }
        }

        LinearLayout sendCommandsLayout = new LinearLayout(this);
        sendCommandsLayout.setOrientation(LinearLayout.HORIZONTAL);
        sendCommandsLayout.setPadding(getPadding(10), getPadding(10), getPadding(10), getPadding(10));
        sendCommandsLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewGroup.LayoutParams sendCommandsLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams sendLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView enviarTextView= new TextView(this);
        enviarTextView.setLayoutParams(sendCommandsLayoutParams);
        enviarTextView.setText(getString(R.string.action_command_send));
        enviarTextView.setPadding(getPadding(0), getPadding(10), getPadding(10), getPadding(10));
        enviarTextView.setGravity(Gravity.RIGHT);
        //passwordTextView.setTextSize(20);
        sendCommandsLayout.addView(enviarTextView);


        TextView cancelTextView= new TextView(this);
        cancelTextView.setLayoutParams(sendCommandsLayoutParams);
        cancelTextView.setText(getString(R.string.action_command_cancel));
        cancelTextView.setPadding(getPadding(10), getPadding(10), getPadding(10), getPadding(10));
        cancelTextView.setGravity(Gravity.RIGHT);
        sendCommandsLayout.addView(cancelTextView);


        layout.addView(sendCommandsLayout, sendLayoutParams);

        //setting custom layout to dialog
        dialog.setContentView(layout);
        dialog.setTitle(command.getName());
        dialog.getWindow().setTitleColor(getResources().getColor(R.color.dialog_title));

        cancelTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        enviarTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String commando = generateCommand(command, dialog);
                if(command.getCode().equals("SIM_RECHARGE")){
                    beginCall(commando);
                }else {
                    if (AppConfig.checkPermitionForOperation(Manifest.permission.SEND_SMS, getApplicationContext()))
                        sendCommandBySms();
                    else
                        AppConfig.requestMissingPermition(Manifest.permission.SEND_SMS, AppConfig.MY_PERMISSIONS_REQUEST_SEND_SMS, VehicleDetails.this);

                }

                dialog.dismiss();
            }

            /**
             * @param command
             * @param dialog
             * @return
             */
            private String generateCommand(final Command command, final Dialog dialog) {
                commando = "";
                for (int i = 0; i < command.getCommandParts().size(); i++) {

                    if (command.getCommandParts().get(i).getType().equals(CommandPartType.COMMAND) && command.getCommandParts().get(i).getPosition() == 1) commando = command.getCommandParts().get(i).getPart();
                    if (command.getCommandParts().get(i).getType().equals(CommandPartType.COMMAND) && command.getCommandParts().get(i).getPosition() > 1) commando += command.getCommandParts().get(i).getPart();
                    if (command.getCommandParts().get(i).getType().equals(CommandPartType.SAPCE)) commando += space;
                    if (command.getCommandParts().get(i).getType().equals(CommandPartType.SIMBOL)) commando += command.getCommandParts().get(i).getPart();
                    if (command.getCommandParts().get(i).getType().equals(CommandPartType.VEHICLE_NUMBER)) commando += vehicle.getCallNumber();

                    if (isToGenerateField(command.getCommandParts().get(i).getType())) {
                        for (int j = 0; j < editTexts.size(); j++) {
                            if (editTexts.get(j).getId() == command.getCommandParts().get(i).getId()) {
                                if (!editTexts.get(j).getText().toString().trim().equals("")) {
                                    commando += editTexts.get(j).getText().toString();
                                    if (command.getCode().equals("PASSWORD_CHANGE") && editTexts.get(j).getText().toString().length() == 6) {
                                         vehicle.setPassword(editTexts.get(1).getText().toString());
                                    } else if (command.getCode().equals("PASSWORD_CHANGE") && editTexts.get(j).getText().toString().length() != 6) {
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),
                                                getString(R.string.minimun_pass_length), Toast.LENGTH_LONG).show();
                                    }
                                }else {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),
                                            getString(R.string.required_filds_not_filled), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
                return commando;
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.command_menu_file, menu);
        return true;
    }
}
