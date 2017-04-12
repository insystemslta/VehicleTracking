package mz.co.insystems.trackingservice.sync;

import org.json.JSONObject;

/**
 * Created by Voloide Tamele on 10/6/16.
 */
public interface DataCallback {
    void  onSuccess(JSONObject result);
}
