package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailTypeUtil;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.AthleteEntry;
import com.xtremis.daedo.tkstrike.ui.model.INetworkConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.model.MatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.scene.FlagEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.RestrictiveTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseScoreboardController extends CommonScoreboardController<MatchWorker> {
  private final String LABEL_RED = "label-red";
  
  private final String LABEL_BLUE = "label-blue";
  
  protected static boolean blueRequestVideo = false;
  
  protected static boolean redRequestVideo = false;
  
  @Value("${tkStrike.time2ShowVideoReplayResult}")
  private Integer time2ShowVideoReplayResult;
  
  @FXML
  private Pane pnVideoRequest;
  
  @FXML
  private Label txtVideoRequest;
  
  @FXML
  private Label txtVideoRequestResult;
  
  @FXML
  private ImageView ivVideoRequestStatus;
  
  @FXML
  private Pane pnBodyLevel;
  
  @FXML
  protected Text txtBodyLevel;
  
  @FXML
  private Pane pnHeadLevel;
  
  @FXML
  protected Text txtHeadLevel;
  
  @FXML
  private RestrictiveTextField txtChangeBodyLevel;
  
  @FXML
  private RestrictiveTextField txtChangeHeadLevel;
  
  @FXML
  private Pane pnLeftVideo;
  
  @FXML
  protected HBox pnLeftVideoContainer;
  
  @FXML
  private Pane pnRightVideo;
  
  @FXML
  protected HBox pnRightVideoContainer;
  
  @FXML
  protected ImageView ivLeftRoundsWinsVideo;
  
  @FXML
  protected ImageView ivRightRoundsWinsVideo;
  
  @Autowired
  protected MatchWorker matchWorker;
  
  protected Pane pnVideoRequest() {
    return this.pnVideoRequest;
  }
  
  protected Label txtVideoRequest() {
    return this.txtVideoRequest;
  }
  
  protected Pane pnLeftVideo() {
    return this.pnLeftVideo;
  }
  
  protected Pane pnRightVideo() {
    return this.pnRightVideo;
  }
  
  MatchWorker getMatchWorker() {
    return this.matchWorker;
  }
  
  protected final void _commonInternalOnWindowShowEvent() {
    _internalOnWindowShowEvent();
  }
  
  protected final void _commonInternalBindUIControls() {
    MatchConfigurationEntry current = (MatchConfigurationEntry)getAppStatusWorker().getMatchConfigurationEntry();
    boolean showPnHeadLevel = true;
    boolean showPnBodyLevel = true;
    INetworkConfigurationEntry networkConfigurationEntry = null;
    try {
      networkConfigurationEntry = getAppStatusWorker().getNetworkConfigurationEntry();
    } catch (TkStrikeServiceException e) {
      manageException((Throwable)e, "BaseScoreboard", null);
    } 
    if (networkConfigurationEntry != null && networkConfigurationEntry
      .getGroup1Config() != null && 
      !networkConfigurationEntry.getGroup1Config().getHeadSensorsEnabled().booleanValue())
      showPnHeadLevel = false; 
    if (networkConfigurationEntry != null && networkConfigurationEntry
      .getGroup1Config() != null && 
      !networkConfigurationEntry.getGroup1Config().getBodySensorsEnabled().booleanValue())
      showPnBodyLevel = false; 
    final boolean fnShowPnHeadLevel = showPnHeadLevel;
    Platform.runLater(new Runnable() {
          public void run() {
            BaseScoreboardController.this.pnHeadLevel.setVisible(fnShowPnHeadLevel);
          }
        });
    final boolean fnShowPnBodyLevel = showPnBodyLevel;
    Platform.runLater(new Runnable() {
          public void run() {
            BaseScoreboardController.this.pnBodyLevel.setVisible(fnShowPnBodyLevel);
          }
        });
    if (getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.NOT_READY) || 
      getMatchWorker().getCurrentMatchStatus().equals(MatchStatusId.WAITING_4_MATCH))
      getRoundCountdownController().forceChangeCountdown(current.getRoundsConfig().getRoundTimeStr()); 
    Integer byCatBodyLevel = Integer.valueOf(current.getCategory().getBodyLevel());
    Integer byCatHeadLevel = Integer.valueOf(current.getCategory().getHeadLevel());
    Integer byWorkerBodyLevel = Integer.valueOf(getMatchWorker().getMinBodyLevel());
    Integer byWorkerHeadLevel = Integer.valueOf(getMatchWorker().getMinHeadLevel());
    this.txtBodyLevel.setText("" + ((byWorkerBodyLevel.intValue() != byCatBodyLevel.intValue()) ? (String)byWorkerBodyLevel : (String)byCatBodyLevel));
    this.txtHeadLevel.setText("" + ((byWorkerHeadLevel.intValue() != byCatHeadLevel.intValue()) ? (String)byWorkerHeadLevel : (String)byCatHeadLevel));
    if (current.getPhase() != null && current.getCategory() != null && current.getCategory().getSubCategory() != null) {
      txtMatchNumber().setText(current.getMatchNumber());
      lblMatchConfig().setText(current.getCategory().getSubCategory().getName() + " - " + current
          .getPhase().getName() + " " + current
          .getCategory().getName());
    } 
    if (isBlueOnLeft()) {
      putAthleteInfo(txtLeftName(), txtLeftAbbr(), pnLeftFlag(), pnLeftVideo(), (AthleteEntry)current.blueAthleteProperty().get(), (current.getBlueAthleteVideoQuota() > 0), Integer.valueOf(current.getBlueAthleteVideoQuota()));
      putAthleteInfo(txtRightName(), txtRightAbbr(), pnRightFlag(), pnRightVideo(), (AthleteEntry)current.redAthleteProperty().get(), (current.getRedAthleteVideoQuota() > 0), Integer.valueOf(current.getRedAthleteVideoQuota()));
    } else {
      putAthleteInfo(txtLeftName(), txtLeftAbbr(), pnLeftFlag(), pnLeftVideo(), (AthleteEntry)current.redAthleteProperty().get(), (current.getRedAthleteVideoQuota() > 0), Integer.valueOf(current.getRedAthleteVideoQuota()));
      putAthleteInfo(txtRightName(), txtRightAbbr(), pnRightFlag(), pnRightVideo(), (AthleteEntry)current.blueAthleteProperty().get(), (current.getBlueAthleteVideoQuota() > 0), Integer.valueOf(current.getBlueAthleteVideoQuota()));
    } 
    _internalBindUIControls();
  }
  
  protected void _commonInternalInitialize(URL url, ResourceBundle resourceBundle) {
    _internalInitialize(url, resourceBundle);
  }
  
  protected final void _commonInternalAfterPropertiesSet() throws Exception {
    this.ivLeftRoundsWinsVideo.setImage(IMAGE_VIDEO);
    this.ivRightRoundsWinsVideo.setImage(IMAGE_VIDEO);
    this.pnHeadLevel.getChildren().add(0, TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().getNode4HeadImpact(this.scoreboardGraphicDetailType, Double.valueOf(40.0D)));
    this.pnBodyLevel.getChildren().add(0, TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().getNode4BodyImpact(this.scoreboardGraphicDetailType, Double.valueOf(40.0D)));
    this.matchWorker.minBodyLevelProperty().addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, final Number t1) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    BaseScoreboardController.this.txtBodyLevel.setText("" + t1.intValue());
                  }
                });
          }
        });
    this.matchWorker.minHeadLevelProperty().addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observableValue, Number number, final Number t1) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    BaseScoreboardController.this.txtHeadLevel.setText("" + t1.intValue());
                  }
                });
          }
        });
    this.matchWorker.blueVideoQuotaProperty().addListener(new VideoQuotaPropertyChangeListener(true));
    this.matchWorker.redVideoQuotaProperty().addListener(new VideoQuotaPropertyChangeListener(false));
    this.matchWorker.blueRequestVideoScoreboardProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean requested) {
            if (requested.booleanValue()) {
              BaseScoreboardController.this.showVideoRequest(true);
            } else {
              BaseScoreboardController.this.hideVideoRequest();
              BaseScoreboardController.this.showVideoRequestResult(true, (MatchWorker.VideoRequestResult)BaseScoreboardController.this.matchWorker.blueVideoRequestResult().getValue());
            } 
          }
        });
    this.matchWorker.redRequestVideoScoreboardProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean requested) {
            if (requested.booleanValue()) {
              BaseScoreboardController.this.showVideoRequest(false);
            } else {
              BaseScoreboardController.this.hideVideoRequest();
              BaseScoreboardController.this.showVideoRequestResult(false, (MatchWorker.VideoRequestResult)BaseScoreboardController.this.matchWorker.redVideoRequestResult().getValue());
            } 
          }
        });
    pnVideoRequest().setVisible(false);
    _internalAfterPropertiesSet();
  }
  
  private void putAthleteInfo(Label txtName, Text txtAbbr, Pane pnFlag, Pane pnVideo, AthleteEntry athleteEntry, boolean videoEnabled, Integer videoQuota) {
    if (StringUtils.isNotBlank(athleteEntry.getScoreboardName())) {
      txtName.setText((athleteEntry
          .getScoreboardName() != null && athleteEntry
          .getScoreboardName().length() >= 16) ? (athleteEntry
          .getScoreboardName().substring(0, 14) + ".") : athleteEntry
          .getScoreboardName());
    } else {
      txtName.setText("");
    } 
    pnFlag.getChildren().clear();
    if (athleteEntry.flag.get() != null)
      if (((FlagEntry)athleteEntry.flag.get()).isShowName() || getAppStatusWorker().getRulesEntry().getForceShowName().booleanValue()) {
        pnFlag.setMaxWidth(0.0D);
        pnFlag.setMinWidth(0.0D);
        pnFlag.setPrefWidth(0.0D);
        txtName.setMaxWidth(465.0D);
        txtName.setMinWidth(465.0D);
        txtName.setPrefWidth(465.0D);
        if (StringUtils.isNotBlank(athleteEntry.getScoreboardName()) && StringUtils.isNotBlank(athleteEntry.getFlag().getName())) {
          txtName.setText("[" + ((FlagEntry)athleteEntry.flag
              .get()).getName() + "] " + ((athleteEntry.getScoreboardName() != null && athleteEntry.getScoreboardName().length() >= 16) ? (athleteEntry.getScoreboardName().substring(0, 14) + ".") : athleteEntry.getScoreboardName()));
        } else {
          txtName.setText("");
        } 
        txtAbbr.setText("");
        txtAbbr.setVisible(false);
      } else if (((FlagEntry)athleteEntry.flag.get()).getImage() != null) {
        pnFlag.setMaxWidth(110.0D);
        pnFlag.setMinWidth(110.0D);
        pnFlag.setPrefWidth(110.0D);
        txtName.setMaxWidth(355.0D);
        txtName.setMinWidth(355.0D);
        txtName.setPrefWidth(355.0D);
        ImageView iv = new ImageView(((FlagEntry)athleteEntry.flag.get()).getImage());
        iv.setFitHeight(getFlagImageHeight());
        iv.setPreserveRatio(true);
        pnFlag.getChildren().add(iv);
        if (athleteEntry.getFlag().getAbbreviation().length() > 3) {
          txtAbbr.setText(athleteEntry.getFlag().getAbbreviation().substring(0, 3));
        } else {
          txtAbbr.setText(athleteEntry.getFlag().getAbbreviation());
        } 
        txtAbbr.setVisible(true);
      }  
    HBox hBox = (HBox)pnVideo.getChildren().get(0);
    hBox.getChildren().clear();
    if (videoEnabled && videoQuota.intValue() > 0) {
      for (int i = 1; i <= videoQuota.intValue(); i++) {
        ImageView imageView = new ImageView(IMAGE_VIDEO);
        imageView.setFitHeight(getVideoImageHeight());
        imageView.setPreserveRatio(true);
        hBox.getChildren().add(imageView);
      } 
    } else if (showNoVideoImage()) {
      ImageView imageView = new ImageView(IMAGE_NO_VIDEO);
      imageView.setFitHeight(getVideoImageHeight());
      imageView.setPreserveRatio(true);
      hBox.getChildren().add(imageView);
    } 
    pnVideo.setUserData(Boolean.valueOf(videoEnabled));
  }
  
  protected final void _commonInternalOnWindowCloseEvent() {}
  
  protected final void _commonInternalUnbindUIControls() {}
  
  private void showVideoRequest(final boolean blue) {
    Platform.runLater(new Runnable() {
          public void run() {
            BaseScoreboardController.this.ivVideoRequestStatus.setImage(new Image(getClass().getResourceAsStream("/images/imageVideoRequest-df.png")));
            BaseScoreboardController.this.txtVideoRequest.getStyleClass().removeAll((Object[])new String[] { "label-blue", "label-red" });
            BaseScoreboardController.this.txtVideoRequest.getStyleClass().addAll((Object[])new String[] { this.val$blue ? "label-blue" : "label-red" });
            BaseScoreboardController.this.txtVideoRequest.setText(blue ? BaseScoreboardController.this.getMessage("label.blue").toUpperCase() : BaseScoreboardController.this.getMessage("label.red").toUpperCase());
            BaseScoreboardController.this.txtVideoRequestResult.setText("");
            BaseScoreboardController.this.pnVideoRequest().setVisible(true);
          }
        });
  }
  
  private void hideVideoRequest() {
    Platform.runLater(new Runnable() {
          public void run() {
            BaseScoreboardController.this.pnVideoRequest().setVisible(false);
          }
        });
  }
  
  private void showVideoRequestResult(final boolean blue, final MatchWorker.VideoRequestResult videoRequestResult) {
    Platform.runLater(new Runnable() {
          public void run() {
            if (videoRequestResult != null && 
              !MatchWorker.VideoRequestResult.CLOSED.equals(videoRequestResult)) {
              boolean accepted = MatchWorker.VideoRequestResult.ACCEPTED.equals(videoRequestResult);
              BaseScoreboardController.this.ivVideoRequestStatus.setImage(new Image(getClass().getResourceAsStream(accepted ? "/images/ico-video-accepted.png" : "/images/ico-video-rejected.png")));
              BaseScoreboardController.this.txtVideoRequest.getStyleClass().removeAll((Object[])new String[] { "label-blue", "label-red" });
              BaseScoreboardController.this.txtVideoRequest.getStyleClass().addAll((Object[])new String[] { this.val$blue ? "label-blue" : "label-red" });
              BaseScoreboardController.this.txtVideoRequest.setText(blue ? BaseScoreboardController.this.getMessage("label.blue").toUpperCase() : BaseScoreboardController.this.getMessage("label.red").toUpperCase());
              BaseScoreboardController.this.txtVideoRequestResult.setText(accepted ? "ACCEPTED" : "REJECTED");
              BaseScoreboardController.this.pnVideoRequest().setVisible(true);
              TkStrikeExecutors.schedule(new Runnable() {
                    public void run() {
                      BaseScoreboardController.this.hideVideoRequest();
                    }
                  },  BaseScoreboardController.this.time2ShowVideoReplayResult.intValue(), TimeUnit.SECONDS);
            } 
          }
        });
  }
  
  class VideoQuotaPropertyChangeListener implements ChangeListener<Number> {
    private boolean isBlue;
    
    public VideoQuotaPropertyChangeListener(boolean isBlue) {
      this.isBlue = isBlue;
    }
    
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
      Platform.runLater(new Runnable() {
            public void run() {
              if (!BaseScoreboardController.this.getMatchWorker().isLock().booleanValue()) {
                ImageView iv = BaseScoreboardController.this.ivLeftRoundsWinsVideo;
                Pane pnVideo = BaseScoreboardController.this.pnLeftVideo();
                if ((BaseScoreboardController.this.isBlueOnLeft() && !BaseScoreboardController.VideoQuotaPropertyChangeListener.this.isBlue) || (BaseScoreboardController.VideoQuotaPropertyChangeListener.this.isBlue && !BaseScoreboardController.this.isBlueOnLeft())) {
                  pnVideo = BaseScoreboardController.this.pnRightVideo();
                  iv = BaseScoreboardController.this.ivRightRoundsWinsVideo;
                } 
                iv.setVisible((newValue != null && newValue.intValue() > 0));
                if (pnVideo.getChildren() != null && pnVideo
                  .getChildren().size() >= 1 && pnVideo
                  .getChildren().get(0) instanceof HBox) {
                  HBox hBox = (HBox)pnVideo.getChildren().get(0);
                  int n = newValue.intValue();
                  hBox.getChildren().clear();
                  if (n > 0) {
                    if (n > 4)
                      n = 4; 
                    for (int i = 1; i <= n; i++) {
                      ImageView imageView = new ImageView(CommonTkStrikeBaseController.IMAGE_VIDEO);
                      imageView.setId("iv" + System.currentTimeMillis());
                      imageView.setFitHeight(BaseScoreboardController.this.getVideoImageHeight());
                      imageView.setPreserveRatio(true);
                      hBox.getChildren().add(imageView);
                    } 
                  } else if (BaseScoreboardController.this.showNoVideoImage()) {
                    ImageView imageView = new ImageView(CommonTkStrikeBaseController.IMAGE_NO_VIDEO);
                    imageView.setFitHeight(BaseScoreboardController.this.getVideoImageHeight());
                    imageView.setPreserveRatio(true);
                    hBox.getChildren().add(imageView);
                  } 
                } 
                pnVideo.setUserData(Boolean.valueOf((newValue.intValue() > 0)));
              } 
            }
          });
    }
  }
  
  protected boolean cleanPointsWhenGoldenPointEnabled() {
    return true;
  }
  
  protected abstract double getVideoImageHeight();
  
  protected abstract void _internalOnWindowShowEvent();
  
  protected abstract void _internalBindUIControls();
  
  protected abstract void _internalInitialize(URL paramURL, ResourceBundle paramResourceBundle);
  
  protected abstract void _internalAfterPropertiesSet() throws Exception;
  
  protected abstract void _internalOnWindowCloseEvent();
  
  protected abstract void _internalUnbindUIControls();
  
  protected abstract boolean showNoVideoImage();
}
