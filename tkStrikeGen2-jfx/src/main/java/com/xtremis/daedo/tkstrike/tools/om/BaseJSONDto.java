package com.xtremis.daedo.tkstrike.tools.om;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.CaseFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class BaseJSONDto implements Serializable {
  private static final long serialVersionUID = -3297201975108902308L;
  
  private static final Logger _log = LoggerFactory.getLogger(BaseJSONDto.class);
  
  protected static final DecimalFormat df = new DecimalFormat("00");
  
  protected static final SimpleDateFormat timeDf = new SimpleDateFormat("mm:ss");
  
  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      mapper.writeValue(os, this);
      jsonObject = new JSONObject(os.toString());
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return jsonObject;
  }
  
  public void fromJSON(String jsonStr) throws JSONException {
    JSONObject jsonObject;
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    JSONObject wrappedObject = new JSONObject(jsonStr);
    String wrapperName = getJSONWrapperName();
    if (wrappedObject.has(wrapperName)) {
      jsonObject = wrappedObject.getJSONObject(wrapperName);
    } else {
      jsonObject = wrappedObject;
    } 
    JavaType jt = TypeFactory.defaultInstance().constructFromCanonical(getClass().getCanonicalName());
    Object result = null;
    try {
      result = jt.getRawClass().newInstance();
    } catch (InstantiationException e) {
      _log.error("InstantiationException", e);
    } catch (IllegalAccessException e) {
      _log.error("IllegalAccessException", e);
    } 
    try {
      result = mapper.readValue(jsonObject.toString(), jt);
    } catch (IOException e) {
      _log.error("IOException", e);
    } 
    if (result != null)
      BeanUtils.copyProperties(result, this, new String[0]); 
  }
  
  public JSONObject toJSONWrapper(List<? extends BaseJSONDto> dtos, String wrapperName, String itemName) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    try {
      int i = 0;
      if (dtos != null)
        for (BaseJSONDto dto : dtos) {
          JSONObject wrapperObject = new JSONObject();
          wrapperObject.put(itemName, dto.toJSON());
          jsonArray.put(i, wrapperObject);
          i++;
        }  
      jsonObject.put(wrapperName, jsonArray);
    } catch (JSONException e) {
      e.printStackTrace();
    } 
    return jsonObject;
  }
  
  protected String getJSONWrapperName() {
    String wrapperName = "";
    try {
      XmlRootElement xmlRootElement = getClass().<XmlRootElement>getAnnotation(XmlRootElement.class);
      if (xmlRootElement != null)
        wrapperName = xmlRootElement.name(); 
    } catch (RuntimeException re) {
      wrapperName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName());
    } 
    return wrapperName;
  }
}
