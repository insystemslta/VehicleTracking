package mz.co.insystems.trackingservice.sync;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.co.insystems.trackingservice.dao.CommandDAO;
import mz.co.insystems.trackingservice.dao.ContactoDAO;
import mz.co.insystems.trackingservice.dao.OwnerDAO;
import mz.co.insystems.trackingservice.dao.PlanDAO;
import mz.co.insystems.trackingservice.dao.PlanItemDAO;
import mz.co.insystems.trackingservice.dao.UserDAO;
import mz.co.insystems.trackingservice.dao.VehicleDAO;
import mz.co.insystems.trackingservice.model.Command;
import mz.co.insystems.trackingservice.model.CommandPart;
import mz.co.insystems.trackingservice.model.CommandResponse;
import mz.co.insystems.trackingservice.model.Contacto;
import mz.co.insystems.trackingservice.model.Owner;
import mz.co.insystems.trackingservice.model.Plan;
import mz.co.insystems.trackingservice.model.PlanItem;
import mz.co.insystems.trackingservice.model.UpdateHeader;
import mz.co.insystems.trackingservice.model.User;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.model.type.OwnerType;
import mz.co.insystems.trackingservice.model.type.UserType;
import mz.co.insystems.trackingservice.model.type.VehicleType;
import mz.co.insystems.trackingservice.util.AppConfig;

/**
 * Created by voloide on 9/18/16.
 */
public class InSystemsSyncService extends IntentService {

    private static final String UPDATES = "updates";
    private static final int AVAILABLE = 1;
    private int result = Activity.RESULT_CANCELED;
    JSONArray thingsToSyncArray = null;
    private User user;
    private boolean updatesAvailable = false;

    // config JSONArray
    JSONObject planJsonObject;
    JSONObject itemJsonObject;
    JSONObject commandJsonObject;
    JSONObject vehicheJsonObject;
    JSONObject ownerJsonObject;
    JSONObject commandPartsJsonObject;
    JSONObject userJsonObject;
    JSONObject planItemRelationJsonObject;
    JSONObject vehicleExtraPlanItemJsonObject;
    JSONObject updateHeaderJsonObject;
    JSONObject commandResponseJsonObject;
    JSONObject contactJsonObject;

    // JSON SYNC RESULT
    private static final String SYNC_RESULT             = "success";
    private static final int SUCCESS                    = 1;
    private static final int FAILED                     = 0;
    private static final String TAG                     = "InSystemsSyncService";

    // Intent extras
    private static final String SYNC_TYPE_TAG           = "syncType";
    private static final String INTENT_USER             = "user";
    private int SYNC_TYPE                               = 0;

    private static final String JSON_OBJECT_REQUEST_TAG = "json_obj_req";

    // JSON Node names
    private static final String PLANS                   = "plan";
    private static final String PLAN_ITEMS              = "items";
    private static final String COMMANDS                = "commands";
    private static final String PARTS                   = "parts";
    private static final String VEHICLES 				= "vehicles";
    private static final String OWNERS 					= "owner";
    private static final String PLAN_ITEMS_RELATION     = "packageItems";
    private static final String VEHICLE_EXTRA_ITEM 		= "extraPlanItems";
    private static final String UPDATE_HEADERS 			= "update_headers";
    private static final String USER                    = "user";
    private static final String COMMAND_RESPONSE 		= "commandResponses";
    private static final String CONTACT          		= "contacto";

    private boolean syncResult = false;
    private boolean isValid = false;
    private Intent serviceIntent;
    private boolean updated = false;

    public InSystemsSyncService() {
        super("InSystemsSyncService");
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        this.serviceIntent = intent;

        SYNC_TYPE = intent.getIntExtra(SYNC_TYPE_TAG, 0);
        Log.d(TAG, "SYNC TYPE == "+SYNC_TYPE);
        setUser(intent);
        result = Activity.RESULT_CANCELED;
        if (isNetworkAvailable()) {
            try {
                switch (SYNC_TYPE) {
                    case Sync.FIRST_USER_SYNC:
                        getUserDataToSync();
                        break;
                    case Sync.FIRST_SYNC:
                        getDataForFirstSync();
                        break;
                    case Sync.USER_CREATION:
                        if (createUser(intent)) 		result = Activity.RESULT_OK;
                        break;
                    case Sync.UPDATE_CHECK:
                        checkAndPerformUpdate();
                        break;
                    case Sync.PASSWORD_RESET:
                        resetUserPassword(intent);
                        break;

                    default: result = Activity.RESULT_CANCELED;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param intent
     */
    private void setUser(Intent intent) {
            user = new User();
            user = (User) intent.getSerializableExtra(INTENT_USER);
    }

    private void resetUserPassword(final Intent intent) {
        // TODO Auto-generated method stub
        // Tag used to cancel the request
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url.USER_PASSWORD_RESET_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(NetworkController.TAG, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(NetworkController.TAG, "Error: " + error.getMessage());
            }
        }){

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("apiKey", AppConfig.AIP_KEY);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contact", intent.getStringExtra("contact"));
                return params;
            }

        };

        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }



    private List<String> genarateUrlParam(){
        List<String> baseParams = new ArrayList<String>();
        baseParams.add(user.getLoginNumber());
        if (user.getPassword() != null) baseParams.add(user.getPassword());
        baseParams.add(AppConfig.AIP_KEY);
        return baseParams;
    }


    /**
     * @param updateHeader
     */
    private void  update(UpdateHeader updateHeader) {
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_VEHICLE)) 			updateVehicle(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_OWNER)) 				updateOwner(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_COMMAND)) 			updateCommad(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_COMMAND_PARTS)) 		updateCommandPart(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_ITEM)) 				updateItem(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_PLAN)) 			    updatePackage(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_PLAN_ITEM)) 		    updatePackageItem(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_USER)) 				updateUser(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_USER_VEHICLE)) 		updateUserVehicle(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_VEHICLE_EXTRA_ITEM)) updateVehicleExtraItem(updateHeader);
        if (updateHeader.getTable().equalsIgnoreCase(Sync.SYNC_COMMAND_RESPONSE)) 	updateCommandResponse(updateHeader);

    }

    private void updateCommandResponse(final UpdateHeader updateHeader) {
        String url = createUrlFromParams(Url.COMMAND_RESPONSE, genarateUrlParam());
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == 1)
                        if (syncCommandResponse(jsonObject.getJSONObject(COMMAND_RESPONSE), updateHeader.getSyncType()))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void updateUserVehicle(UpdateHeader updateHeader) {

        // TODO Auto-generated method stub

    }


    private void setHeaderAsUpdated(final UpdateHeader updateHeader) {

        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;

        String url = Url.SET_UPDATE_AS_APLLYED;



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sync_id", String.valueOf(updateHeader.getId()));
                params.put("vehicle_id",  String.valueOf(updateHeader.getVehicle().getId()));
                params.put("user_id", String.valueOf(user.getId()));
                params.put("api_key", AppConfig.AIP_KEY);

                return params;
            }

        };

        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void updateVehicleExtraItem(final UpdateHeader updateHeader) {
        String url = createUrlFromParams(Url.VEHICLE_EXTRA_ITEM_GET, genarateUrlParam());

        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == SUCCESS)
                        if (syncVehicleExtraItems(jsonObject.getJSONArray(VEHICLE_EXTRA_ITEM), updateHeader.getSyncType()))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }


    private void updatePackageItem(final UpdateHeader updateHeader) {

        String url = createUrlFromParams(Url.PACKAGE_ITEM_GET, genarateUrlParam());

        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == SUCCESS)
                        if (syncPackageItems(jsonObject.getJSONObject(PLAN_ITEMS_RELATION), updateHeader.getSyncType()))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }


    private void updateUser(final UpdateHeader updateHeader) {

        String url = createUrlFromParams(Url.USER_GET_BY_CREDENTIALS, genarateUrlParam());
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == 1)
                        if (syncUser(jsonObject.getJSONObject(USER), updateHeader.getSyncType()))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void updatePackage(final UpdateHeader updateHeader) {
        String url = createUrlFromParams(Url.PACKAGE_GET, genarateUrlParam());
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == 1)
                        if (syncPlan(jsonObject.getJSONObject(PLANS), updateHeader.getSyncType()))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void updateCommandPart(final UpdateHeader updateHeader) {
        String url = createUrlFromParams(Url.COMMAND_PARTS_GET, genarateUrlParam());
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == 1)
                       if (syncCommandParts(jsonObject.getJSONObject(PARTS), updateHeader.getSyncType()))
                           setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void updateItem(final UpdateHeader updateHeader) {
        String url = createUrlFromParams(Url.ITEM_GET, genarateUrlParam());
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == 1)
                        if (syncItems(jsonObject.getJSONObject(PLAN_ITEMS), updateHeader.getSyncType()))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void updateCommad(final UpdateHeader updateHeader) {
        String url = createUrlFromParams(Url.COMMAND_GET, genarateUrlParam());
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == SUCCESS)
                       if (syncCommands(jsonObject.getJSONObject(COMMANDS), updateHeader.getSyncType()))
                           setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }



    private void updateOwner(final UpdateHeader updateHeader) {
        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(user.getId()));
        params.add(AppConfig.AIP_KEY);

        String url = createUrlFromParams(Url.OWNER_GET, params);
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == 1)
                        if (syncOwner(jsonObject.getJSONArray(OWNERS), updateHeader.getSyncType()))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);

    }

    /**
     * @param updateHeader
     * @return
     */
    private void updateVehicle(final UpdateHeader updateHeader) {

        List<String> params = new ArrayList<String>();
        params.add(String.valueOf(updateHeader.getVehicle().getId()));
        params.add(AppConfig.AIP_KEY);

        String url = createUrlFromParams(Url.VEHICLE_GET, params);
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == SUCCESS)
                        if (syncVehicle(jsonObject.getJSONObject(VEHICLES), updateHeader.getSyncType(), user))
                            setHeaderAsUpdated(updateHeader);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });
        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    private void checkAndPerformUpdate() {
        String url = createUrlFromParams(Url.CHECK_FOR_UPDATES, genarateUrlParam());

        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.d(TAG, "Cheking for updates....");
                    if (jsonObject.getInt(SYNC_RESULT) == SUCCESS){
                        if (jsonObject.getInt(UPDATES) == AVAILABLE) {

                            String url = createUrlFromParams(Url.GET_UPDATE_HEADERS, genarateUrlParam());
                            String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
                            final List<UpdateHeader> headers = new ArrayList<UpdateHeader>();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {
                                        if (jsonObject.getInt(SYNC_RESULT) == SUCCESS){
                                            updateHeaderJsonObject = jsonObject.getJSONObject(UPDATE_HEADERS);
                                            for (int i = 0; i < updateHeaderJsonObject.length(); i++) {
                                                JSONObject upHeaderJsonObject = updateHeaderJsonObject.getJSONObject(String.valueOf(i));
                                                UpdateHeader header = new UpdateHeader();
                                                header.setId(upHeaderJsonObject.getLong("id"));
                                                header.setVehicle(new Vehicle(upHeaderJsonObject.getLong("vehicle_id")));
                                                header.setTable(upHeaderJsonObject.getString("table"));
                                                header.setSync(upHeaderJsonObject.getInt("sync"));
                                                header.setSyncType(upHeaderJsonObject.getInt("sync_type"));
                                                header.setTarget(upHeaderJsonObject.getInt("target_id"));
                                                headers.add(header);
                                            }

                                            for (UpdateHeader updateHeader : headers) {
                                                update(updateHeader);
                                            }
                                            result = Activity.RESULT_OK;
                                            sendResult(serviceIntent);
                                        }

                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                                }
                            });
                            // Adding request to request queue
                            NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });

        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);

    }

    private boolean createUser(Intent intent) {
        final boolean[] userIsCreated = {false};
        final User[] user = {new User()};
        user[0].setName(intent.getStringExtra("name"));
        user[0].setPassword(intent.getStringExtra("password"), true);
        user[0].setContacto(new Contacto(intent.getLongExtra("contact_id", 0), intent.getStringExtra("email"),intent.getStringExtra("contact"),null));
        user[0].setActive(false);
        user[0].setType(new UserType(intent.getIntExtra("type", 1)));

        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;

        String url = Url.USER_CREATE_URL;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response != null) {
                                Log.i("Insystems", response.getString("message"));
                                int success = response.getInt(SYNC_RESULT);

                                if (success == 1) {
                                    user[0].setId(response.getInt("new_user_id"));

                                    UserDAO userDao = new UserDAO(getApplicationContext());
                                    userDao.open();
                                    userDao.create(user[0],getApplicationContext());
                                    userDao.close();

                                    userIsCreated[0] = true;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Set has updated", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", user[0].getName());
                params.put("surname", user[0].getSurname());
                params.put("login_number", user[0].getLoginNumber());
                params.put("password", user[0].getPassword());
                params.put("contact_id", String.valueOf(user[0].getContacto().getId()));
                params.put("type_id", String.valueOf(user[0].getType().getId()));

                return params;
            }

        };

        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        return userIsCreated[0];
    }

    /**
     * @param intent
     */
    private void sendResult(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Messenger messenger = (Messenger)extras.get("messenger");
            Message msg = Message.obtain();
            msg.arg1 = result;
            try {
                messenger.send(msg);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }

        }
    }

    private void getDataForFirstSync() {
        // Tag used to cancel the request
        String tag_json_obj = JSON_OBJECT_REQUEST_TAG;

        List<String> params = new ArrayList<String>();
        params.add(user.getLoginNumber());
        params.add(user.getPassword());
        params.add(AppConfig.AIP_KEY);

        String url = createUrlFromParams(Url.SYNC_ALL, params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(SYNC_RESULT) == SUCCESS){
                        Log.d(TAG, "Loading JsonObjects...");

                        itemJsonObject                  = jsonObject.getJSONObject(PLAN_ITEMS);
                        commandJsonObject               = jsonObject.getJSONObject(COMMANDS);
                        commandPartsJsonObject          = jsonObject.getJSONObject(PARTS);
                        vehicheJsonObject               = jsonObject.getJSONObject(VEHICLES);
                        planItemRelationJsonObject      = jsonObject.getJSONObject(PLAN_ITEMS_RELATION);
                        commandResponseJsonObject       = jsonObject.getJSONObject(COMMAND_RESPONSE);


                        try {
                            Log.d(TAG, "Starting sync...");
                            if (user.getType().isAdmin()){
                                Log.d(TAG, "Doing Admin plan sync...");
                                planJsonObject = jsonObject.getJSONObject(PLANS);
                                syncPlan(planJsonObject, SYNC_TYPE);
                            }
                            if (syncItems(itemJsonObject, SYNC_TYPE)
                                    && syncCommands(commandJsonObject, SYNC_TYPE)
                                    && syncCommandParts(commandPartsJsonObject, SYNC_TYPE)
                                    && syncCommandResponse(commandResponseJsonObject, SYNC_TYPE)
                                    && syncVehicle(vehicheJsonObject, SYNC_TYPE, user)
                                    && syncPackageItems(planItemRelationJsonObject, SYNC_TYPE))
                                syncResult = true;

                            if (syncResult) result = Activity.RESULT_OK;
                            sendResult(serviceIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });

        // Adding request to request queue
        NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }



    public static String createUrlFromParams(String baseUrl, List<String> params) {
        String url = baseUrl;
        for (String p : params){
            url += "/"+p;
        }
        return url;
    }


    private boolean syncCommandResponse(JSONObject jsonObject, int syncType) {
        boolean synced = false;
        try {
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject responseObject = jsonObject.getJSONObject(String.valueOf(i));
                CommandResponse response = new CommandResponse();
                response.setId(responseObject.getInt("id"));
                response.setHeader(responseObject.getString("header"));
                response.setTitle(responseObject.getString("title"));
                response.setDescription(responseObject.getString("description"));

                CommandDAO commandDao = new CommandDAO(getApplicationContext());
                commandDao.open();

                if (syncType == Sync.FIRST_SYNC) {
                        commandDao.addCommamndResponse(response);
                    synced = true;
                }else if (syncType == Sync.UPDATE_SYNC) {
                    commandDao.updateCommamndResponse(response);
                    synced = true;
                }else {
                    throw new IllegalArgumentException("INCORRECT SYNCTYPE");
                }
                commandDao.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;
    }

    private boolean syncVehicleExtraItems(JSONArray vehicheExtraArray, int syncType) {
        boolean synced = false;
        try {
            if (vehicheExtraArray.length() > 0) {
                // looping through All Items
                for (int i = 0; i < vehicheExtraArray.length(); i++) {
                    JSONObject c = vehicheExtraArray.getJSONObject(i);

                    VehicleDAO vehicleDao = new VehicleDAO(getApplicationContext());
                    vehicleDao.open();

                    if (syncType == Sync.FIRST_SYNC) {
                        vehicleDao.loadExtraItem(c.getInt("id"), c.getInt("vehicle_id"), c.getInt("item_id"));
                        synced = true;
                    }else if (syncType == Sync.UPDATE_SYNC) {
                        vehicleDao.updateExtraItem(c.getInt("id"), c.getInt("vehicle_id"), c.getInt("item_id"));
                        synced = true;
                    }else {
                        throw new Exception("INCORRECT SYNCTYPE");
                    }
                    vehicleDao.close();

                }
            }	else {
                synced = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;

    }

    private boolean syncOwner(JSONArray ownerArray, int syncType) {
        boolean synced = false;
        try {
            for (int i = 0; i < ownerArray.length(); i++) {
                JSONObject c = ownerArray.getJSONObject(i);
                Owner owner = new Owner();
                owner.setId(c.getLong("_id"));
                owner.setName(c.getString("name"));
                owner.setSurname(c.getString("surname"));
                owner.setContacto(new Contacto(c.getInt("contact_id")));
                owner.setType(new OwnerType(c.getInt("type")));

                OwnerDAO ownerDao = new OwnerDAO(getApplicationContext());
                ownerDao.open();

                if (syncType == Sync.FIRST_SYNC) {
                    if (!ownerDao .exists(owner)) {
                        ownerDao.create(owner, getApplicationContext());
                        synced = true;
                    }else {
                        ownerDao.update(owner, getApplicationContext());
                        synced = true;
                    }
                }else if (syncType == Sync.UPDATE_SYNC) {
                    ownerDao.update(owner, getApplicationContext());
                    synced = true;
                }else {
                    throw new Exception("INCORRECT SYNCTYPE");
                }
                ownerDao.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;

    }

    private boolean syncCommandParts(JSONObject jsonObject, int syncType) {
        boolean synced = false;

        try {
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject partObject = jsonObject.getJSONObject(String.valueOf(i));
                CommandPart part = new CommandPart();
                part.setId(partObject.getInt("id"));
                part.setPart(partObject.getString("part"));
                part.setPosition(partObject.getInt("position"));
                part.setType(partObject.getString("type"));

                Command cmd = new Command();
                cmd.setId(partObject.getInt("command_id"));

                CommandDAO commandDao = new CommandDAO(getApplicationContext());
                commandDao.open();

                if (syncType == Sync.FIRST_SYNC) {
                    commandDao.addCommamndPart(part, cmd);
                    synced   = true;
                }else if (syncType == Sync.UPDATE_SYNC) {
                    commandDao.updateCommamndPart(part, cmd);
                    synced = true;
                }else {
                    throw new IllegalArgumentException("INCORRECT SYNCTYPE");
                }
                commandDao.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced ;


    }

    private boolean syncCommands(JSONObject jsonObject, int syncType) {

        boolean synced = false;
        try {
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject cmdObject = jsonObject.getJSONObject(String.valueOf(i));
                Command cmd = new Command();
                cmd.setId(cmdObject.getInt("id"));
                cmd.setName(cmdObject.getString("name"));
                cmd.setCode(cmdObject.getString("code"));
                cmd.setIcon(cmdObject.getString("icon"));
                cmd.setTarget(cmdObject.getInt("user_type_target"));
                PlanItem item = new PlanItem();
                item.setCode(cmdObject.getString("item_code"));

                CommandDAO commandDao = new CommandDAO(getApplicationContext());
                commandDao.open();

                if (syncType == Sync.FIRST_SYNC) {
                    commandDao.addICommand(cmd, item);
                    synced   = true;
                }else if (syncType == Sync.UPDATE_SYNC) {
                    commandDao.update(cmd);
                    synced = true;
                }else {
                    throw new IllegalArgumentException("INCORRECT SYNCTYPE");
                }
                commandDao.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;

    }

    private boolean syncPackageItems(JSONObject planItemjsonObject, int syncType) {
        boolean synced = false;
        try {
            for (int i = 0; i < planItemjsonObject.length(); i++) {
                JSONObject jsonObject = planItemjsonObject.getJSONObject(String.valueOf(i));
                PlanDAO packageDao = new PlanDAO(getApplicationContext());
                packageDao.open();

                if (syncType == Sync.FIRST_SYNC) {
                    packageDao.loadPlans(jsonObject.getInt("id"), jsonObject.getInt("package_id"), jsonObject.getInt("item_id"), getApplicationContext());
                    synced    = true;
                }else if (syncType == Sync.UPDATE_SYNC) {
                    packageDao.updatePackageItem(jsonObject.getInt("id"), jsonObject.getInt("package_id"), jsonObject.getInt("item_id"), getApplicationContext());
                    synced = true;
                }else {
                    throw new IllegalArgumentException("INCORRECT SYNCTYPE");
                }
                packageDao.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return synced;

    }

    private boolean syncItems(JSONObject jsonObject, int syncType) {
        boolean synced = false;

        try {
            for (int i = 0; i < jsonObject.length(); i++) {
                PlanItem item = new PlanItem();
                JSONObject itemObject = jsonObject.getJSONObject(String.valueOf(i));
                item.setId(itemObject.getInt("id"));
                item.setName(itemObject.getString("name"));
                item.setCode(itemObject.getString("code"));

                PlanItemDAO itemDao = new PlanItemDAO(getApplicationContext());
                itemDao.open();

                if (syncType == Sync.FIRST_SYNC) {
                    itemDao .create(item);
                    synced   = true;
                }else if (syncType == Sync.UPDATE_SYNC) {
                    itemDao.update(item);
                    synced = true;
                }else {
                    throw new IllegalArgumentException("incorrect syncType");
                }
                itemDao.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;


    }

    private boolean syncPlan(JSONObject planJsonObject, int syncType) {
        boolean synced = false;
        try {
            for (int i = 0; i < planJsonObject.length(); i++) {
                //JSONObject planJsonObject = jsonObject.getJSONObject(String.valueOf(i));

                Plan plan = new Plan();
                plan.setId(planJsonObject.getLong("id"));
                plan.setName(planJsonObject.getString("name"));

                PlanDAO planDAO = new PlanDAO(getApplicationContext());
                planDAO.open();

                if (syncType == Sync.FIRST_SYNC) {
                    planDAO.create(plan);
                    synced    = true;
                }else if (syncType == Sync.UPDATE_SYNC) {
                    planDAO.update(plan);
                    synced = true;
                }else {
                    throw new IllegalArgumentException("INCORRECT SYNCTYPE");
                }
                planDAO.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;
    }

    private boolean syncVehicle(JSONObject vehicheJsonObject, int syncType, User user) {
        boolean synced = false;
        try {
            for (int i = 0; i < vehicheJsonObject.length(); i++) {
                JSONObject vehicleObject = vehicheJsonObject.getJSONObject(String.valueOf(i));
                Vehicle vehicle = new Vehicle();
                vehicle.setId(vehicleObject.getInt("id"));
                vehicle.setMake(vehicleObject.getString("make"));
                vehicle.setModel(vehicleObject.getString("model"));
                vehicle.setNrPlate(vehicleObject.getString("nr_plate"));
                vehicle.setIMEI(vehicleObject.getString("emai"));
                vehicle.setOwner(new Owner());

                //get owner node
                JSONObject ownerJsonObject = vehicleObject.getJSONObject(OWNERS);
                for (int j = 0;j < ownerJsonObject.length(); j++){
                    vehicle.getOwner().setId(ownerJsonObject.getInt("id"));
                    vehicle.getOwner().setName(ownerJsonObject.getString("name"));
                    vehicle.getOwner().setSurname(ownerJsonObject.getString("surname"));
                    vehicle.getOwner().setType(new OwnerType(ownerJsonObject.getInt("type")));

                    if (ownerJsonObject.has(CONTACT)) {
                        JSONObject contactoJsonObject = ownerJsonObject.getJSONObject(CONTACT);

                        vehicle.getOwner().setContacto(new Contacto());
                        for (int k = 0; k < contactoJsonObject.length(); k++) {
                            vehicle.getOwner().getContacto().setId(contactoJsonObject.getInt("id"));
                            vehicle.getOwner().getContacto().setEmail(contactoJsonObject.getString("email"));
                            vehicle.getOwner().getContacto().setTelefone_1(contactoJsonObject.getString("telefone_1"));
                            vehicle.getOwner().getContacto().setTelefone_2(contactoJsonObject.getString("telefone_2"));
                        }
                    }
                }

                //get plan node
                JSONObject planJsonObject = vehicleObject.getJSONObject(PLANS);
                Plan plan = new Plan();
                for (int k = 0;k < planJsonObject.length(); k++){
                    plan.setId(planJsonObject.getInt("id"));
                    plan.setName(planJsonObject.getString("name"));
                }

                List<PlanItem> planItemList = new ArrayList<>();
                try {
                    if (vehicleObject.has(VEHICLE_EXTRA_ITEM)) {
                        //get extraPlanItems node
                        JSONObject extraPlanItemsJsonObject = vehicleObject.getJSONObject(VEHICLE_EXTRA_ITEM);
                        for (int k = 0; k < extraPlanItemsJsonObject.length(); k++) {
                            JSONObject extraPlnItemJsonObject = extraPlanItemsJsonObject.getJSONObject(String.valueOf(k));
                            PlanItem planItem = new PlanItem(extraPlnItemJsonObject.getInt("item_id"));
                            planItemList.add(planItem);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!planItemList.isEmpty())
                    vehicle.setExtraPlanItems(planItemList);

                vehicle.setPlan(plan);
                if (!vehicleObject.getString("registration_date").equalsIgnoreCase(null))
                    vehicle.setRegistrationDate(vehicleObject.getString("registration_date"));
                vehicle.setCallNumber(vehicleObject.getString("call_number"));
                vehicle.setType(new VehicleType(vehicleObject.getInt("type")));
                vehicle.setImage(vehicleObject.getString("imageUri"));
                vehicle.setActive((vehicleObject.getInt("state") == 1));

                VehicleDAO vehicleDao = new VehicleDAO(getApplicationContext());
                vehicleDao.open();


                if (syncType == Sync.FIRST_SYNC) {
                    if (!vehicleDao .checkIfVehicleExistOnDevice(vehicle)) {
                        vehicleDao.create(vehicle, user, getApplicationContext());
                        synced  = true;
                    }else {
                        vehicleDao.update(vehicle);
                        synced = true;
                    }
                }else if (syncType == Sync.UPDATE_SYNC) {
                    vehicleDao.update(vehicle);
                    synced = true;
                }else {
                    throw new IllegalArgumentException("INCORRECT SYNCTYPE");
                }
                vehicleDao.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;

    }

    /**
     * @param
     */
    private void getUserDataToSync() {

        try {
            String url = createUrlFromParams(Url.USER_GET_BY_CONTACT, genarateUrlParam());
            String tag_json_obj = JSON_OBJECT_REQUEST_TAG;
            JsonObjectRequest jsonUserObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        Log.d(TAG, "Sincronizando dados do utilizador para a bd...");
                        boolean isUserSynced = syncUser(jsonObject.getJSONObject(USER), SYNC_TYPE);
                        if (isUserSynced) result = Activity.RESULT_OK;
                        sendResult(serviceIntent);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                }
            })
            {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            // Adding request to request queue
            NetworkController.getInstance().addToRequestQueue(jsonUserObjectRequest, tag_json_obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean syncContact(JSONArray contactArray, int syncType){
        ContactoDAO contactoDAO = new ContactoDAO(getApplicationContext());
        contactoDAO.open();
        boolean syncResult = false;
        try {
            for (int i = 0; i < contactArray.length(); i++) {
                JSONObject c = contactArray.getJSONObject(i);
                Contacto contacto = new Contacto(c.getInt("id"), c.getString("email"), c.getString("telefone_1"), c.getString("telefone_2"));

                if (syncType == Sync.FIRST_USER_SYNC) {
                    if (!contactoDAO.contactExistsOnDb(contacto))
                        contactoDAO.create(contacto);
                }else if (syncType == Sync.UPDATE_SYNC) {
                    contactoDAO.update(contacto);
                }
                syncResult = true;
            }
            contactoDAO.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return syncResult;
    }

    private boolean syncUser(JSONObject userJsonObject, int syncType){
        UserDAO userDao = new UserDAO(getApplicationContext());
        userDao.open();
        boolean synced = false;
        try {
            for (int i = 0; i < userJsonObject.length(); i++) {
                if (syncType == Sync.FIRST_USER_SYNC) {
                    userDao.create(generateUserFromJsonObject(userJsonObject), getApplicationContext());
                }else if (syncType == Sync.UPDATE_SYNC) {
                    userDao.update(generateUserFromJsonObject(userJsonObject), getApplicationContext());
                }
                synced = true;
            }
            userDao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return synced;
    }

    private User generateUserFromJsonObject(JSONObject c) throws JSONException {
        JSONObject contactoJsonObject = c.getJSONObject("contacto");


        User user = new User();
        user.setId(c.getInt("id"));
        user.setName(c.getString("name"));
        user.setSurname(c.getString("surname"));
        user.setLoginNumber(c.getString("loginnumber"));
        user.setPassword(c.getString("password"),false);
        user.setContacto(new Contacto());

        for (int j = 0;j < contactoJsonObject.length(); j++){
            user.getContacto().setId(contactoJsonObject.getInt("id"));
            user.getContacto().setEmail(contactoJsonObject.getString("email"));
            user.getContacto().setTelefone_1(contactoJsonObject.getString("telefone_1"));
            user.getContacto().setTelefone_2(contactoJsonObject.getString("telefone_2"));
        }

        user.setActive((c.getInt("state") > 0));
        user.setType(new UserType(c.getInt("type_id")));
        if (c.getString("last_reset_date").toString().trim() == null)
            user.setLastResetDate(c.getString("last_reset_date"));
        user.setOldPassword(c.getString("old_password"));
        user.setPasswordRestoreCode(c.getString("password_reset_code"));
        return user;
    }
}
