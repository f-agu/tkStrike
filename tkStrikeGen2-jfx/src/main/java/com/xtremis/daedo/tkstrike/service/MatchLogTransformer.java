package com.xtremis.daedo.tkstrike.service;

public interface MatchLogTransformer<D extends com.xtremis.daedo.tkstrike.om.CommonMatchLogDto, ID extends com.xtremis.daedo.tkstrike.om.CommonMatchLogItemDto, DE extends com.xtremis.daedo.tkstrike.orm.model.MatchLogEntity> extends Transformer<D, DE> {}
