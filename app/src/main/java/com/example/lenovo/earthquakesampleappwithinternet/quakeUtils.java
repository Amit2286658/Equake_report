package com.example.lenovo.earthquakesampleappwithinternet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class quakeUtils {

    private static final String LOG_TAG="MyApp";

    private static final String delimiter="of";
    private static DecimalFormat decimalFormatter=new DecimalFormat("0.0");

    static List<content_class> fetchEarthQuakeData(String requestUrl){
        String jsonResponse="";
        URL url=createUrl(requestUrl);
        try {
            assert url != null;
            jsonResponse+=makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractQuakeDataFromHttp(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            return null;
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse += readFromStream(inputStream);
            }
        } catch (IOException e) {
            // TODO: Handle the exception
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static ArrayList<content_class> extractQuakeDataFromHttp(String jsonResponse){

        /* Sample JSON response for a USGS query */

        ArrayList<content_class> dataList = new ArrayList<>();

        try {
            JSONObject mainObject=new JSONObject(jsonResponse);
            JSONArray ARRAY=mainObject.getJSONArray("features");

            for (int i=0; i<ARRAY.length(); i++){
                JSONObject indexRootObject=ARRAY.getJSONObject(i);
                JSONObject properties=indexRootObject.getJSONObject("properties");

                /*getting the desired properties from the indexed object */
                double magnitude=properties.getDouble("mag");
                String place=properties.getString("place");
                long timeInMiliseconds =properties.getLong("time");
                String link=properties.getString("url");
                int Tsunami_alert=properties.getInt("tsunami");

                String Status=properties.getString("status");

                String Alert=properties.getString("alert");

                String felt = properties.getString("felt");

                String title=properties.getString("title");

                String intensity = properties.getString("cdi");
                String significance = properties.getString("sig");
                long Updated = properties.getLong("updated");
                String updatedDate = Date_formatter(new Date(Updated));
                String updatedTime = Time_formatter(new Date(Updated));

                JSONObject geometry=indexRootObject.getJSONObject("geometry");
                JSONArray coordinates=geometry.getJSONArray("coordinates");

                double mLongitude=coordinates.getDouble(0);
                String longitude=Double.toString(mLongitude);
                double mLatitude=coordinates.getDouble(1);
                String latitude=Double.toString(mLatitude);
                double mDepth=coordinates.getDouble(2);
                String depth=Double.toString(mDepth);

                Date dateObject=new Date(timeInMiliseconds);

                String date= Date_formatter(dateObject);

                String time=Time_formatter(dateObject);

                String mag=decimalFormatter.format(magnitude);

                if(place.contains(delimiter)){
                    String[] splittedLocation=place.split(delimiter);
                    /*checking if the magnitude contains the negative number
                     * if it does, we'll omit the negative symbol from the String converted magnitude*/

                        dataList.add(new content_class(mag,splittedLocation[0].trim()+" "+delimiter,
                                splittedLocation[1].trim(),date,time, link, Alert, Tsunami_alert, Status, longitude,
                                latitude, depth, felt, title, intensity, significance, updatedDate, updatedTime));
                }else{
                        dataList.add(new content_class(mag, "Near to", place.trim(), date, time, link, Alert,
                                Tsunami_alert, Status, longitude, latitude,
                                depth, felt, title, intensity,significance, updatedDate,updatedTime));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;

    }
    private static String Date_formatter(Date dateObject){
        SimpleDateFormat dateFormatter =new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return dateFormatter.format(dateObject);
    }

    private static String Time_formatter(Date dateObject){
        SimpleDateFormat timeFormatter=new SimpleDateFormat("hh:mm a",Locale.getDefault());
        return timeFormatter.format(dateObject);
    }

}
