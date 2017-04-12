package mz.co.insystems.trackingservice.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by voloide on 9/16/16.
 */
public class AppConfig {
    public static final String AIP_KEY = "d9bfd1770a826d178660a2087eca168d";
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE               = 100;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS                 = 101;
    public static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS              = 102;
    public static final int MY_PERMISSIONS_REQUEST_READ_SMS                 = 103;
    public static final int MY_PERMISSIONS_REQUEST_INTERNET                 = 104;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE     = 105;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE         = 106;
    public static final int MY_PERMISSIONS_REQUEST_READ_GSERVICES           = 107;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE   = 108;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION     = 109;


    public static boolean checkPermitionForOperation(String permission, Context context){
        if (ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED) return true;
        return false;
    }

    public static int requestPermission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_CALL_PHONE;
                break;
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return  MY_PERMISSIONS_REQUEST_SEND_SMS;
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
                break;
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_RECEIVE_SMS;
                break;
            case MY_PERMISSIONS_REQUEST_READ_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_READ_SMS;
                break;
            case MY_PERMISSIONS_REQUEST_INTERNET:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_INTERNET;
                break;
            case MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE;
                break;
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_READ_PHONE_STATE;
                break;
            case MY_PERMISSIONS_REQUEST_READ_GSERVICES:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_READ_GSERVICES;
                break;
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) return MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
                break;
            default:
        }
        return 0;
    }

    public static void requestMissingPermition(String permission, int myRequestCode, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity, new String[]{permission}, myRequestCode);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

}
