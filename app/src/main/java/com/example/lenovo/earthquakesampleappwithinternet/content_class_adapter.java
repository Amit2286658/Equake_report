package com.example.lenovo.earthquakesampleappwithinternet;

/*declaring import statements to be used throughout the entire program*/

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class content_class_adapter extends ArrayAdapter<content_class> {

    private SimpleDateFormat date_fromatter=new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat time_formatter=new SimpleDateFormat("hh:mm a",Locale.getDefault());

    private Typeface roboto_mono= Typeface.createFromAsset(getContext().getAssets(),"fonts/roboto_mono.ttf");
    private Typeface roboto_thin = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_thin.ttf");
    private Typeface roboto_light = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_light.ttf");
    private Typeface roboto_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_regular.ttf");

    public content_class_adapter(Context context, ArrayList<content_class> datalist) {
        super(context, 0, datalist);
    }

    public static class viewHolder{
        viewHolder(){
            ////////empty constructor
        }
        TextView magnitude;
        TextView locationPrimary;
        TextView locationSecondary;
        TextView date;
        TextView time;
        GradientDrawable magnitudeCircle;
    }

    //overrdiding the getView method that the listView will use
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        viewHolder holder;

        //assigning the convert view to ListItemView
        View ListItemView = convertView;
        if (ListItemView == null){
            //inflating the layout in case it's null( which happens to be the first time, the listView displays data on screen)
            ListItemView = LayoutInflater.from(getContext()).inflate(R.layout.listview_content_items, parent, false);

            holder = new viewHolder();

            holder.magnitude = (TextView)ListItemView.findViewById(R.id.magnitude);
            holder.locationPrimary = (TextView)ListItemView.findViewById(R.id.locationPrimary);
            holder.locationSecondary = (TextView)ListItemView.findViewById(R.id.locationSecondary);
            holder.date = (TextView)ListItemView.findViewById(R.id.date);
            holder.time = (TextView)ListItemView.findViewById(R.id.time);
            holder.magnitudeCircle = (GradientDrawable)holder.magnitude.getBackground();

            ListItemView.setTag(holder);
        } else {
            holder = (viewHolder)ListItemView.getTag();
        }

        //getting the item at the specified position provided by the getView method by default
        //and assigning it to the object of the content_class and declaring it to be final
        final content_class getPosition= getItem(position);
        assert getPosition != null;

        /*finding the textView to set the magnitude value to the correct textView*/
        //////////////////////////////////TextView magnitude=(TextView)ListItemView.findViewById(R.id.magnitude);
        holder.magnitude.setText(getPosition.getMagnitude());

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        /////////////////////////////////GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor=getColorResouceId(getPosition.getMagnitude());
        // Set the color on the magnitude circle
        holder.magnitudeCircle.setColor(magnitudeColor);

        /*finding the textView to set the primary location value to the correct textView*/
        /////////////////////////////////TextView locationPrimary=(TextView)ListItemView.findViewById(R.id.locationPrimary);
        holder.locationPrimary.setText(getPosition.getLocationPrimary());
        holder.locationPrimary.setTypeface(roboto_light);

        /*finding the textView to set the secondary location value to the correct textView*/
        /////////////////////////////////TextView locationSecondary=(TextView)ListItemView.findViewById(R.id.locationSecondary);
        holder.locationSecondary.setText(getPosition.getLocationSecondary());
        holder.locationSecondary.setTypeface(roboto_regular);

        /*finding the textView to set the date value to the correct textView*/
        /*finding the textView to set the desiredtime value to the correct textView*/
        /////////////////////////////////////TextView date=(TextView)ListItemView.findViewById(R.id.date);
        /////////////////////////////////////TextView time=(TextView)ListItemView.findViewById(R.id.time);

        /*
        * setting the date and desiredtime in an appropriate way*/
        try{
            Date currentTime=new Date(System.currentTimeMillis()-3600*1000);
            if (current_date_check(getPosition.getDate())==1){
                if (time_formatter.parse(getPosition.getTime()).after(time_formatter.parse(time_formatter.format(currentTime)))){
                    holder.date.setText(String.valueOf(timeCheck(getPosition.getTime()))+" mins ago");
                    holder.date.setTypeface(roboto_regular);
                    holder.time.setText("");
                    holder.time.setVisibility(View.GONE);
                }
                else {
                    holder.date.setText("Today at "+ getPosition.getTime());
                    holder.date.setTypeface(roboto_regular);
                    holder.time.setText("");
                    holder.time.setVisibility(View.GONE);
                }
            }
            else if (current_date_check(getPosition.getDate())==0){
                holder.date.setText(formatDate(getPosition.getDate())+" at "+getPosition.getTime());
                holder.date.setTypeface(roboto_regular);
                holder.time.setText("");
                holder.time.setVisibility(View.GONE);
            }else{
                holder.date.setText(getPosition.getDate());
                holder.time.setText(getPosition.getTime());
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return ListItemView;
    }

    /*creating a method to get the colors resource id, and it returns an int data type*/
    private int getColorResouceId(String magnitude){
        int color_resource_id;
        int color_id=(int)Double.parseDouble(magnitude);

        /*switch statement to return the correct id of color based on the value of magnitude*/
        switch (color_id){
            case 0:
                color_resource_id=R.color.magnitude0;
                break;
            case 1:
                color_resource_id=R.color.magnitude1;
                break;
            case 2:
                color_resource_id=R.color.magnitude2;
                break;
            case 3:
                color_resource_id=R.color.magnitude3;
                break;
            case 4:
                color_resource_id=R.color.magnitude4;
                break;
            case 5:
                color_resource_id=R.color.magnitude5;
                break;
            case 6:
                color_resource_id=R.color.magnitude6;
                break;
            case 7:
                color_resource_id=R.color.magnitude7;
                break;
            case 8:
                color_resource_id=R.color.magnitude8;
                break;
            case 9:
                color_resource_id=R.color.magnitude9;
                break;
            case 10:
                color_resource_id=R.color.magnitude10;
                break;
            default:
                color_resource_id=R.color.magnitude11;
                break;
        }
        return  ContextCompat.getColor(getContext(),color_resource_id);
    }

    /*this method checks if the earthquake's date is the current date or not,
    * if yes, it returns 1,
    * if the earthquake's date is behind the current date, it returns 0,
    * in any other case, it returns -1.*/
    private int current_date_check(String earthquakeDate) throws ParseException {
        Date current_date=date_fromatter.parse(date_fromatter.format(new Date()));
        Date dateOfEarthquake=date_fromatter.parse(earthquakeDate);
        if (dateOfEarthquake.equals(current_date)){
            return 1;
        }
        else if (dateOfEarthquake.before(current_date))
            return 0;
        else
            return -1;
    }

    /*returns the minutes
    * and is called only if the earthquake's desiredtime is within an hour behind the current desiredtime*/
    private long timeCheck(String time) throws ParseException{

            return ((time_formatter.parse(time_formatter.format(new Date()))
                    .getTime()-time_formatter.parse(time).getTime())/1000)/60;
    }

    private String formatDate(String earthquakeDate) throws ParseException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        Date date = date_fromatter.parse(earthquakeDate);
        return simpleDateFormat.format(date);
    }
}

