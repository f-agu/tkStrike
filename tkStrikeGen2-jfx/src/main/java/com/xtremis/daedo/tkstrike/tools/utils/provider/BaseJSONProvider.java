package com.xtremis.daedo.tkstrike.tools.utils.provider;

import com.xtremis.daedo.tkstrike.tools.om.BaseJSONDto;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jettison.json.JSONException;

public class BaseJSONProvider implements MessageBodyReader<BaseJSONDto>, MessageBodyWriter<BaseJSONDto> {
  public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return BaseJSONDto.class.isAssignableFrom(aClass);
  }
  
  public BaseJSONDto readFrom(Class<BaseJSONDto> baseJSONDtoClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
    String payload = IOUtils.toString(inputStream, "utf-8");
    try {
      BaseJSONDto res = baseJSONDtoClass.newInstance();
      res.fromJSON(payload);
      return res;
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    } 
    return null;
  }
  
  public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return BaseJSONDto.class.isAssignableFrom(aClass);
  }
  
  public long getSize(BaseJSONDto baseJSONDto, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return -1L;
  }
  
  public void writeTo(BaseJSONDto baseJSONDto, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> stringObjectMultivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
    try {
      outputStream.write(baseJSONDto.toJSON().toString().getBytes());
    } catch (JSONException e) {
      e.printStackTrace();
    } 
  }
}
