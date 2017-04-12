package mz.co.insystems.trackingservice.sync;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mz.co.insystems.trackingservice.util.AppConfig;

/**
 * Created by voloide on 9/19/16.
 */
public class ParseComServerAuthenticate implements ServerAuthenticate{
    private boolean isValid = false;
    private static final String RESULT = "success";
    private int SUCCESS = 1;
    public static final String TAG = ParseComServerAuthenticate.class.getSimpleName().toUpperCase();

    public ParseComServerAuthenticate() {
        super();

    }

    @Override
    public boolean userSignIn(String loginNumber, String password, boolean isFirstSync) {
        try {
            List<String> params = new ArrayList<String>();
            params.add(loginNumber);

            String baseUrl;
            if (password != null) {
                params.add(password);
                baseUrl = Url.USER_VALIDATE;
            }else{
                baseUrl = Url.VALIDADE_USER_CONTACT;
            }

            params.add(AppConfig.AIP_KEY);

            String url = InSystemsSyncService.createUrlFromParams(baseUrl, params);
            String tag_json_obj = "json_obj_req";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        if (!jsonObject.isNull(RESULT)) {
                            if (jsonObject.getInt(RESULT) == SUCCESS) {
                                Log.d("Validar Callback", "No onResponse, resultado === " + jsonObject.getInt(RESULT));
                                isValid = true;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    VolleyLog.d(this.getClass().getSimpleName().toUpperCase(), "Error: " + volleyError.getMessage());
                }
            }) {

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
            NetworkController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }


}
