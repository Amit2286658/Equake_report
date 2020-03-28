package com.example.lenovo.earthquakesampleappwithinternet.timeChecks;

import java.text.ParseException;

public interface timecheck {

    boolean yesterdayCheck(String Date) throws ParseException;

    boolean last2DaysCheck(String Date) throws ParseException;

    boolean last4DaysCheck(String Date) throws ParseException;

    boolean thisWeekCheck(String Date) throws ParseException;

}