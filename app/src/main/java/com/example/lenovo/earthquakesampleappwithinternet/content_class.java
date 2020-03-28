package com.example.lenovo.earthquakesampleappwithinternet;


/*creating an another class called content_class that provides the data to the custom adapter*/
class content_class{

    //declaring the required strings,the constructor since accepts only the Strings,
    //therefore only the parameter in string data type should be provided to the class using the constructor
    private String Magnitude, LocationPrimary, LocationSecondary,date, time, Link, alert, status,
            Longitude, Latitude, Depth, felt, title, intensity, significsnce, updatedDate, updatedTime;
    private int tsunami_warning;

    @Deprecated
    content_class(){
        /*
        * i'm deprecating the constructor
        * because i don't see it's any future use,
        * the another constructor should be used in it's place*/
    }


    content_class(String magnitude, String locationPrimary, String locationSecondary, String dateInput, String timeInput,
                  String link, String Alert, int tsunami, String Status, String longitude, String latitude, String depth,
                  String felt, String title, String mIntensity, String mSignificanse, String mUpdatedDate, String mUpdatedTime){
        Magnitude=magnitude;
        LocationPrimary = locationPrimary;
        LocationSecondary=locationSecondary;
        date=dateInput;
        time= timeInput;
        Link=link;
        alert=Alert;
        tsunami_warning=tsunami;
        status=Status;
        this.Longitude =longitude;
        this.Latitude =latitude;
        this.Depth =depth;
        this.felt = felt;
        this.title = title;
        intensity = mIntensity;
        significsnce = mSignificanse;
        updatedDate = mUpdatedDate;
        updatedTime = mUpdatedTime;
    }

    String getMagnitude() {
        return Magnitude;
    }

    String getDate() {
        return date;
    }

    String getLocationPrimary() {
        return LocationPrimary;
    }

    String getLocationSecondary() {
        return LocationSecondary;
    }

    String getTime() {
        return time;
    }
    String getLink() {
        return Link;
    }

    String getAlert() {
        return alert;
    }

    String getDepth() {
        return Depth;
    }

    String getLatitude() {
        return Latitude;
    }

    String getLongitude() {
        return Longitude;
    }

    String getStatus() {
        return status;
    }

    int getTsunami_warning() {
        return tsunami_warning;
    }

    String getFelt() {
        return felt;
    }

    public String getTitle() {
        return title;
    }

    String getIntensity() {
        return intensity;
    }

    String getSignificsnce() {
        return significsnce;
    }

    String getUpdatedDate() {
        return updatedDate;
    }

    String getUpdatedTime() {
        return updatedTime;
    }

}
