package com.xtremis.daedo.tkstrike.ui.scene;

import com.xtremis.daedo.tkstrike.orm.model.Entity;
import com.xtremis.daedo.tkstrike.orm.model.Flag;
import com.xtremis.daedo.tkstrike.ui.model.Entry;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class FlagEntry implements Entry<Flag> {
  public SimpleStringProperty id = new SimpleStringProperty(this, "id");
  
  public SimpleStringProperty name = new SimpleStringProperty(this, "name");
  
  public SimpleStringProperty abbreviation = new SimpleStringProperty(this, "abbreviation", "");
  
  public SimpleStringProperty imagePath = new SimpleStringProperty(this, "imagePath", "");
  
  public SimpleBooleanProperty showName = new SimpleBooleanProperty(this, "showName", Boolean.FALSE.booleanValue());
  
  public SimpleObjectProperty<Image> image = new SimpleObjectProperty(this, "image");
  
  public void fillByEntity(Flag entity) {
    if (entity != null) {
      this.id.set(entity.getId());
      this.name.set(entity.getName());
      this.abbreviation.set(entity.getAbbreviation());
      this.showName.set(((entity.getShowName() != null) ? entity.getShowName() : Boolean.FALSE).booleanValue());
      if (StringUtils.isNotBlank(entity.getFlagImagePath())) {
        this.imagePath.set(entity.getFlagImagePath());
        byte[] imageBytes = null;
        try {
          imageBytes = FileUtils.readFileToByteArray(new File(entity.getFlagImagePath()));
        } catch (IOException e) {
          e.printStackTrace();
        } 
        if (imageBytes != null)
          this.image.set(new Image(new ByteArrayInputStream(imageBytes))); 
      } 
    } 
  }
  
  public String getId() {
    return this.id.get();
  }
  
  public String getName() {
    return this.name.get();
  }
  
  public String getAbbreviation() {
    return this.abbreviation.get();
  }
  
  public Image getImage() {
    return (Image)this.image.get();
  }
  
  public String getImagePath() {
    return this.imagePath.get();
  }
  
  public SimpleStringProperty imagePathProperty() {
    return this.imagePath;
  }
  
  public boolean isShowName() {
    return this.showName.get();
  }
  
  public SimpleBooleanProperty showNameProperty() {
    return this.showName;
  }
}
