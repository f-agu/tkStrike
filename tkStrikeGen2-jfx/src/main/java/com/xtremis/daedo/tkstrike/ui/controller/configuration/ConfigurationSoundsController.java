package com.xtremis.daedo.tkstrike.ui.controller.configuration;

import com.xtremis.daedo.tkstrike.orm.model.SoundVolume;
import com.xtremis.daedo.tkstrike.service.SoundConfigurationService;
import com.xtremis.daedo.tkstrike.service.SoundPlayerService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeBaseController;
import com.xtremis.daedo.tkstrike.ui.model.ISoundConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.SoundConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.SoundConfigurationItemEntry;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class ConfigurationSoundsController extends TkStrikeBaseController {
  @FXML
  private Node rootView;
  
  @FXML
  private CheckBox chkBSR;
  
  @FXML
  private ComboBox<Integer> cmbSecondsBeforeBSR;
  
  @FXML
  private ComboBox<String> cmbVolumeBSR;
  
  @FXML
  private TextField txtBSR;
  
  @FXML
  private Button btPlayBSR;
  
  @FXML
  private CheckBox chkEOT;
  
  @FXML
  private ComboBox<String> cmbVolumeEOT;
  
  @FXML
  private TextField txtEOT;
  
  @FXML
  private Button btPlayEOT;
  
  @FXML
  private CheckBox chkWBH;
  
  @FXML
  private ComboBox<String> cmbVolumeWBH;
  
  @FXML
  private TextField txtWBH;
  
  @FXML
  private Button btPlayWBH;
  
  @FXML
  private CheckBox chkWHH;
  
  @FXML
  private ComboBox<String> cmbVolumeWHH;
  
  @FXML
  private TextField txtWHH;
  
  @FXML
  private Button btPlayWHH;
  
  @FXML
  private CheckBox chkWKG;
  
  @FXML
  private ComboBox<String> cmbVolumeWKG;
  
  @FXML
  private TextField txtWKG;
  
  @FXML
  private Button btPlayWKG;
  
  @FXML
  private CheckBox chkWGJ;
  
  @FXML
  private ComboBox<String> cmbVolumeWGJ;
  
  @FXML
  private TextField txtWGJ;
  
  @FXML
  private Button btPlayWGJ;
  
  @FXML
  private CheckBox chkWTM;
  
  @FXML
  private ComboBox<String> cmbVolumeWTM;
  
  @FXML
  private TextField txtWTM;
  
  @FXML
  private Button btPlayWTM;
  
  @Autowired
  private SoundPlayerService soundPlayerService;
  
  @Autowired
  private SoundConfigurationService soundConfigurationService;
  
  private SoundConfigurationEntry soundConfigurationEntry = null;
  
  final FileChooser fileChooser = new FileChooser();
  
  public void onWindowShowEvent() {
    super.onWindowShowEvent();
    try {
      this.soundConfigurationEntry = (SoundConfigurationEntry)this.soundConfigurationService.getSoundConfigurationEntry();
    } catch (TkStrikeServiceException e) {
      e.printStackTrace();
    } 
    Assert.notNull(this.soundConfigurationEntry);
    _bindControls();
  }
  
  private void _bindControls() {
    this.chkBSR.selectedProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundBeforeStartRoundProperty().get()).enabledProperty());
    this.cmbSecondsBeforeBSR.valueProperty().bindBidirectional(this.soundConfigurationEntry.soundBeforeStartRoundSecondsBeforeProperty());
    this.cmbVolumeBSR.valueProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundBeforeStartRoundProperty().get()).soundVolumeProperty());
    this.txtBSR.textProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundBeforeStartRoundProperty().get()).nameProperty());
    this.chkEOT.selectedProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundEndOfTimeProperty().get()).enabledProperty());
    this.cmbVolumeEOT.valueProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundEndOfTimeProperty().get()).soundVolumeProperty());
    this.txtEOT.textProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundEndOfTimeProperty().get()).nameProperty());
    this.chkWBH.selectedProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenBodyHitProperty().get()).enabledProperty());
    this.cmbVolumeWBH.valueProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenBodyHitProperty().get()).soundVolumeProperty());
    this.txtWBH.textProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenBodyHitProperty().get()).nameProperty());
    this.chkWHH.selectedProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenHeadHitProperty().get()).enabledProperty());
    this.cmbVolumeWHH.valueProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenHeadHitProperty().get()).soundVolumeProperty());
    this.txtWHH.textProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenBodyHitProperty().get()).nameProperty());
    this.chkWKG.selectedProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenKyongGoProperty().get()).enabledProperty());
    this.cmbVolumeWKG.valueProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenKyongGoProperty().get()).soundVolumeProperty());
    this.txtWKG.textProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenKyongGoProperty().get()).nameProperty());
    this.chkWGJ.selectedProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenGameJeonProperty().get()).enabledProperty());
    this.cmbVolumeWGJ.valueProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenGameJeonProperty().get()).soundVolumeProperty());
    this.txtWGJ.textProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenGameJeonProperty().get()).nameProperty());
    this.chkWTM.selectedProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenTechMeetingProperty().get()).enabledProperty());
    this.cmbVolumeWTM.valueProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenTechMeetingProperty().get()).soundVolumeProperty());
    this.txtWTM.textProperty().bindBidirectional((Property)((SoundConfigurationItemEntry)this.soundConfigurationEntry.soundWhenTechMeetingProperty().get()).nameProperty());
  }
  
  public void save() {
    try {
      this.soundConfigurationService.update((ISoundConfigurationEntry)this.soundConfigurationEntry);
      doCloseThisStage();
    } catch (TkStrikeServiceException e) {
      manageException((Throwable)e, "SaveConfigurationSounds", null);
    } 
  }
  
  public void undo() {
    onWindowShowEvent();
  }
  
  public void changeBET() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundBeforeEndTime());
  }
  
  public void playBET() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundBeforeEndTime());
  }
  
  public void changeBSR() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundBeforeStartRound());
  }
  
  public void playBSR() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundBeforeStartRound());
  }
  
  public void changeEOT() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundEndOfTime());
  }
  
  public void playEOT() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundEndOfTime());
  }
  
  public void changeWBH() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundWhenBodyHit());
  }
  
  public void playWBH() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundWhenBodyHit());
  }
  
  public void changeWHH() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundWhenHeadHit());
  }
  
  public void playWHH() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundWhenHeadHit());
  }
  
  public void changeWKG() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundWhenKyongGo());
  }
  
  public void playWKG() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundWhenKyongGo());
  }
  
  public void changeWGJ() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundWhenGameJeon());
  }
  
  public void playWGJ() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundWhenGameJeon());
  }
  
  public void changeWTM() {
    _changeSoundFile(this.soundConfigurationEntry.getSoundWhenTechMeeting());
  }
  
  public void playWTM() {
    this.soundPlayerService.playSoundItem(this.soundConfigurationEntry.getSoundWhenTechMeeting());
  }
  
  private void _changeSoundFile(SoundConfigurationItemEntry soundConfigurationItemEntry) {
    if (soundConfigurationItemEntry != null) {
      this.fileChooser.setTitle(getMessage("title.selectAudio"));
      this.fileChooser.getExtensionFilters().addAll((Object[])new FileChooser.ExtensionFilter[] { new FileChooser.ExtensionFilter("MP3", new String[] { "*.mp3" }), new FileChooser.ExtensionFilter("WAV", new String[] { "*.wav" }) });
      File newSoundOrigFile = this.fileChooser.showOpenDialog((Window)getCurrentStage());
      if (newSoundOrigFile != null && 
        newSoundOrigFile.exists()) {
        File newSoundDestFile = new File("./customSounds/" + newSoundOrigFile.getName());
        try {
          if (newSoundDestFile.exists())
            FileUtils.forceDelete(newSoundDestFile); 
          FileUtils.copyFile(newSoundOrigFile, newSoundDestFile, true);
          soundConfigurationItemEntry.nameProperty().set(newSoundDestFile.getPath());
          soundConfigurationItemEntry.soundProperty().set(new AudioClip(newSoundDestFile.toURI().toURL().toString()));
        } catch (IOException e) {
          manageException(e, "changeSoundFile", getMessage("message.error.changeSoundFile", new String[] { e.getMessage() }));
        } 
      } 
    } 
  }
  
  public void initialize(URL url, ResourceBundle resourceBundle) {
    _initializeSecondsBeforeComboBox(this.cmbSecondsBeforeBSR);
    _initializeVolumeComboBox(this.cmbVolumeBSR);
    _initializeVolumeComboBox(this.cmbVolumeEOT);
    _initializeVolumeComboBox(this.cmbVolumeWBH);
    _initializeVolumeComboBox(this.cmbVolumeWHH);
    _initializeVolumeComboBox(this.cmbVolumeWKG);
    _initializeVolumeComboBox(this.cmbVolumeWGJ);
    _initializeVolumeComboBox(this.cmbVolumeWTM);
    this.btPlayBSR.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_play.png"))));
    this.btPlayEOT.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_play.png"))));
    this.btPlayWBH.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_play.png"))));
    this.btPlayWHH.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_play.png"))));
    this.btPlayWKG.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_play.png"))));
    this.btPlayWGJ.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_play.png"))));
    this.btPlayWTM.setGraphic((Node)new ImageView(new Image(getClass().getResourceAsStream("/images/ic_play.png"))));
  }
  
  public void afterPropertiesSet() throws Exception {}
  
  public Node getRootView() {
    return this.rootView;
  }
  
  private void _initializeSecondsBeforeComboBox(ComboBox<Integer> cmb) {
    cmb.getItems().add(Integer.valueOf(3));
    cmb.getItems().add(Integer.valueOf(5));
    cmb.getItems().add(Integer.valueOf(10));
    cmb.getItems().add(Integer.valueOf(15));
    cmb.getItems().add(Integer.valueOf(30));
  }
  
  private void _initializeVolumeComboBox(ComboBox<String> cmb) {
    cmb.getItems().add(SoundVolume.HIGH.toString());
    cmb.getItems().add(SoundVolume.MEDIUM.toString());
    cmb.getItems().add(SoundVolume.LOW.toString());
  }
}
