package com.xtremis.daedo.tkstrike.tools.om;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "idsWrapper")
@XmlRootElement(name = "idsWrapper")
public class IdsWrapper extends BaseJSONDto {
  @XmlTransient
  private Collection<String> ids;
  
  @XmlElement(name = "roles", required = false)
  private String locale;
  
  @XmlElement(name = "emailIdentification", required = false)
  private String emailIdentification;
  
  @XmlElementWrapper(name = "ids")
  public String[] getIdsArr() {
    String[] res = null;
    if (this.ids != null)
      res = this.ids.<String>toArray(new String[0]); 
    return res;
  }
  
  public Collection<String> getIds() {
    return this.ids;
  }
  
  public void setIds(Collection<String> ids) {
    this.ids = ids;
  }
  
  public String getLocale() {
    return this.locale;
  }
  
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    int i = 0;
    for (String id : this.ids) {
      JSONObject wrapperObject = new JSONObject();
      wrapperObject.put("id", id);
      jsonArray.put(i, wrapperObject);
      i++;
    } 
    jsonObject.put("ids", jsonArray);
    jsonObject.put("locale", this.locale);
    return jsonObject;
  }
  
  public void fromJSON(String jsonStr) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonStr);
    if (jsonObject.has("ids")) {
      JSONArray jsonArray = jsonObject.getJSONArray("ids");
      if (jsonArray != null) {
        LinkedHashSet<String> rfidIntegratedsStrings = new LinkedHashSet<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
          Object object = jsonArray.get(i);
          String id = null;
          if (object instanceof JSONObject) {
            JSONObject jsonObjectRfid = jsonArray.getJSONObject(i);
            if (jsonObjectRfid.has("id"))
              id = jsonObjectRfid.getString("id"); 
          } else if (object instanceof String) {
            id = (String)object;
          } 
          rfidIntegratedsStrings.add(id);
        } 
        setIds(rfidIntegratedsStrings);
      } 
    } 
    if (jsonObject.has("locale"))
      setLocale(jsonObject.getString("locale")); 
  }
  
  public String getEmailIdentification() {
    return this.emailIdentification;
  }
  
  public void setEmailIdentification(String emailIdentification) {
    this.emailIdentification = emailIdentification;
  }
}
