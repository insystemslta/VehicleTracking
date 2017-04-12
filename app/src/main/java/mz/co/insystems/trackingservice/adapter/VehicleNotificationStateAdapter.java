package mz.co.insystems.trackingservice.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.activities.VehicleStateActivity;

/**
 * Created by voloide on 9/23/16.
 */
public class VehicleNotificationStateAdapter extends BaseAdapter {

    Context context;
    private final ArrayList mData;
    public VehicleNotificationStateAdapter(HashMap<String, HashMap<String, String>> map, Context context){
        mData = new ArrayList();
        mData.addAll(map.entrySet());
        this.context = context;
    }
    @Override
    public int getCount() {
        return mData.size();
    }


    @Override
    public Map.Entry<String, HashMap<String, String>> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_adapter_item, parent, false);
        } else {
            result = convertView;
        }
        Log.d("STATE ADAPTER", "Position "+position);
        Map.Entry<String, HashMap<String, String>> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.text_key)).setText(item.getKey());
        //((TextView) result.findViewById(R.id.text_value)).setText(item.getValue());

        Iterator it = item.getValue().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry stateMap = (Map.Entry) it.next();
            ((TextView) result.findViewById(R.id.text_value)).setText(stateMap.getKey().toString());
            Resources resources = context.getResources();
            final int iconId = resources.getIdentifier(stateMap.getValue().toString(), "mipmap", context.getPackageName());
            if (iconId != 0)
                ((ImageView) result.findViewById(R.id.notification_icon)).setImageDrawable(result.getResources().getDrawable(iconId));
            //it.remove();
        }
        return result;
    }
}

