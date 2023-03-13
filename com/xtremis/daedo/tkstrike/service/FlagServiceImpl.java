package com.xtremis.daedo.tkstrike.service;

import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.orm.repository.FlagRepository;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FlagServiceImpl extends BaseTkStrikeService<Flag, FlagEntry> implements FlagService {
  @Resource
  private FlagRepository flagRepository;
  
  protected JpaRepository<Flag, String> getRepository() {
    return (JpaRepository<Flag, String>)this.flagRepository;
  }
  
  protected Sort getDefaultSort() {
    return new Sort(Sort.Direction.ASC, new String[] { "name" });
  }
  
  protected void deleteAllChild() throws TkStrikeServiceException {}
  
  public Flag getByAbbreviation(String abbreviation) throws TkStrikeServiceException {
    if (StringUtils.isNotBlank(abbreviation)) {
      List<Flag> flags = this.flagRepository.getByAbbreviation(abbreviation.toUpperCase());
      return (flags != null && !flags.isEmpty()) ? flags.get(0) : null;
    } 
    return null;
  }
  
  public FlagEntry getEntryByAbbreviation(String abbreviation) throws TkStrikeServiceException {
    Flag flag = getByAbbreviation(abbreviation);
    if (flag != null)
      return transform(flag); 
    return null;
  }
  
  @Transactional(readOnly = false)
  public Flag doGetCreateOrUpdateEntity(String flagAbbreviation, String flagName, Boolean showName) throws TkStrikeServiceException {
    if (flagAbbreviation != null) {
      Flag flag = getByAbbreviation(flagAbbreviation);
      if (flag == null)
        try {
          byte[] defaultImageBytes = _getDefaultImage();
          flag = createNew(StringUtils.isNotEmpty(flagName) ? flagName : flagAbbreviation, flagAbbreviation, (showName != null) ? showName : Boolean.FALSE, defaultImageBytes);
        } catch (IOException e) {
          throw new TkStrikeServiceException(e);
        }  
      return flag;
    } 
    return null;
  }
  
  public FlagEntry doGetCreateOrUpdateEntry(String flagAbbreviation, String flagName, Boolean showName) throws TkStrikeServiceException {
    FlagEntry res = null;
    Flag flag = doGetCreateOrUpdateEntity(flagAbbreviation, flagName, showName);
    if (flag != null) {
      res = new FlagEntry();
      res.fillByEntity(flag);
    } 
    return res;
  }
  
  private byte[] _getDefaultImage() throws IOException {
    return IOUtils.toByteArray(getClass().getResource("/images/daedo4Flag.jpg"));
  }
  
  @Transactional(readOnly = false)
  public Flag createNew(String name, String abbreviation, Boolean showName, byte[] image) throws TkStrikeServiceException {
    try {
      if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(abbreviation)) {
        Flag newFlag = new Flag();
        newFlag.setName(name);
        if (abbreviation.length() > 15)
          abbreviation = abbreviation.substring(0, 15); 
        newFlag.setAbbreviation(abbreviation.toUpperCase());
        newFlag.setShowName((showName != null) ? showName : Boolean.FALSE);
        File imageFile = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "flags/" + name + ".png");
        if (image != null && image.length > 0) {
          FileUtils.writeByteArrayToFile(imageFile, image);
        } else {
          FileUtils.writeByteArrayToFile(imageFile, _getDefaultImage());
        } 
        newFlag.setFlagImagePath(imageFile.getPath());
        return (Flag)this.flagRepository.saveAndFlush(newFlag);
      } 
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
    return null;
  }
  
  @Transactional(readOnly = false)
  public Flag update(String id, String name, String abbreviation, Boolean showName, byte[] image) throws TkStrikeServiceException {
    try {
      Flag flag = getById(id);
      if (flag != null) {
        if (StringUtils.isNotBlank(name))
          flag.setName(name); 
        if (StringUtils.isNotBlank(abbreviation)) {
          if (abbreviation.length() > 15)
            abbreviation = abbreviation.substring(0, 15); 
          flag.setAbbreviation(abbreviation.toUpperCase());
        } 
        if (showName != null)
          flag.setShowName(showName); 
        if (image != null && image.length > 0) {
          File imageFile = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + "flags/" + name + ".png");
          FileUtils.writeByteArrayToFile(imageFile, image);
          flag.setFlagImagePath(imageFile.getAbsolutePath());
        } 
        return (Flag)this.flagRepository.saveAndFlush(flag);
      } 
      return null;
    } catch (Exception e) {
      throw new TkStrikeServiceException(e);
    } 
  }
  
  public Boolean canDelete(String id) {
    return Boolean.valueOf(true);
  }
}
