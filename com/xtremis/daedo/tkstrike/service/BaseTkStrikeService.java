package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public abstract class BaseTkStrikeService<E extends Entity, D extends Entry> implements TkStrikeService<E, D> {
  protected abstract JpaRepository<E, String> getRepository();
  
  protected abstract Sort getDefaultSort();
  
  protected abstract void deleteAllChild() throws TkStrikeServiceException;
  
  public List<E> findAll() throws TkStrikeServiceException {
    try {
      Sort sort = getDefaultSort();
      return getRepository().findAll(sort);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public E getById(String id) throws TkStrikeServiceException {
    try {
      return (E)getRepository().getOne(id);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public D getEntryById(String id) throws TkStrikeServiceException {
    E entity = getById(id);
    if (entity != null)
      return transform(entity); 
    return null;
  }
  
  @Transactional(readOnly = false)
  public void delete(String id) throws TkStrikeServiceException {
    if (!canDelete(id).booleanValue())
      throw new TkStrikeServiceException("Can't delete entity, have relateds!"); 
    try {
      getRepository().delete(id);
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  @Transactional(readOnly = false)
  public void deleteAll() throws TkStrikeServiceException {
    deleteAllChild();
    try {
      getRepository().deleteAll();
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public List<D> findAllEntries() throws TkStrikeServiceException {
    List<D> entries = new ArrayList<>();
    List<E> entities = findAll();
    if (entities != null)
      for (Entity entity : entities)
        entries.add(transform((E)entity));  
    return entries;
  }
  
  public D transform(E entity) throws TkStrikeServiceException {
    try {
      D d = newEntry();
      if (entity != null)
        d.fillByEntity((Entity)entity); 
      return d;
    } catch (InstantiationException|IllegalAccessException e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  private D newEntry() throws InstantiationException, IllegalAccessException {
    return (D)((Class<Entry>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1])
      .newInstance();
  }
  
  private E newEntity() throws InstantiationException, IllegalAccessException {
    return (E)((Class<Entity>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0])
      .newInstance();
  }
}
