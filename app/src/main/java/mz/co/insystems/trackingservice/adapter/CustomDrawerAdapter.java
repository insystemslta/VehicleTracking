package mz.co.insystems.trackingservice.adapter;


import java.util.List;


import mz.co.insystems.trackingservice.R;
import mz.co.insystems.trackingservice.model.Command;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CustomDrawerAdapter extends ArrayAdapter<Command> {
 
      Context context;
      List<Command> commandList;
      int layoutResID;
 
      public CustomDrawerAdapter(Context context, int layoutResourceID,
                  List<Command> listItems) {
            super(context, layoutResourceID, listItems);
            this.context = context;
            this.commandList = listItems;
            this.layoutResID = layoutResourceID;
 
      }
 
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
 
            DrawerItemHolder drawerHolder;
            View view = convertView;
 
            if (view == null) {
                  LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                  drawerHolder = new DrawerItemHolder();
 
                  view = inflater.inflate(layoutResID, parent, false);
                  drawerHolder.ItemName = (TextView) view
                              .findViewById(R.id.drawer_itemName);
                  drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
 
                  view.setTag(drawerHolder);
 
            } else {
                  drawerHolder = (DrawerItemHolder) view.getTag();
 
            }
 
            Command dItem = (Command) this.commandList.get(position);

            
            //String uri = "@drawable/myresource";  // where myresource.png is the file
            // extension removed from the String

            Resources resources = context.getResources();
            final int resourceId = resources.getIdentifier(dItem.getIcon(), "drawable", context.getPackageName());
            //return resources.getDrawable(resourceId);
 
            //drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(resourceId));
            drawerHolder.ItemName.setText(dItem.getName());
 
            return view;
      }
 
      private static class DrawerItemHolder {
            TextView ItemName;
            ImageView icon;
      }
}