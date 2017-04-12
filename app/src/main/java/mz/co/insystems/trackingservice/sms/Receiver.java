package mz.co.insystems.trackingservice.sms;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.activities.MapActivity;
import mz.co.insystems.trackingservice.activities.VehicleAlarmImage;
import mz.co.insystems.trackingservice.activities.VehicleStateActivity;
import mz.co.insystems.trackingservice.dao.CommandDAO;
import mz.co.insystems.trackingservice.dao.VehicleDAO;
import mz.co.insystems.trackingservice.model.CommandResponse;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.util.AlarmType;

/**
 * Created by Voloide Tamele on 9/16/16.
 */
public class Receiver extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    VehicleDAO vehicleDao;
    protected Vehicle VehicleOnDb = new Vehicle();

    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = new Object[0];
        String sender = null;
        StringBuilder notificationMessage = null;
        SmsMessage shortMessage = null;
        try {
            pdus = (Object[]) intent.getExtras().get("pdus");
            sender = "";
            notificationMessage = new StringBuilder();
            if (pdus != null)
            // get sender from first PDU
            shortMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            else
                return;

            sender = shortMessage.getOriginatingAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        vehicleDao = new VehicleDAO(context);
        vehicleDao.open();

        Vehicle vehicle = new Vehicle();
        if (sender != null && sender.length() > 3)
            vehicle.setCallNumber(sender.substring(4));
        else
            return;


        if (isAlertFromVehicle(vehicle)) {
            VehicleOnDb = vehicleDao.get(vehicle);
            vehicleDao.close();
            for (int i = 0; i < pdus.length; i++) {
                shortMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                try {
                    notificationMessage.append(shortMessage.getDisplayMessageBody());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            genarateNotification(context, processNotificaion(notificationMessage));
        }
    }

    private CommandResponse processNotificaion(StringBuilder notificationMessage) {

        String firstStr = "";
        String[] reponseArrayParts;
        if (notificationMessage.toString().length() > 0) firstStr = notificationMessage.toString().substring(0, 2);
        if (firstStr.equalsIgnoreCase("VR")) {
            reponseArrayParts = notificationMessage.toString().split(" ");
        }else{
            reponseArrayParts = notificationMessage.toString().split("\n");
        }

        final CommandResponse commandResponse = new CommandResponse();
        try {
            int i = 0;
            do {
                if (i == 0 && reponseArrayParts[i].contains(":") && !reponseArrayParts[0].contains("VR:")) {
                    fillResponse(reponseArrayParts, commandResponse, i);
                }
                if (i > 0 && reponseArrayParts[i].contains(":")) fillResponse(reponseArrayParts, commandResponse, i);
                if (i == 0 && !reponseArrayParts[i].contains(":")) commandResponse.setHeader(reponseArrayParts[i]);
                if (i == 0 && reponseArrayParts[i].contains("VR:")) {
                    commandResponse.setHeader("VR");
                    fillResponse(reponseArrayParts, commandResponse, i+1);
                }
                i++;
            } while (i < reponseArrayParts.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commandResponse;
    }


    /**
     * @param reponseArrayParts array of parts of the response
     * @param commandResponse command response object
     * @param i array position
     */
    private void fillResponse(String[] reponseArrayParts, CommandResponse commandResponse, int i) {
        String[] reponse = reponseArrayParts[i].split(":");


        try {
            for (int j = 0; j < reponse.length;j++){

                if (reponse[j].equalsIgnoreCase("lat") && j+2 < reponse.length){
                    String[] reponseLat = reponse[j+1].split(" ");
                    for (int k = 0; k < reponseLat.length; k++) {
                        commandResponse.setLatitude(reponseLat[0].trim());
                    }
                    commandResponse.setLongitude(reponse[j+2].trim());
                    commandResponse.setHeader(AlarmType.LOCATION_ALARM);
                }else {
                    if (reponse[j].equalsIgnoreCase("lat")) 	commandResponse.setLatitude(reponse[j+1].trim());
                }

                if (reponse[j].equalsIgnoreCase("pwr")){
                    String[] reponseP = reponse[j+1].split(" ");
                    commandResponse.setPower((reponseP[1].trim().equalsIgnoreCase("on")));
                    if (reponseP[2].trim().equalsIgnoreCase("door")) {
                        String[] reponseP1 = reponse[j+2].split(" ");
                        commandResponse.setDoor((reponseP1[1].trim().equalsIgnoreCase("on")));
                        if (reponseP1[2].trim().equalsIgnoreCase("acc")){
                            commandResponse.setAcc((reponse[3].trim().equalsIgnoreCase("on")));
                        }
                    }
                }
                if (reponse[j].equalsIgnoreCase("Power")) 	commandResponse.setHeader(AlarmType.STATE_ALARM);
                if (reponse[j].equalsIgnoreCase("Power")) 	commandResponse.setPower((reponse[j+1].trim().equalsIgnoreCase("on")));
                if (reponse[j].equalsIgnoreCase("bat")) 	commandResponse.setBat(reponse[j+1].trim());
                if (reponse[j].equalsIgnoreCase("imei")) 	commandResponse.setImei(reponse[j+1].trim());
                if (reponse[j].equalsIgnoreCase("gprs")) 	commandResponse.setGprs((reponse[j+1].trim().equalsIgnoreCase("on")));
                if (reponse[j].equalsIgnoreCase("gps")) 	commandResponse.setGps((reponse[j+1].trim().equalsIgnoreCase("on")));
                if (reponse[j].equalsIgnoreCase("acc")) 	commandResponse.setAcc((reponse[j+1].trim().equalsIgnoreCase("on")));
                if (reponse[j].equalsIgnoreCase("door")) 	commandResponse.setDoor((reponse[j+1].trim().equalsIgnoreCase("on")));
                if (reponse[j].equalsIgnoreCase("gsm")) 	commandResponse.setGsm(Double.parseDouble(reponse[j+1].trim()));
                if (reponse[j].equalsIgnoreCase("oil")) 	commandResponse.setOil(reponse[j+1].trim());
                if (reponse[j].equalsIgnoreCase("odo")) 	commandResponse.setOdo(Double.parseDouble(reponse[j+1].trim()));
                if (reponse[j].equalsIgnoreCase("apn")) 	commandResponse.setApn(reponse[j+1].trim());
                if (reponse[j].equalsIgnoreCase("ip")) 		commandResponse.setIp(reponse[j+1].trim());
                if (reponse[j].equalsIgnoreCase("ip")) 		commandResponse.setPort(Integer.parseInt(reponse[j+2].trim()));
                if (reponse[j].equalsIgnoreCase("arm")) 	commandResponse.setArm((reponse[j+1].trim().equalsIgnoreCase("on")));
                if (reponse[j].equalsIgnoreCase("long")) 	commandResponse.setLongitude(reponse[j+1].trim());
                if (reponse[j].equalsIgnoreCase("speed")) 	commandResponse.setSpeed(Double.parseDouble(reponse[j+1].trim()));
                if (reponse[j].equalsIgnoreCase("http")) {
                    if (reponse[j+1].trim().contains("maps.google.com"))
                        commandResponse.setLocationUrl("http:"+reponse[j+1].trim());
                    else if (reponse[j+1].trim().contains("01.GPSTrackerXY.com"))
                        commandResponse.setAlarmImageUrl("http:"+reponse[j+1].trim());
                }
                String stringTime = "";
                if (reponse[j].equalsIgnoreCase("T") && !commandResponse.getHeader().contains(AlarmType.VR_ALARM)){

                    String[] dateArray = reponse[j+1].split(" ");
                    for (int k = 0; k < dateArray.length; k++) {
                        if(k == 0){
                            DateFormat df = new SimpleDateFormat("MM/dd/yy");
                            try {
                                commandResponse.setDate(df.parse(dateArray[k]));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        if(k == 1) stringTime = dateArray[k];
                    }
                    stringTime = stringTime + ":" +reponse[j+2];
                    SimpleDateFormat sdf = new SimpleDateFormat ("HH:mm");
                    commandResponse.setTime(sdf.parse(stringTime));
                }

                if (reponse[j].equalsIgnoreCase("T") && commandResponse.getHeader().contains(AlarmType.VR_ALARM)){

                    DateFormat df = new SimpleDateFormat("MM/dd/yy");
                    try {
                        commandResponse.setDate(df.parse(reponse[j+1]));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (reponse[j].equalsIgnoreCase("T") && commandResponse.getHeader().contains(AlarmType.VR_ALARM)){
                    // To-do set time
                }
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    private void genarateNotification(Context context, final CommandResponse vehicleResponse){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //if (responseIsPassChanged(vehicleResponse))


        Intent vehicleStateIntent = new Intent(context, VehicleStateActivity.class); //VehicleStateActivity
        setVehicleStateIntentExtras(vehicleResponse, vehicleStateIntent);

        Intent mapNotificationIntent = new Intent(context, MapActivity.class); //MapActivity
        setVehicleStateIntentExtras(vehicleResponse, mapNotificationIntent);

        Intent vehicleImageIntent = new Intent(context, VehicleAlarmImage.class); //VehicleAlarmImage
        setVehicleStateIntentExtras(vehicleResponse, vehicleImageIntent);

        PendingIntent mapPendingIntent 			= PendingIntent.getActivity(context, 0, mapNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent vehicleStatePendingIntent = PendingIntent.getActivity(context, 0, vehicleStateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent vehicleImagePendingIntent = PendingIntent.getActivity(context, 0, vehicleImageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        CommandDAO commandDao = new CommandDAO(context);
        commandDao.open();
        vehicleResponse.setTitle(commandDao.getCommandResponseTitleFromHeader(vehicleResponse.getHeader()));
        vehicleResponse.setDescription(commandDao.getCommandResponseDescriptionFromHeader(vehicleResponse.getHeader()));
        commandDao.close();

        int icon = 0;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(vehicleResponse.getTitle())
                .setSound(alarmSound)
                .setContentText(vehicleResponse.getDescription());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            icon = R.mipmap.ic_notification_new;
        } else {
            icon = R.mipmap.ic_launcher;
        }
        builder.setSmallIcon(icon);

        if (responseHasLocation(vehicleResponse))
            builder.addAction(R.mipmap.ic_room_black_24dp,
                    context.getString(R.string.notification_locate_vehicle),
                    mapPendingIntent);
        if (responseHasState(vehicleResponse))
            builder.addAction(R.mipmap.ic_info_black_24dp,
                    context.getString(R.string.notification_vehicle_state),
                    vehicleStatePendingIntent);
        if (vehicleResponse.getHeader().equalsIgnoreCase(AlarmType.VR_ALARM))
            builder.addAction(R.mipmap.ic_action_camera,
                    context.getString(R.string.notification_vehicle_image),
                    vehicleImagePendingIntent);

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(vehicleResponse.getDescription()));

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
    }

    /**
     * @param vehicleResponse
     * @param intent
     */
    private void setVehicleStateIntentExtras(CommandResponse vehicleResponse, Intent intent) {
        intent.putExtra("vehicleResponse", vehicleResponse);
        intent.putExtra("vehicle", VehicleOnDb);
    }

    private boolean responseHasState(CommandResponse vehicleResponse) {
        if (vehicleResponse.getHeader().contains(AlarmType.STATE_ALARM)) return true;
        if (vehicleResponse.getHeader().contains(AlarmType.LOCATION_ALARM)) return true;
        return false;
    }

    private boolean responseIsPassChanged(CommandResponse vehicleResponse) {
        if (vehicleResponse.getHeader().contains(AlarmType.PWD_CHANGED)) return true;
        return false;
    }

    private boolean responseHasLocation(CommandResponse response) {
        return (response.getLatitude() != 0.0 && response.getLongitude() != 0.0);
    }

    private boolean isAlertFromVehicle(Vehicle vehicle) {
        return (vehicleDao.confirmContact(vehicle));
    }
}
