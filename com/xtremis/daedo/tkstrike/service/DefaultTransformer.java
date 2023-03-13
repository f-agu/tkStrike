package com.xtremis.daedo.tkstrike.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;

public abstract class DefaultTransformer<D, E> implements Transformer<D, E> {
  abstract D newDto();
  
  abstract E newBean();
  
  public List<D> transferToDtos(List<E> list) {
    List<D> linkedList = new ArrayList<>(list.size());
    for (E item : list) {
      D dto = newDto();
      transferToDto(item, dto);
      linkedList.add(dto);
    } 
    return linkedList;
  }
  
  public void transferToDto(E item, D dto) {
    if (item != null && dto != null)
      BeanUtils.copyProperties(item, dto, getIgnoredProperties4Copy()); 
  }
  
  public D transferToDto(E item) {
    if (item != null) {
      D dto = newDto();
      transferToDto(item, dto);
      return dto;
    } 
    return null;
  }
  
  public List<E> transferToBeans(List<D> dtos) {
    List<E> beanList = new ArrayList<>();
    for (D dto : dtos) {
      E bean = newBean();
      transferToBean(dto, bean);
      beanList.add(bean);
    } 
    return beanList;
  }
  
  public void transferToBean(D dto, E bean) {
    BeanUtils.copyProperties(dto, bean, getIgnoredProperties4Copy());
  }
  
  public E transferToBean(D dto) {
    E bean = newBean();
    transferToBean(dto, bean);
    return bean;
  }
  
  public String[] getIgnoredProperties4Copy() {
    return null;
  }
}
