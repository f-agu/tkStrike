package com.xtremis.daedo.tkstrike.tools.om;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "res")
@XmlRootElement(name = "res")
@JsonIgnoreProperties({"items"})
public class BaseListResponseDto<D extends ItemDto> extends BaseJSONDto {
  @XmlTransient
  private Collection<D> items;
  
  public Collection<D> getItems() {
    return this.items;
  }
  
  public void setItems(Collection<D> items) {
    this.items = items;
  }
  
  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = super.toJSON();
    if (this.items != null) {
      JSONArray jsonArray = new JSONArray();
      for (ItemDto itemDto : this.items)
        jsonArray.put(itemDto.toJSON()); 
      jsonObject.put("items", jsonArray);
    } 
    return jsonObject;
  }
  
  public void fromJSON(String jsonStr) throws JSONException {
    JSONObject jsonObject;
    super.fromJSON(jsonStr);
    JSONObject wrappedObject = new JSONObject(jsonStr);
    String wrapperName = getJSONWrapperName();
    if (wrappedObject.has(wrapperName)) {
      jsonObject = wrappedObject.getJSONObject(wrapperName);
    } else {
      jsonObject = wrappedObject;
    } 
    if (jsonObject.has("items")) {
      JSONArray jsonArray = jsonObject.getJSONArray("items");
      LinkedHashSet<D> itemsSet = new LinkedHashSet<>(jsonArray.length());
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject itemJSON = jsonArray.getJSONObject(i);
        D item = newItemDto();
        item.fromJSON(itemJSON.toString());
        itemsSet.add(item);
      } 
      this.items = itemsSet;
    } 
  }
  
  protected D newItemDto() {
    try {
      Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
      return (D)getClass(type).newInstance();
    } catch (Exception e) {
      return null;
    } 
  }
  
  private static Class<?> getClass(Type type) {
    if (type instanceof Class)
      return (Class)type; 
    if (type instanceof ParameterizedType)
      return getClass(((ParameterizedType)type).getRawType()); 
    return null;
  }
}
