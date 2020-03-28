package equake_report.contract;

public class constantValues {
    public static final String LAST_HOUR = "Last hour";
    public static final String LAST_12HOUR = "Last 12 hrs";
    public static final String LAST_24HOUR = "Last 24 hrs";
    public static final String LAST_48HOUR = "Last 48 hrs";
    public static final String MAG0 = "0+";
    public static final String MAG1 = "1.0+";
    public static final String MAG2 = "2.0+";
    public static final String MAG3 = "3.0+";
    public static final String MAG4 = "4.0+";
    public static final String MAG5 = "5.0+";
    public static final String MAG6 = "6.0+";
    public static final String MAG7 = "7.0+";
    public static final String MAPTYPE_NORMAL = "Normal";
    public static final String MAPTYPE_HYBRID = "Hybrid";
    public static final String MAPTYPE_SATELLITE = "Satellite";
    public static final String MAPTYPE_TERRAIN = "Terrain";
    public static final String YESTERDAY = "Yesterday";
    public static final String Last2DAYS = "Last 2 days";
    public static final String Last4DAYS = "Last 4 days";
    public static final String THIS_WEEK = "This week";

    /*below final variables are usd as the keys to pass data from listView to the detailsActivity activity in a bundle*/

    public static final String ALERT = "Alert_level : ";
    public static final String DATEANDTIME = "Date : ";
    public static final String REPORTEDTIME = "Updated : "; //reported and updated words are used interchangeably
    public static final String TSUNAMIWARNING = "Tsunami warning : ";
    public static final String INTENSITY = "Intensity : ";
    public static final String SIGNIFICANCE = "Significance : ";
    public static final String LONGITUDE = "Longitude : ";
    public static final String LATITUDE = "Latitude : ";
    public static final String DEPTH = "Depth : ";
    public static final String STATUS = "Status : ";
    public static final String FELT = "Felt : ";
    public static final String MAGNITUDE="mag";
    public static final String TITLE="title";
    public static final String LONGITUDE_DOUBLE="lon_double";
    public static final String LATITUDE_DOUBLE="lat_double";
    public static final String LOCATIONPRIMARY_DETAIL="locationPrimary";
    public static final String LOCATIONSECONDARY_DETAIL = "locationSecondary";
    public static final String USGSLINK_DETAIL = "usgs_link";
    public static final String ACTIVITY_IDENTIFIER = "activity_identifier";

    /*keys to be used in the inbuilt browser*/

    public static final String URL_KEY = "itemUrl";
    public static final String BROWSER_TITLE_KEY = "browserTitle";


    /*keys to use to restore the main fragment state on recreation*/

    public static final String TIME_IDENTIFIER_RESTORE_KEY = "timeRestoreKey";
    public static final String MAG_IDENTIFIER_RESTORE_KEY = "magIdentifierKey";
    public static final String EARTHQUAKE_LOADER_ID_KEY = "earthquake_loader_id_key";
    public static final String DESIRED_MAG_KEY = "desired_mag_key";

    /*keys to use to restore the full_map_fragmant state on recreation*/

    public static final String DESIRED_MAG_MAP_KEY = "desiredMag_map_key";
    public static final String DESIRED_TIME_MAP_KEY = "desiredTime_map_key";
    public static final String TIME_IDENTIFIER_MAP_KEY = "timeIdentifier_map_key";
    public static final String MAG_IDENTIFIER_MAP_KEY = "magIdentifier_map_key";
}

