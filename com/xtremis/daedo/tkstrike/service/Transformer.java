package com.xtremis.daedo.tkstrike.service;

import java.util.List;

public interface Transformer<D, E> {
  List<D> transferToDtos(List<E> paramList);
  
  void transferToDto(E paramE, D paramD);
  
  D transferToDto(E paramE);
  
  List<E> transferToBeans(List<D> paramList);
  
  void transferToBean(D paramD, E paramE);
  
  E transferToBean(D paramD);
  
  String[] getIgnoredProperties4Copy();
}
