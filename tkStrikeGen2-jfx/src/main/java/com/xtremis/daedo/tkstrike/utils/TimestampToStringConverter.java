package com.xtremis.daedo.tkstrike.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.util.StringConverter;

public class TimestampToStringConverter extends StringConverter<Long> {
  private final SimpleDateFormat sdf;
  
  public TimestampToStringConverter(String sdfFormat) {
    this.sdf = new SimpleDateFormat(sdfFormat);
  }
  
  public String toString(Long aLong) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(aLong.longValue());
    return this.sdf.format(cal.getTime());
  }
  
  public Long fromString(String s) {
    Calendar cal = Calendar.getInstance();
    try {
      cal.setTime(this.sdf.parse(s));
    } catch (ParseException e) {
      e.printStackTrace();
    } 
    return Long.valueOf(cal.getTimeInMillis());
  }
}
