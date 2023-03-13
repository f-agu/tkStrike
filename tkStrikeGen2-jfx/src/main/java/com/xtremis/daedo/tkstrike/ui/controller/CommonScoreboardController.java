package com.xtremis.daedo.tkstrike.ui.controller;

import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailType;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailTypeUtil;
import com.xtremis.daedo.tkstrike.om.AppStatusId;
import com.xtremis.daedo.tkstrike.om.MatchStatusId;
import com.xtremis.daedo.tkstrike.om.NetworkErrorCause;
import com.xtremis.daedo.tkstrike.om.combat.BestOf3RoundSuperiority;
import com.xtremis.daedo.tkstrike.om.combat.FinalDecision;
import com.xtremis.daedo.tkstrike.om.combat.HitEventType;
import com.xtremis.daedo.tkstrike.om.combat.HitEventValidator;
import com.xtremis.daedo.tkstrike.om.combat.HitJudgeStatus;
import com.xtremis.daedo.tkstrike.om.combat.MatchWinner;
import com.xtremis.daedo.tkstrike.service.CommonMatchWorker;
import com.xtremis.daedo.tkstrike.service.ExternalConfigService;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.ei.om.MatchVictoryCriteria;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.model.ExternalConfigEntry;
import com.xtremis.daedo.tkstrike.ui.model.IMatchConfigurationEntry;
import com.xtremis.daedo.tkstrike.ui.scene.control.TkStrikeCombatHit;
import com.xtremis.daedo.tkstrike.utils.ScoreboardBackgroundType;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class CommonScoreboardController<MW extends CommonMatchWorker> extends CommonTkStrikeBaseController {
  private static final Image IMAGE_HEAD_HIT = new Image(CommonScoreboardController.class.getResourceAsStream("/images/headGearLittle.png"));
  
  private static final Image IMAGE_BODY_HIT = new Image(CommonScoreboardController.class.getResourceAsStream("/images/trunkLittle.png"));
  
  private static final Image IMAGE_CHECK = new Image(CommonScoreboardController.class.getResourceAsStream("/images/check.png"));
  
  @Value("${tkStrike.showTotalPenaltiesOnGoldenPoint}")
  private Boolean showTotalPenaltiesOnGoldenPoint;
  
  @Value("${tkStrike.scoreboard.graphicDetailType}")
  protected TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType;
  
  @Value("${tkStrike.scoreboard.showImpactWithFlash}")
  private Boolean showImpactWithFlash;
  
  @Value("${tkStrike.showWinnerInfoWhenGoldenPointFinish:false}")
  private Boolean showWinnerInfoWhenGoldenPointFinish;
  
  @Value("${tkStrike.scoreboard.backgroundType}")
  private ScoreboardBackgroundType scoreboardBackgroundType;
  
  @Value("${tkStrike.scoreboard.customize.combatInfo.fontSize}")
  private String combatInfoFontSize;
  
  @Value("${tkStrike.scoreboard.customize.combatInfo.fontFamily}")
  private String combatInfoFontFamily;
  
  @Value("${tkStrike.scoreboard.textImpactExtraStyleClassExtra}")
  private String scoreboardTextImpactStyleClassExtra;
  
  @FXML
  private Pane pnPointGap;
  
  @FXML
  private Label lblPointGapWinner;
  
  @FXML
  private Pane pnPointGapBOF3;
  
  @FXML
  private Label lblBestOf3PointGap;
  
  @FXML
  private Pane pnKyeShi;
  
  @FXML
  private Text lblKyeShiTime;
  
  @FXML
  private Label lblDoctor;
  
  @FXML
  private Pane pnTimeout;
  
  @FXML
  private Pane pnMatchNumber;
  
  @FXML
  private Pane pnNetworkError;
  
  @FXML
  private Label lblNetworkError1;
  
  @FXML
  private Label lblNetworkError2;
  
  @FXML
  private ImageView ivErrorCause;
  
  @FXML
  private Pane pnRest;
  
  @FXML
  private Label lblRest;
  
  @FXML
  private Text txtRestTime;
  
  @FXML
  private Pane pnShowWinner;
  
  private SimpleBooleanProperty showWinnerProperty = new SimpleBooleanProperty(this, "showWinnerProperty", Boolean.FALSE.booleanValue());
  
  @FXML
  private Pane pnScoreboard;
  
  @FXML
  private Label lblFinalDecission;
  
  @FXML
  private Label lblFinalBlueResult;
  
  @FXML
  private Label lblFinalRedResult;
  
  @FXML
  private Label lblMatchWinnerTitle;
  
  @FXML
  private Label lblWinner;
  
  @FXML
  private ImageView ivWinnerFlag;
  
  @FXML
  private Label lblMatchConfig;
  
  @FXML
  private Text txtMatchNumber;
  
  @FXML
  private StackPane pnCountdownContainer;
  
  @FXML
  private Text txtCurrentRound;
  
  @FXML
  private Pane pnLeftAthlete;
  
  @FXML
  private Text txtLeftPoints;
  
  @FXML
  private Label lblLeftPenalties;
  
  @FXML
  private Text txtLeftAbbr;
  
  @FXML
  private Label txtLeftName;
  
  @FXML
  private Pane pnLeftHits;
  
  @FXML
  private Pane pnLeftFlag;
  
  @FXML
  private HBox pnLeftLastImpact;
  
  @FXML
  private VBox pnLeftRoundWins;
  
  @FXML
  private Pane pnLeftScorePoints;
  
  @FXML
  private Pane pnLeftScoreRoundsInfo;
  
  @FXML
  private Text txtLeftRoundsWins;
  
  @FXML
  private Label lblLeftR1Points;
  
  @FXML
  private StackPane pnLeftR1Winner;
  
  @FXML
  private Label lblLeftR2Points;
  
  @FXML
  private StackPane pnLeftR2Winner;
  
  @FXML
  private Label lblLeftR3Points;
  
  @FXML
  private StackPane pnLeftR3Winner;
  
  @FXML
  private Pane pnRightAthlete;
  
  @FXML
  private Text txtRightPoints;
  
  @FXML
  private Label lblRightPenalties;
  
  @FXML
  private Text txtRightAbbr;
  
  @FXML
  private Label txtRightName;
  
  @FXML
  private Pane pnRightHits;
  
  @FXML
  private Pane pnRightFlag;
  
  @FXML
  private HBox pnRightLastImpact;
  
  @FXML
  private VBox pnRightRoundWins;
  
  @FXML
  private Pane pnRightScorePoints;
  
  @FXML
  private Pane pnRightScoreRoundsInfo;
  
  @FXML
  private Text txtRightRoundsWins;
  
  @FXML
  private Label lblRightR1Points;
  
  @FXML
  private StackPane pnRightR1Winner;
  
  @FXML
  private Label lblRightR2Points;
  
  @FXML
  private StackPane pnRightR2Winner;
  
  @FXML
  private Label lblRightR3Points;
  
  @FXML
  private StackPane pnRightR3Winner;
  
  private Label lblLeftTotalPenalties;
  
  private Label lblRightTotalPenalties;
  
  private static final String STYLECLASS_COUNTDOWN_WORKING_TEXT = "roundCountdownWorking";
  
  private static final String STYLECLASS_COUNTDOWN_WORKING_RECT = "pn-countdownWorking";
  
  protected static final String STYLECLASS_BLUE_RADIAL = "sb-blue-radial";
  
  protected static final String STYLECLASS_BLUE_PLAIN = "sb-blue-plain";
  
  protected static final String STYLECLASS_RED_RADIAL = "sb-red-radial";
  
  protected static final String STYLECLASS_RED_PLAIN = "sb-red-plain";
  
  protected static final String STYLECLASS_BLUE = "sb-blue";
  
  protected static final String STYLECLASS_LABEL_BLUE = "label-blue";
  
  protected static final String STYLECLASS_RED = "sb-red";
  
  protected static final String STYLECLASS_LABEL_RED = "label-red";
  
  protected String getBlueBackgroundStyle() {
    return ScoreboardBackgroundType.GRADIENT.equals(this.scoreboardBackgroundType) ? "sb-blue-radial" : "sb-blue-plain";
  }
  
  protected String getRedBackgroundStyle() {
    return ScoreboardBackgroundType.GRADIENT.equals(this.scoreboardBackgroundType) ? "sb-red-radial" : "sb-red-plain";
  }
  
  private Integer leftLastImpactIndex = Integer.valueOf(0);
  
  private Integer rightLastImpactIndex = Integer.valueOf(0);
  
  @FXML
  private Pane pnGoldenPointTieBreaker;
  
  @FXML
  private Label lblBlueGPTBPunches;
  
  @FXML
  private Label lblRedGPTBPunches;
  
  @FXML
  private Label lblBlueGPTBHits;
  
  @FXML
  private Label lblRedGPTBHits;
  
  @FXML
  private Label lblBlueGPTBRounds;
  
  @FXML
  private Label lblRedGPTBRounds;
  
  @FXML
  private Label lblBlueGPTBPenalties;
  
  @FXML
  private Label lblRedGPTBPenalties;
  
  @FXML
  private Label lblFinalDecissionGDPTB;
  
  @FXML
  private Label lblMatchWinnerTitleGDPTB;
  
  @FXML
  private Label lblWinnerGDPTB;
  
  @FXML
  private ImageView ivWinnerFlagGDPTB;
  
  @FXML
  private Pane pnGoldenPointTieBreakerPARA;
  
  @FXML
  private Label lblBlueGPTBTechPointsPARA;
  
  @FXML
  private Label lblRedGPTBTechPointsPARA;
  
  @FXML
  private Label lblBlueGPTBHitsPARA;
  
  @FXML
  private Label lblRedGPTBHitsPARA;
  
  @FXML
  private Label lblBlueGPTBPenaltiesPARA;
  
  @FXML
  private Label lblRedGPTBPenaltiesPARA;
  
  @FXML
  private Label lblFinalDecissionGDPTBPARA;
  
  @FXML
  private Label lblMatchWinnerTitleGDPTBPARA;
  
  @FXML
  private Label lblWinnerGDPTBPARA;
  
  @FXML
  private ImageView ivWinnerFlagGDPTBPARA;
  
  @FXML
  private Pane pnRoundSuperiorityBestOf3;
  
  @FXML
  private Label lblMatchTitleRoundSUPBestOf3;
  
  @FXML
  private Label lblRoundSUPBestOf3;
  
  @FXML
  private ImageView ivWinnerFlagRoundSUPBestOf3;
  
  @FXML
  private Label lblWinnerRoundSUPBestOf3;
  
  @FXML
  private Label lblBlueRoundSUPBestOf3TurningSpinning;
  
  @FXML
  private Label lblBlueRoundSUPBestOf3Tech;
  
  @FXML
  private Label lblBlueRoundSUPBestOf3Hits;
  
  @FXML
  private Label lblRedRoundSUPBestOf3TurningSpinning;
  
  @FXML
  private Label lblRedRoundSUPBestOf3Tech;
  
  @FXML
  private Label lblRedRoundSUPBestOf3Hits;
  
  protected Pane pnScoreboard() {
    return this.pnScoreboard;
  }
  
  protected Label lblMatchConfig() {
    return this.lblMatchConfig;
  }
  
  protected Text txtMatchNumber() {
    return this.txtMatchNumber;
  }
  
  protected Text txtLeftPoints() {
    return this.txtLeftPoints;
  }
  
  protected Text txtLeftAbbr() {
    return this.txtLeftAbbr;
  }
  
  protected Label txtLeftName() {
    return this.txtLeftName;
  }
  
  protected Pane pnLeftHits() {
    return this.pnLeftHits;
  }
  
  protected Pane pnLeftFlag() {
    return this.pnLeftFlag;
  }
  
  protected Text txtRightPoints() {
    return this.txtRightPoints;
  }
  
  protected Text txtRightAbbr() {
    return this.txtRightAbbr;
  }
  
  protected Label txtRightName() {
    return this.txtRightName;
  }
  
  protected Pane pnRightHits() {
    return this.pnRightHits;
  }
  
  protected Pane pnRightFlag() {
    return this.pnRightFlag;
  }
  
  protected Pane pnTimeout() {
    return this.pnTimeout;
  }
  
  protected Text lblKyeShiTime() {
    return this.lblKyeShiTime;
  }
  
  protected Pane pnKyeShi() {
    return this.pnKyeShi;
  }
  
  protected Pane pnRest() {
    return this.pnRest;
  }
  
  protected Label lblRest() {
    return this.lblRest;
  }
  
  protected Text txtRestTime() {
    return this.txtRestTime;
  }
  
  protected Text txtCurrentRound() {
    return this.txtCurrentRound;
  }
  
  protected Pane pnShowWinner() {
    return this.pnShowWinner;
  }
  
  protected Label lblFinalDecission() {
    return this.lblFinalDecission;
  }
  
  protected Label lblWinner() {
    return this.lblWinner;
  }
  
  protected ImageView ivWinnerFlag() {
    return this.ivWinnerFlag;
  }
  
  protected Pane pnNetworkError() {
    return this.pnNetworkError;
  }
  
  private String[] getTextImpactStyleClass() {
    return new String[] { "newTextImpact", "df-newTextImpact", this.scoreboardTextImpactStyleClassExtra };
  }
  
  private final ChangeListener<Number> penaltiesLeftListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number number, final Number t1) {
        Platform.runLater(new Runnable() {
              public void run() {
                if (t1 != null)
                  CommonScoreboardController.this.lblLeftPenalties.setText("" + t1); 
                if (CommonScoreboardController.this.showTotalPenaltiesOnGoldenPoint.booleanValue() && CommonScoreboardController.this.getMatchWorker().isGoldenPointWorking())
                  if (CommonScoreboardController.this.isBlueOnLeft()) {
                    CommonScoreboardController.this.lblLeftTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueTotalPenalties());
                  } else {
                    CommonScoreboardController.this.lblRightTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getRedTotalPenalties());
                  }  
              }
            });
      }
    };
  
  private final ChangeListener<Number> penaltiesRightListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number number, final Number t1) {
        Platform.runLater(new Runnable() {
              public void run() {
                if (t1 != null)
                  CommonScoreboardController.this.lblRightPenalties.setText("" + t1); 
                if (CommonScoreboardController.this.showTotalPenaltiesOnGoldenPoint.booleanValue() && CommonScoreboardController.this.getMatchWorker().isGoldenPointWorking())
                  if (!CommonScoreboardController.this.isBlueOnLeft()) {
                    CommonScoreboardController.this.lblLeftTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueTotalPenalties());
                  } else {
                    CommonScoreboardController.this.lblRightTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getRedTotalPenalties());
                  }  
              }
            });
      }
    };
  
  private final ChangeListener<Number> pointsLeftListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldPoints, final Number newPoints) {
        if (!MatchStatusId.WAITING_4_START_GOLDENPOINT.equals(CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus()))
          Platform.runLater(new Runnable() {
                public void run() {
                  CommonScoreboardController.this.txtLeftPoints().setText(newPoints.toString());
                }
              }); 
      }
    };
  
  private final ChangeListener<Number> pointsRightListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldPoints, final Number newPoints) {
        if (!MatchStatusId.WAITING_4_START_GOLDENPOINT.equals(CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus()))
          Platform.runLater(new Runnable() {
                public void run() {
                  CommonScoreboardController.this.txtRightPoints().setText(newPoints.toString());
                }
              }); 
      }
    };
  
  private ChangeListener<Number> roundsWinLeftListener;
  
  private ChangeListener<Number> leftR1PointsListener;
  
  private ChangeListener<Number> leftR2PointsListener;
  
  private ChangeListener<Number> leftR3PointsListener;
  
  private ChangeListener<Number> roundsWinRightListener;
  
  private ChangeListener<Number> rightR1PointsListener;
  
  private ChangeListener<Number> rightR2PointsListener;
  
  private ChangeListener<Number> rightR3PointsListener;
  
  class DefaultTextUpdateByNumberChangeListener implements ChangeListener<Number> {
    private final Text textField;
    
    public DefaultTextUpdateByNumberChangeListener(Text textField) {
      this.textField = textField;
    }
    
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
      if (newValue != null)
        Platform.runLater(new Runnable() {
              public void run() {
                CommonScoreboardController.DefaultTextUpdateByNumberChangeListener.this.textField.setText(newValue.toString());
              }
            }); 
    }
  }
  
  class DefaultLabelUpdateByNumberChangeListener implements ChangeListener<Number> {
    private final Label label;
    
    public DefaultLabelUpdateByNumberChangeListener(Label label) {
      this.label = label;
    }
    
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
      if (newValue != null)
        Platform.runLater(new Runnable() {
              public void run() {
                CommonScoreboardController.DefaultLabelUpdateByNumberChangeListener.this.label.setText(newValue.toString());
              }
            }); 
    }
  }
  
  private final ChangeListener<Number> nearMissHitsBlueListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
        Platform.runLater(new Runnable() {
              public void run() {
                if (newValue.intValue() >= 0) {
                  CommonScoreboardController.this.lblBlueGPTBHits.setText(newValue.toString());
                  CommonScoreboardController.this.lblBlueGPTBHitsPARA.setText(newValue.toString());
                } else {
                  CommonScoreboardController.this.lblBlueGPTBHits.setText("0");
                  CommonScoreboardController.this.lblBlueGPTBHitsPARA.setText("0");
                } 
              }
            });
      }
    };
  
  private final ChangeListener<Number> nearMissHitsRedListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
        Platform.runLater(new Runnable() {
              public void run() {
                if (newValue.intValue() >= 0) {
                  CommonScoreboardController.this.lblRedGPTBHits.setText(newValue.toString());
                  CommonScoreboardController.this.lblRedGPTBHitsPARA.setText(newValue.toString());
                } else {
                  CommonScoreboardController.this.lblRedGPTBHits.setText("0");
                  CommonScoreboardController.this.lblRedGPTBHitsPARA.setText("0");
                } 
              }
            });
      }
    };
  
  private ObservableList<Integer> leftImpacts2Show = FXCollections.observableArrayList();
  
  private final ChangeListener<Number> lastImpactLeftListener = new LastImpactListener(Boolean.valueOf(true));
  
  private ObservableList<Integer> rightImpacts2Show = FXCollections.observableArrayList();
  
  private final ChangeListener<Number> lastImpactRightListener = new LastImpactListener(Boolean.valueOf(false));
  
  private final ChangeListener<String> currentRoundStrListener = new ChangeListener<String>() {
      public void changed(ObservableValue<? extends String> observableValue, String oldRound, final String newRound) {
        Platform.runLater(new Runnable() {
              public void run() {
                if ("0".equals(newRound)) {
                  CommonScoreboardController.this.txtCurrentRound().setText("1");
                } else {
                  CommonScoreboardController.this.txtCurrentRound().setText(newRound);
                } 
                CommonScoreboardController.this.pnLeftHits().getChildren().clear();
                CommonScoreboardController.this.pnRightHits().getChildren().clear();
                if (MatchVictoryCriteria.CONVENTIONAL.equals(CommonScoreboardController.this.getMatchWorker().getMatchVictoryCriteria())) {
                  CommonScoreboardController.this.clearImpactInScore((Pane)CommonScoreboardController.this.pnLeftLastImpact);
                  CommonScoreboardController.this.clearImpactInScore((Pane)CommonScoreboardController.this.pnRightLastImpact);
                  CommonScoreboardController.this.leftImpacts2Show.clear();
                  CommonScoreboardController.this.rightImpacts2Show.clear();
                } 
              }
            });
      }
    };
  
  @Autowired
  private ExternalConfigService externalConfigService;
  
  ExternalConfigEntry currentExternalConfig = new ExternalConfigEntry();
  
  private Timeline flashLeftImpact1;
  
  private Timeline flashLeftImpact2;
  
  private Timeline flashRightImpact1;
  
  private Timeline flashRightImpact2;
  
  public final void onWindowShowEvent() {
    super.onWindowShowEvent();
    try {
      this.currentExternalConfig = this.externalConfigService.getExternalConfigEntry();
    } catch (TkStrikeServiceException e) {
      e.printStackTrace();
    } 
    if (this.currentExternalConfig != null) {
      setBlueOnLeft(this.currentExternalConfig.getExtScoreboardBlueOnLeft());
      this.pnLeftAthlete.getStyleClass().removeAll((Object[])new String[] { "sb-blue-radial", "sb-red-radial", "sb-blue-plain", "sb-red-plain" });
      pnLeftFlag().getChildren().clear();
      this.pnRightAthlete.getStyleClass().removeAll((Object[])new String[] { "sb-blue-radial", "sb-red-radial", "sb-blue-plain", "sb-red-plain" });
      pnRightFlag().getChildren().clear();
      if (isBlueOnLeft()) {
        this.pnLeftAthlete.getStyleClass().addAll((Object[])new String[] { getBlueBackgroundStyle() });
        this.pnRightAthlete.getStyleClass().addAll((Object[])new String[] { getRedBackgroundStyle() });
      } else {
        this.pnLeftAthlete.getStyleClass().addAll((Object[])new String[] { getRedBackgroundStyle() });
        this.pnRightAthlete.getStyleClass().addAll((Object[])new String[] { getBlueBackgroundStyle() });
      } 
    } 
    bindUIControls();
    _commonInternalOnWindowShowEvent();
  }
  
  final void bindUIControls() {
    IMatchConfigurationEntry current = getAppStatusWorker().getMatchConfigurationEntry();
    setBlueOnLeft(this.currentExternalConfig.getExtScoreboardBlueOnLeft());
    getMatchWorker().bluePenaltiesProperty().removeListener(this.penaltiesLeftListener);
    getMatchWorker().bluePenaltiesProperty().removeListener(this.penaltiesRightListener);
    getMatchWorker().redPenaltiesProperty().removeListener(this.penaltiesRightListener);
    getMatchWorker().redPenaltiesProperty().removeListener(this.penaltiesLeftListener);
    getMatchWorker().blueGeneralPointsProperty().removeListener(this.pointsLeftListener);
    getMatchWorker().blueGeneralPointsProperty().removeListener(this.pointsRightListener);
    getMatchWorker().redGeneralPointsProperty().removeListener(this.pointsLeftListener);
    getMatchWorker().redGeneralPointsProperty().removeListener(this.pointsRightListener);
    getMatchWorker().blueLastImpactValueProperty().removeListener(this.lastImpactLeftListener);
    getMatchWorker().blueLastImpactValueProperty().removeListener(this.lastImpactRightListener);
    getMatchWorker().redLastImpactValueProperty().removeListener(this.lastImpactLeftListener);
    getMatchWorker().redLastImpactValueProperty().removeListener(this.lastImpactRightListener);
    getMatchWorker().blueRoundsWinProperty().removeListener(this.roundsWinRightListener);
    getMatchWorker().blueRoundsWinProperty().removeListener(this.roundsWinLeftListener);
    getMatchWorker().blueR1PointsProperty().removeListener(this.leftR1PointsListener);
    getMatchWorker().blueR1PointsProperty().removeListener(this.rightR1PointsListener);
    getMatchWorker().blueR2PointsProperty().removeListener(this.leftR2PointsListener);
    getMatchWorker().blueR2PointsProperty().removeListener(this.rightR2PointsListener);
    getMatchWorker().blueR3PointsProperty().removeListener(this.leftR3PointsListener);
    getMatchWorker().blueR3PointsProperty().removeListener(this.rightR3PointsListener);
    getMatchWorker().redRoundsWinProperty().removeListener(this.roundsWinLeftListener);
    getMatchWorker().redRoundsWinProperty().removeListener(this.roundsWinRightListener);
    getMatchWorker().redR1PointsProperty().removeListener(this.leftR1PointsListener);
    getMatchWorker().redR1PointsProperty().removeListener(this.rightR1PointsListener);
    getMatchWorker().redR2PointsProperty().removeListener(this.leftR2PointsListener);
    getMatchWorker().redR2PointsProperty().removeListener(this.rightR2PointsListener);
    getMatchWorker().redR3PointsProperty().removeListener(this.leftR3PointsListener);
    getMatchWorker().redR3PointsProperty().removeListener(this.rightR3PointsListener);
    Integer maxGamJeomsAllowed = getMatchWorker().getMaxGamJeomsAllowed();
    if (isBlueOnLeft()) {
      getMatchWorker().bluePenaltiesProperty().addListener(this.penaltiesLeftListener);
      getMatchWorker().redPenaltiesProperty().addListener(this.penaltiesRightListener);
      getMatchWorker().blueGeneralPointsProperty().addListener(this.pointsLeftListener);
      txtLeftPoints().setText("" + getMatchWorker().getBlueGeneralPoints());
      getMatchWorker().redGeneralPointsProperty().addListener(this.pointsRightListener);
      txtRightPoints().setText("" + getMatchWorker().getRedGeneralPoints());
      if (MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria())) {
        getMatchWorker().blueRoundsWinProperty().addListener(this.roundsWinLeftListener);
        getMatchWorker().redRoundsWinProperty().addListener(this.roundsWinRightListener);
        getMatchWorker().blueR1PointsProperty().addListener(this.leftR1PointsListener);
        getMatchWorker().blueR2PointsProperty().addListener(this.leftR2PointsListener);
        getMatchWorker().blueR3PointsProperty().addListener(this.leftR3PointsListener);
        getMatchWorker().redR1PointsProperty().addListener(this.rightR1PointsListener);
        getMatchWorker().redR2PointsProperty().addListener(this.rightR2PointsListener);
        getMatchWorker().redR3PointsProperty().addListener(this.rightR3PointsListener);
        ((StackPane)this.pnLeftLastImpact.getChildren().get(0)).getChildren().clear();
        ((StackPane)this.pnLeftLastImpact.getChildren().get(1)).getChildren().clear();
        ((StackPane)this.pnRightLastImpact.getChildren().get(0)).getChildren().clear();
        ((StackPane)this.pnRightLastImpact.getChildren().get(1)).getChildren().clear();
        Platform.runLater(new Runnable() {
              public void run() {
                ((StackPane)CommonScoreboardController.this.pnLeftLastImpact.getChildren().get(0)).getChildren().add(new CommonScoreboardController.BestOf3SupLeadPane(CommonScoreboardController.this
                      .getMatchWorker().bestOf3CurrentRoundPartialWinner(), true));
                ((StackPane)CommonScoreboardController.this.pnRightLastImpact.getChildren().get(0)).getChildren().add(new CommonScoreboardController.BestOf3SupLeadPane(CommonScoreboardController.this
                      .getMatchWorker().bestOf3CurrentRoundPartialWinner(), false));
                ((StackPane)CommonScoreboardController.this.pnLeftLastImpact.getChildren().get(1)).getChildren().add(new CommonScoreboardController.BestOf3HitsPane(CommonScoreboardController.this.getMatchWorker().blueNearMissHitsProperty()));
                ((StackPane)CommonScoreboardController.this.pnRightLastImpact.getChildren().get(1)).getChildren().add(new CommonScoreboardController.BestOf3HitsPane(CommonScoreboardController.this.getMatchWorker().redNearMissHitsProperty()));
              }
            });
      } else {
        getMatchWorker().blueLastImpactValueProperty().addListener(this.lastImpactLeftListener);
        getMatchWorker().redLastImpactValueProperty().addListener(this.lastImpactRightListener);
      } 
    } else {
      getMatchWorker().bluePenaltiesProperty().addListener(this.penaltiesRightListener);
      getMatchWorker().redPenaltiesProperty().addListener(this.penaltiesLeftListener);
      getMatchWorker().blueGeneralPointsProperty().addListener(this.pointsRightListener);
      txtLeftPoints().setText("" + getMatchWorker().getRedGeneralPoints());
      getMatchWorker().redGeneralPointsProperty().addListener(this.pointsLeftListener);
      txtRightPoints().setText("" + getMatchWorker().getBlueGeneralPoints());
      if (MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria())) {
        getMatchWorker().blueRoundsWinProperty().addListener(this.roundsWinRightListener);
        getMatchWorker().redRoundsWinProperty().addListener(this.roundsWinLeftListener);
        getMatchWorker().blueR1PointsProperty().addListener(this.rightR1PointsListener);
        getMatchWorker().blueR2PointsProperty().addListener(this.rightR2PointsListener);
        getMatchWorker().blueR3PointsProperty().addListener(this.rightR3PointsListener);
        getMatchWorker().redR1PointsProperty().addListener(this.leftR1PointsListener);
        getMatchWorker().redR2PointsProperty().addListener(this.leftR2PointsListener);
        getMatchWorker().redR3PointsProperty().addListener(this.leftR3PointsListener);
        Platform.runLater(new Runnable() {
              public void run() {
                ((StackPane)CommonScoreboardController.this.pnLeftLastImpact.getChildren().get(0)).getChildren().add(new CommonScoreboardController.BestOf3SupLeadPane(CommonScoreboardController.this
                      .getMatchWorker().bestOf3CurrentRoundPartialWinner(), false));
                ((StackPane)CommonScoreboardController.this.pnRightLastImpact.getChildren().get(0)).getChildren().add(new CommonScoreboardController.BestOf3SupLeadPane(CommonScoreboardController.this
                      .getMatchWorker().bestOf3CurrentRoundPartialWinner(), true));
                ((StackPane)CommonScoreboardController.this.pnLeftLastImpact.getChildren().get(1)).getChildren().add(new CommonScoreboardController.BestOf3HitsPane(CommonScoreboardController.this.getMatchWorker().redNearMissHitsProperty()));
                ((StackPane)CommonScoreboardController.this.pnRightLastImpact.getChildren().get(1)).getChildren().add(new CommonScoreboardController.BestOf3HitsPane(CommonScoreboardController.this.getMatchWorker().blueNearMissHitsProperty()));
              }
            });
      } else {
        getMatchWorker().blueLastImpactValueProperty().addListener(this.lastImpactRightListener);
        getMatchWorker().redLastImpactValueProperty().addListener(this.lastImpactLeftListener);
      } 
    } 
    if (this.showTotalPenaltiesOnGoldenPoint.booleanValue()) {
      this.lblLeftTotalPenalties.setVisible(false);
      this.lblRightTotalPenalties.setVisible(false);
    } 
    _refreshRoundsWinner(Integer.valueOf(getMatchWorker().getCurrentRound()));
    _commonInternalBindUIControls();
  }
  
  public final EventHandler<WindowEvent> getOnWindowCloseEventHandler() {
    return new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
          CommonScoreboardController.this.unbindUIControls();
          CommonScoreboardController.this._commonInternalOnWindowCloseEvent();
        }
      };
  }
  
  private final void unbindUIControls() {
    txtLeftName().setText("");
    txtLeftAbbr().textProperty().unbind();
    txtRightName().setText("");
    txtRightAbbr().textProperty().unbind();
    _commonInternalUnbindUIControls();
  }
  
  private void clearImpactInScore(Pane impactPane) {
    ((Pane)impactPane.getChildren().get(0)).getChildren().clear();
    ((Node)impactPane.getChildren().get(0)).setUserData(null);
    ((Pane)impactPane.getChildren().get(1)).getChildren().clear();
    ((Node)impactPane.getChildren().get(1)).setUserData(null);
  }
  
  public final void initialize(URL url, ResourceBundle resourceBundle) {
    pnLeftHits().getChildren().addListener(new ListChangeListener<Node>() {
          public void onChanged(ListChangeListener.Change<? extends Node> change) {
            if (CommonScoreboardController.this.pnLeftHits().getChildren().size() == 9)
              CommonScoreboardController.this.pnLeftHits().getChildren().remove(0); 
          }
        });
    pnRightHits().getChildren().addListener(new ListChangeListener<Node>() {
          public void onChanged(ListChangeListener.Change<? extends Node> change) {
            if (CommonScoreboardController.this.pnRightHits().getChildren().size() == 9)
              CommonScoreboardController.this.pnRightHits().getChildren().remove(0); 
          }
        });
    _commonInternalInitialize(url, resourceBundle);
  }
  
  private Timeline addFlash(StackPane impactPane, Timeline flash) {
    flash = new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(600.0D), new KeyValue[] { new KeyValue((WritableValue)impactPane.visibleProperty(), Boolean.valueOf(false)) }), new KeyFrame(Duration.millis(900.0D), new KeyValue[] { new KeyValue((WritableValue)impactPane.visibleProperty(), Boolean.valueOf(true)) }) });
    flash.setDelay(Duration.seconds(0.5D));
    flash.setCycleCount(3);
    flash.setAutoReverse(false);
    return flash;
  }
  
  private void putRoundsWinnerInfoInRest() {
    StackPane pnBlueR1Wins = isBlueOnLeft() ? this.pnLeftR1Winner : this.pnRightR1Winner;
    StackPane pnBlueR2Wins = isBlueOnLeft() ? this.pnLeftR2Winner : this.pnRightR2Winner;
    StackPane pnBlueR3Wins = isBlueOnLeft() ? this.pnLeftR3Winner : this.pnRightR3Winner;
    StackPane pnRedR1Wins = isBlueOnLeft() ? this.pnRightR1Winner : this.pnLeftR1Winner;
    StackPane pnRedR2Wins = isBlueOnLeft() ? this.pnRightR2Winner : this.pnLeftR2Winner;
    StackPane pnRedR3Wins = isBlueOnLeft() ? this.pnRightR3Winner : this.pnLeftR3Winner;
    MatchWinner r1Winner = getMatchWorker().getRoundWinner(Integer.valueOf(1));
    pnBlueR1Wins.getChildren().clear();
    pnRedR1Wins.getChildren().clear();
    if (r1Winner != null && !MatchWinner.TIE.equals(r1Winner))
      if (MatchWinner.BLUE.equals(r1Winner)) {
        pnBlueR1Wins.getChildren().add(createRoundWinnerStackPane(true));
      } else {
        pnRedR1Wins.getChildren().add(createRoundWinnerStackPane(false));
      }  
    MatchWinner r2Winner = getMatchWorker().getRoundWinner(Integer.valueOf(2));
    pnBlueR2Wins.getChildren().clear();
    pnRedR2Wins.getChildren().clear();
    if (r2Winner != null && !MatchWinner.TIE.equals(r2Winner))
      if (MatchWinner.BLUE.equals(r2Winner)) {
        pnBlueR2Wins.getChildren().add(createRoundWinnerStackPane(true));
      } else {
        pnRedR2Wins.getChildren().add(createRoundWinnerStackPane(false));
      }  
    MatchWinner r3Winner = getMatchWorker().getRoundWinner(Integer.valueOf(3));
    pnBlueR3Wins.getChildren().clear();
    pnRedR3Wins.getChildren().clear();
    if (r3Winner != null && !MatchWinner.TIE.equals(r3Winner))
      if (MatchWinner.BLUE.equals(r3Winner)) {
        pnBlueR3Wins.getChildren().add(createRoundWinnerStackPane(true));
      } else {
        pnRedR3Wins.getChildren().add(createRoundWinnerStackPane(false));
      }  
  }
  
  public final void afterPropertiesSet() throws Exception {
    this.pnCountdownContainer.getChildren().add(0, getRoundCountdownController().getRootView());
    if (this.showTotalPenaltiesOnGoldenPoint.booleanValue()) {
      this.lblLeftTotalPenalties = (Label)getRootView().lookup("#lblLeftTotalPenalties");
      this.lblRightTotalPenalties = (Label)getRootView().lookup("#lblRightTotalPenalties");
    } 
    this.roundsWinLeftListener = new DefaultTextUpdateByNumberChangeListener(this.txtLeftRoundsWins);
    this.leftR1PointsListener = new DefaultLabelUpdateByNumberChangeListener(this.lblLeftR1Points);
    this.leftR2PointsListener = new DefaultLabelUpdateByNumberChangeListener(this.lblLeftR2Points);
    this.leftR3PointsListener = new DefaultLabelUpdateByNumberChangeListener(this.lblLeftR3Points);
    this.roundsWinRightListener = new DefaultTextUpdateByNumberChangeListener(this.txtRightRoundsWins);
    this.rightR1PointsListener = new DefaultLabelUpdateByNumberChangeListener(this.lblRightR1Points);
    this.rightR2PointsListener = new DefaultLabelUpdateByNumberChangeListener(this.lblRightR2Points);
    this.rightR3PointsListener = new DefaultLabelUpdateByNumberChangeListener(this.lblRightR3Points);
    getAppStatusWorker().bestOf3SuperiorityRoundWinnerProperty().addListener(new ChangeListener() {
          public void changed(ObservableValue observable, Object oldValue, final Object newValue) {
            if (newValue != null && newValue instanceof MatchWinner && 
              MatchVictoryCriteria.BESTOF3.equals(CommonScoreboardController.this.getMatchWorker().getMatchVictoryCriteria()) && MatchWinner.TIE
              .equals(CommonScoreboardController.this.getMatchWorker().getRoundWinner(Integer.valueOf(CommonScoreboardController.this.getMatchWorker().getCurrentRound()))))
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.updateBestOf3RoundSuperiorityWinnerInfo((MatchWinner)newValue);
                    }
                  }); 
          }
        });
    if (TkStrikeScoreboardGraphicDetailType.LOW_GRAPHIC_DETAIL.equals(this.scoreboardGraphicDetailType)) {
      Timeline flashLeftPoints = new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(600.0D), new KeyValue[] { new KeyValue((WritableValue)this.txtLeftPoints.visibleProperty(), Boolean.valueOf(false)) }), new KeyFrame(Duration.millis(900.0D), new KeyValue[] { new KeyValue((WritableValue)this.txtLeftPoints.visibleProperty(), Boolean.valueOf(true)) }) });
      flashLeftPoints.setDelay(Duration.seconds(0.5D));
      flashLeftPoints.setCycleCount(3);
      flashLeftPoints.setAutoReverse(false);
      Timeline flashRightPoints = new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(600.0D), new KeyValue[] { new KeyValue((WritableValue)this.txtRightPoints.visibleProperty(), Boolean.valueOf(false)) }), new KeyFrame(Duration.millis(900.0D), new KeyValue[] { new KeyValue((WritableValue)this.txtRightPoints.visibleProperty(), Boolean.valueOf(true)) }) });
      flashRightPoints.setDelay(Duration.seconds(0.5D));
      flashRightPoints.setCycleCount(3);
      flashRightPoints.setAutoReverse(false);
      this.txtLeftPoints.textProperty().addListener(new AthletesPointsTextListener(flashLeftPoints, this.txtLeftPoints));
      this.txtRightPoints.textProperty().addListener(new AthletesPointsTextListener(flashRightPoints, this.txtRightPoints));
    } 
    StackPane leftImpact1 = (StackPane)this.pnLeftLastImpact.getChildren().get(0);
    this.flashLeftImpact1 = addFlash(leftImpact1, this.flashLeftImpact1);
    StackPane leftImpact2 = (StackPane)this.pnLeftLastImpact.getChildren().get(1);
    this.flashLeftImpact2 = addFlash(leftImpact2, this.flashLeftImpact2);
    StackPane rightImpact1 = (StackPane)this.pnRightLastImpact.getChildren().get(0);
    this.flashRightImpact1 = addFlash(rightImpact1, this.flashRightImpact1);
    StackPane rightImpact2 = (StackPane)this.pnRightLastImpact.getChildren().get(1);
    this.flashRightImpact2 = addFlash(rightImpact2, this.flashRightImpact2);
    getMatchWorker().blueGoldenPointImpactsProperty().addListener(this.nearMissHitsBlueListener);
    getMatchWorker().redGoldenPointImpactsProperty().addListener(this.nearMissHitsRedListener);
    getMatchWorker().blueGoldenPointPunchesProperty().addListener(new SimpleIntegerToLabelListener(this.lblBlueGPTBPunches));
    getMatchWorker().redGoldenPointPunchesProperty().addListener(new SimpleIntegerToLabelListener(this.lblRedGPTBPunches));
    getMatchWorker().bluePARATechPointsProperty().addListener(new SimpleIntegerToLabelListener(this.lblBlueGPTBTechPointsPARA));
    getMatchWorker().redPARATechPointsProperty().addListener(new SimpleIntegerToLabelListener(this.lblRedGPTBTechPointsPARA));
    getAppStatusWorker().matchConfigurationChanges().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            if (t1.booleanValue())
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.bindUIControls();
                    }
                  }); 
          }
        });
    getAppStatusWorker().lastAppStatusIdProperty().addListener(new ChangeListener<AppStatusId>() {
          public void changed(ObservableValue<? extends AppStatusId> observable, AppStatusId oldValue, AppStatusId newValue) {
            if (AppStatusId.MATCH_CONFIGURED.equals(newValue))
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.txtLeftPoints().setText("0");
                      CommonScoreboardController.this.txtRightPoints().setText("0");
                    }
                  }); 
          }
        });
    getMatchWorker().roundsWinnerChangesProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue()) {
              for (int i = 1; i <= CommonScoreboardController.this.getMatchWorker().getCurrentRound(); i++)
                CommonScoreboardController.this._refreshRoundsWinner(Integer.valueOf(i)); 
              if (MatchVictoryCriteria.BESTOF3.equals(CommonScoreboardController.this.getMatchWorker().getMatchVictoryCriteria()) && MatchStatusId.ROUND_FINISHED.equals(CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus()))
                Platform.runLater(new Runnable() {
                      public void run() {
                        CommonScoreboardController.this.putRoundsWinnerInfoInRest();
                      }
                    }); 
            } 
          }
        });
    getMatchWorker().currentMatchStatusProperty().addListener(new ChangeListener<MatchStatusId>() {
          public void changed(ObservableValue<? extends MatchStatusId> observableValue, final MatchStatusId prevMatchStatusId, final MatchStatusId newMatchStatus) {
            if (newMatchStatus != null)
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.pnShowWinner().setVisible(false);
                      try {
                        CommonScoreboardController.this.pnKyeShi().setVisible(MatchStatusId.ROUND_KYESHI.equals(newMatchStatus));
                        if (MatchStatusId.ROUND_KYESHI.equals(newMatchStatus)) {
                          CommonScoreboardController.this.txtLeftPoints.getStyleClass().add("pointsWhenKyeShi");
                          CommonScoreboardController.this.txtLeftPoints.getStyleClass().remove("newPoints");
                          CommonScoreboardController.this.txtRightPoints.getStyleClass().add("pointsWhenKyeShi");
                          CommonScoreboardController.this.txtRightPoints.getStyleClass().remove("newPoints");
                        } else {
                          CommonScoreboardController.this.txtLeftPoints.getStyleClass().remove("pointsWhenKyeShi");
                          CommonScoreboardController.this.txtLeftPoints.getStyleClass().add("newPoints");
                          CommonScoreboardController.this.txtRightPoints.getStyleClass().remove("pointsWhenKyeShi");
                          CommonScoreboardController.this.txtRightPoints.getStyleClass().add("newPoints");
                        } 
                        CommonScoreboardController.this.pnRest().setVisible((MatchStatusId.ROUND_FINISHED.equals(newMatchStatus) || MatchStatusId.ROUND_PARA_TIMEOUT_WORKING.equals(newMatchStatus)));
                        boolean showRoundWinsInfo = (MatchVictoryCriteria.BESTOF3.equals(CommonScoreboardController.this.getMatchWorker().getMatchVictoryCriteria()) && (MatchStatusId.ROUND_FINISHED.equals(newMatchStatus) || MatchStatusId.ROUND_PARA_TIMEOUT_WORKING.equals(newMatchStatus)));
                        if (CommonScoreboardController.this.pnLeftScorePoints != null)
                          CommonScoreboardController.this.pnLeftScorePoints.setVisible(!showRoundWinsInfo); 
                        if (CommonScoreboardController.this.pnLeftScoreRoundsInfo != null)
                          CommonScoreboardController.this.pnLeftScoreRoundsInfo.setVisible(showRoundWinsInfo); 
                        if (CommonScoreboardController.this.pnRightScorePoints != null)
                          CommonScoreboardController.this.pnRightScorePoints.setVisible(!showRoundWinsInfo); 
                        if (CommonScoreboardController.this.pnRightScoreRoundsInfo != null)
                          CommonScoreboardController.this.pnRightScoreRoundsInfo.setVisible(showRoundWinsInfo); 
                        if (showRoundWinsInfo) {
                          CommonScoreboardController.this.putRoundsWinnerInfoInRest();
                          CommonScoreboardController.this.showOrHidePointGap(true, false);
                        } 
                        CommonScoreboardController.this.lblRest().setText(CommonScoreboardController.this.getMessage(MatchStatusId.ROUND_PARA_TIMEOUT_WORKING.equals(newMatchStatus) ? "message.matchPaused" : "message.restTime"));
                        CommonScoreboardController.this.pnTimeout().setVisible((MatchStatusId.ROUND_PAUSED.equals(newMatchStatus) || MatchStatusId.NETWORK_ERROR.equals(newMatchStatus)));
                        CommonScoreboardController.this.pnNetworkError().setVisible((MatchStatusId.NETWORK_ERROR.equals(newMatchStatus) && (MatchStatusId.ROUND_WORKING
                            .equals(prevMatchStatusId) || MatchStatusId.ROUND_IN_GOLDENPOINT
                            .equals(prevMatchStatusId))));
                        NetworkErrorCause networkErrorCause = CommonScoreboardController.this.getMatchWorker().getNetworkErrorCause();
                        if (networkErrorCause != null) {
                          String labelStyle = "label-blue";
                          String labelMessage = CommonScoreboardController.this.getMessage("label.networkError.body");
                          if (networkErrorCause.isBlue()) {
                            if (networkErrorCause.isBody()) {
                              CommonScoreboardController.this.ivErrorCause.setImage(new Image(getClass().getResourceAsStream("/images/blueBody.png")));
                            } else {
                              CommonScoreboardController.this.ivErrorCause.setImage(new Image(getClass().getResourceAsStream("/images/blueHead.png")));
                              labelMessage = CommonScoreboardController.this.getMessage("label.networkError.head");
                            } 
                          } else {
                            labelStyle = "label-red";
                            if (networkErrorCause.isBody()) {
                              CommonScoreboardController.this.ivErrorCause.setImage(new Image(getClass().getResourceAsStream("/images/redBody.png")));
                            } else {
                              CommonScoreboardController.this.ivErrorCause.setImage(new Image(getClass().getResourceAsStream("/images/redHead.png")));
                              labelMessage = CommonScoreboardController.this.getMessage("label.networkError.head");
                            } 
                          } 
                          CommonScoreboardController.this.lblNetworkError1.getStyleClass().removeAll((Object[])new String[] { "label-blue", "label-red" });
                          CommonScoreboardController.this.lblNetworkError1.getStyleClass().addAll((Object[])new String[] { labelStyle });
                          CommonScoreboardController.this.lblNetworkError1.setText(labelMessage);
                          CommonScoreboardController.this.lblNetworkError2.setText(CommonScoreboardController.this.getMessage("label.networkError.general"));
                        } 
                        boolean showWinner = false;
                        if (MatchStatusId.MATCH_FINISHED.equals(newMatchStatus)) {
                          if (CommonScoreboardController.this.getMatchWorker().isParaTkdMatch() ? CommonScoreboardController.this.pnGoldenPointTieBreakerPARA.isVisible() : CommonScoreboardController.this.pnGoldenPointTieBreaker.isVisible()) {
                          
                          } else {
                          
                          } 
                          showWinner = true;
                        } 
                        CommonScoreboardController.this.showWinnerProperty.setValue(Boolean.valueOf((showWinner || (MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(newMatchStatus) && CommonScoreboardController.this.showWinnerInfoWhenGoldenPointFinish.booleanValue()))));
                        if (MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION.equals(newMatchStatus) && CommonScoreboardController.this
                          .getMatchWorker().isGoldenPointTieBreaker())
                          CommonScoreboardController.this.updateTieBreakerPenalties(); 
                        if (newMatchStatus.equals(MatchStatusId.MATCH_FINISHED)) {
                          CommonScoreboardController.this.pnLeftHits().getChildren().clear();
                          CommonScoreboardController.this.pnRightHits().getChildren().clear();
                          CommonScoreboardController.this.clearImpactInScore((Pane)CommonScoreboardController.this.pnLeftLastImpact);
                          CommonScoreboardController.this.clearImpactInScore((Pane)CommonScoreboardController.this.pnRightLastImpact);
                        } else if (newMatchStatus.equals(MatchStatusId.ROUND_IN_GOLDENPOINT) || newMatchStatus
                          .equals(MatchStatusId.WAITING_4_START_GOLDENPOINT)) {
                          if (newMatchStatus.equals(MatchStatusId.WAITING_4_START_GOLDENPOINT) || CommonScoreboardController.this
                            .cleanPointsWhenGoldenPointEnabled())
                            if (CommonScoreboardController.this.isBlueOnLeft()) {
                              CommonScoreboardController.this.lblLeftPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueGoldenPointPenalties());
                              CommonScoreboardController.this.lblRightPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getRedGoldenPointPenalties());
                            } else {
                              CommonScoreboardController.this.lblLeftPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getRedGoldenPointPenalties());
                              CommonScoreboardController.this.lblRightPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueGoldenPointPenalties());
                            }  
                          if (newMatchStatus.equals(MatchStatusId.WAITING_4_START_GOLDENPOINT)) {
                            CommonScoreboardController.this.pnLeftHits().getChildren().clear();
                            CommonScoreboardController.this.pnRightHits().getChildren().clear();
                            if (CommonScoreboardController.this.isBlueOnLeft()) {
                              CommonScoreboardController.this.txtLeftPoints().setText("" + CommonScoreboardController.this.getMatchWorker().getBlueGeneralPoints());
                              CommonScoreboardController.this.txtRightPoints().setText("" + CommonScoreboardController.this.getMatchWorker().getRedGeneralPoints());
                            } else {
                              CommonScoreboardController.this.txtLeftPoints().setText("" + CommonScoreboardController.this.getMatchWorker().getRedGeneralPoints());
                              CommonScoreboardController.this.txtRightPoints().setText("" + CommonScoreboardController.this.getMatchWorker().getBlueGeneralPoints());
                            } 
                            if (CommonScoreboardController.this.showTotalPenaltiesOnGoldenPoint.booleanValue())
                              Platform.runLater(new Runnable() {
                                    public void run() {
                                      CommonScoreboardController.this.lblLeftTotalPenalties.setVisible(true);
                                      CommonScoreboardController.this.lblRightTotalPenalties.setVisible(true);
                                      if (CommonScoreboardController.this.isBlueOnLeft()) {
                                        CommonScoreboardController.this.lblLeftTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueTotalPenalties());
                                        CommonScoreboardController.this.lblRightTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getRedTotalPenalties());
                                      } else {
                                        CommonScoreboardController.this.lblLeftTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getRedTotalPenalties());
                                        CommonScoreboardController.this.lblRightTotalPenalties.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueTotalPenalties());
                                      } 
                                    }
                                  }); 
                          } 
                          CommonScoreboardController.this.clearImpactInScore((Pane)CommonScoreboardController.this.pnLeftLastImpact);
                          CommonScoreboardController.this.clearImpactInScore((Pane)CommonScoreboardController.this.pnRightLastImpact);
                          CommonScoreboardController.this.getMatchWorker().bluePenaltiesProperty().removeListener(CommonScoreboardController.this.penaltiesLeftListener);
                          CommonScoreboardController.this.getMatchWorker().bluePenaltiesProperty().removeListener(CommonScoreboardController.this.penaltiesRightListener);
                          CommonScoreboardController.this.getMatchWorker().redPenaltiesProperty().removeListener(CommonScoreboardController.this.penaltiesRightListener);
                          CommonScoreboardController.this.getMatchWorker().redPenaltiesProperty().removeListener(CommonScoreboardController.this.penaltiesLeftListener);
                          if (CommonScoreboardController.this.isBlueOnLeft()) {
                            CommonScoreboardController.this.getMatchWorker().blueGoldenPointPenaltiesProperty().addListener(CommonScoreboardController.this.penaltiesLeftListener);
                            CommonScoreboardController.this.getMatchWorker().redGoldenPointPenaltiesProperty().addListener(CommonScoreboardController.this.penaltiesRightListener);
                          } else {
                            CommonScoreboardController.this.getMatchWorker().blueGoldenPointPenaltiesProperty().addListener(CommonScoreboardController.this.penaltiesRightListener);
                            CommonScoreboardController.this.getMatchWorker().redGoldenPointPenaltiesProperty().addListener(CommonScoreboardController.this.penaltiesLeftListener);
                          } 
                        } 
                      } catch (RuntimeException ignore) {
                        ignore.printStackTrace();
                      } 
                    }
                  }); 
          }
        });
    this.lblDoctor.visibleProperty().bind((ObservableValue)getMatchWorker().doctorInRoundProperty());
    this.showWinnerProperty.addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (CommonScoreboardController.this.getMatchWorker().isParaTkdMatch() ? CommonScoreboardController.this.pnGoldenPointTieBreakerPARA.isVisible() : CommonScoreboardController.this.pnGoldenPointTieBreaker.isVisible())
              CommonScoreboardController.this._refreshWinnerInfo(); 
          }
        });
    getMatchWorker().matchWinnerChangesProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean changes) {
            if (changes.booleanValue()) {
              if (CommonScoreboardController.this.getMatchWorker().isParaTkdMatch() ? CommonScoreboardController.this.pnGoldenPointTieBreakerPARA.isVisible() : CommonScoreboardController.this.pnGoldenPointTieBreaker.isVisible()) {
                Platform.runLater(new Runnable() {
                      public void run() {
                        CommonScoreboardController.this.pnShowWinner().setVisible(false);
                        if (CommonScoreboardController.this.getMatchWorker().isParaTkdMatch()) {
                          CommonScoreboardController.this.putWinnerInfo(CommonScoreboardController.this.lblMatchWinnerTitleGDPTBPARA, CommonScoreboardController.this.lblFinalDecissionGDPTBPARA, CommonScoreboardController.this.lblWinnerGDPTBPARA, CommonScoreboardController.this.ivWinnerFlagGDPTBPARA);
                        } else {
                          CommonScoreboardController.this.putWinnerInfo(CommonScoreboardController.this.lblMatchWinnerTitleGDPTB, CommonScoreboardController.this.lblFinalDecissionGDPTB, CommonScoreboardController.this.lblWinnerGDPTB, CommonScoreboardController.this.ivWinnerFlagGDPTB);
                        } 
                      }
                    });
                return;
              } 
              CommonScoreboardController.this._refreshWinnerInfo();
            } 
          }
        });
    getMatchWorker().kyeShiCountdownCurrentTimeAsStringProperty().addListener(new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> observableValue, String s, final String newValue) {
            try {
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.lblKyeShiTime().setText(newValue);
                    }
                  });
            } catch (RuntimeException e) {
              e.printStackTrace();
            } 
          }
        });
    getMatchWorker().restTimeCountdownCurrentTimeAsStringProperty().addListener(new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> observableValue, String s, final String t1) {
            try {
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.txtRestTime().setText(t1);
                    }
                  });
            } catch (RuntimeException runtimeException) {}
          }
        });
    getMatchWorker().restTimeCountdownFinishedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean finished) {
            if (finished.booleanValue())
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.pnRest().setVisible(false);
                    }
                  }); 
          }
        });
    getMatchWorker().paraTimeOutCountdownCurrentTimeAsStringProperty().addListener(new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> observableValue, String s, final String t1) {
            try {
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.txtRestTime().setText(t1);
                    }
                  });
            } catch (RuntimeException runtimeException) {}
          }
        });
    getMatchWorker().paraTimeOutCountdownFinishedProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean finished) {
            if (finished.booleanValue())
              Platform.runLater(new Runnable() {
                    public void run() {
                      CommonScoreboardController.this.pnRest().setVisible(false);
                    }
                  }); 
          }
        });
    getMatchWorker().currentRoundStrProperty().addListener(this.currentRoundStrListener);
    getMatchWorker().currentRoundProperty().addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if (newValue != null && newValue.intValue() > 0)
              CommonScoreboardController.this._refreshRoundsWinner(Integer.valueOf(newValue.intValue())); 
          }
        });
    getMatchWorker().showGoldenPointTieBreakerOnScoreboard().addListener(getShowGoldenPointTieBreakerOnScoreboardListener());
    getMatchWorker().bestOf3RoundSuperiorityOnScoreboard().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            CommonTkStrikeBaseController.logger.info("bestOf3RoundSuperiorityOnScoreboard changed new value " + newValue);
            final boolean show = (newValue != null) ? newValue.booleanValue() : false;
            Platform.runLater(new Runnable() {
                  public void run() {
                    CommonScoreboardController.this.lblMatchTitleRoundSUPBestOf3.setText(CommonScoreboardController.this.lblMatchConfig.getText());
                    CommonScoreboardController.this.lblRoundSUPBestOf3.setText("ROUND " + CommonScoreboardController.this.txtCurrentRound.getText());
                    BestOf3RoundSuperiority bestOf3RoundSuperiority = CommonScoreboardController.this.getMatchWorker().getCurrentBestOf3RoundSuperiority();
                    if (bestOf3RoundSuperiority != null) {
                      MatchWinner currentRoundWinner = bestOf3RoundSuperiority.getRoundWinner();
                      if (MatchWinner.TIE.equals(currentRoundWinner)) {
                        CommonTkStrikeBaseController.logger.info("bestOf3RoundSuperiorityOnScoreboard superiority on TIE get from AppStatusWorker");
                        currentRoundWinner = (MatchWinner)CommonScoreboardController.this.getAppStatusWorker().bestOf3SuperiorityRoundWinnerProperty().get();
                      } 
                      CommonScoreboardController.this.updateBestOf3RoundSuperiorityWinnerInfo(currentRoundWinner);
                      CommonScoreboardController.this.lblBlueRoundSUPBestOf3TurningSpinning.setText("" + bestOf3RoundSuperiority.getBlueTurningSpinning());
                      CommonScoreboardController.this.lblRedRoundSUPBestOf3TurningSpinning.setText("" + bestOf3RoundSuperiority.getRedTurningSpinning());
                      CommonScoreboardController.this.lblBlueRoundSUPBestOf3Tech.setText("" + bestOf3RoundSuperiority.getBlueTech());
                      CommonScoreboardController.this.lblRedRoundSUPBestOf3Tech.setText("" + bestOf3RoundSuperiority.getRedTech());
                      CommonScoreboardController.this.lblBlueRoundSUPBestOf3Hits.setText("" + bestOf3RoundSuperiority.getBlueHits());
                      CommonScoreboardController.this.lblRedRoundSUPBestOf3Hits.setText("" + bestOf3RoundSuperiority.getRedHits());
                    } 
                    CommonScoreboardController.this.pnRoundSuperiorityBestOf3.setVisible(show);
                  }
                });
          }
        });
    getMatchWorker().matchFinalDecisionProperty().addListener(new ChangeListener<FinalDecision>() {
          public void changed(ObservableValue<? extends FinalDecision> observable, FinalDecision oldValue, FinalDecision newValue) {
            if (newValue != null) {
              MatchStatusId currentMatchStatus = CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus();
              if (FinalDecision.PTG.equals(newValue) && (MatchStatusId.ROUND_WORKING
                .equals(currentMatchStatus) || MatchStatusId.ROUND_FINISHED
                .equals(currentMatchStatus) || MatchStatusId.MATCH_NEEDS_CONFIRM_FINAL_DECISION
                .equals(currentMatchStatus))) {
                CommonTkStrikeBaseController.logger.info("matchFinalDecisionProperty changed FinalDecision " + newValue + " MatchSatus: " + currentMatchStatus);
                Platform.runLater(new Runnable() {
                      public void run() {
                        CommonTkStrikeBaseController.logger.info("PointGap visible to true");
                        CommonScoreboardController.this.showOrHidePointGap(false, true);
                      }
                    });
              } 
            } 
          }
        });
    getMatchWorker().bestOf3RoundWithPointGap().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    if (Boolean.TRUE.equals(newValue)) {
                      MatchWinner matchWinner = CommonScoreboardController.this.getMatchWorker().getBestOf3RoundWinnerWithPointGap();
                      CommonTkStrikeBaseController.logger.info("BestOf3 round  " + CommonScoreboardController.this.getMatchWorker().getCurrentRound() + " won by point gap to " + matchWinner);
                      CommonScoreboardController.this.lblPointGapWinner.getStyleClass()
                        .removeAll((Object[])new String[] { "sb-blue", "sb-red" });
                      CommonScoreboardController.this.lblPointGapWinner.getStyleClass()
                        .addAll((Object[])new String[] { MatchWinner.BLUE.equals(matchWinner) ? "sb-blue" : "sb-red" });
                      CommonScoreboardController.this.lblPointGapWinner.setText(MatchWinner.BLUE.equals(matchWinner) ? CommonScoreboardController.this
                          .getMessage("label.blue").toUpperCase() : CommonScoreboardController.this
                          .getMessage("label.red").toUpperCase());
                    } 
                    boolean show = (newValue != null && newValue.booleanValue() && !MatchStatusId.ROUND_FINISHED.equals(CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus()) && !MatchStatusId.ROUND_PARA_TIMEOUT_WORKING.equals(CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus()));
                    CommonScoreboardController.this.showOrHidePointGap(false, show);
                  }
                });
          }
        });
    getMatchWorker().matchWinnerProperty().addListener(new ChangeListener<MatchWinner>() {
          public void changed(ObservableValue<? extends MatchWinner> observable, MatchWinner oldValue, final MatchWinner newValue) {
            if (newValue != null)
              Platform.runLater(new Runnable() {
                    public void run() {
                      if (!MatchWinner.TIE.equals(newValue)) {
                        CommonScoreboardController.this.lblPointGapWinner.getStyleClass().removeAll((Object[])new String[] { "sb-blue", "sb-red" });
                        CommonScoreboardController.this.lblPointGapWinner.getStyleClass().addAll((Object[])new String[] { MatchWinner.BLUE.equals(this.val$newValue) ? "sb-blue" : "sb-red" });
                        CommonScoreboardController.this.lblPointGapWinner.setText(MatchWinner.BLUE.equals(newValue) ? CommonScoreboardController.this
                            .getMessage("label.blue").toUpperCase() : CommonScoreboardController.this.getMessage("label.red").toUpperCase());
                      } else {
                        CommonScoreboardController.this.showOrHidePointGap(true, false);
                      } 
                    }
                  }); 
          }
        });
    pnShowWinner().visibleProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue.booleanValue())
              CommonScoreboardController.this.showOrHidePointGap(true, false); 
          }
        });
    getMatchWorker().addListener(new CommonMatchWorker.HitEventValidatorListener() {
          public void tryToRemoveHitEvent(final boolean blue, final HitEventType hitEventType) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    CommonTkStrikeBaseController.logger.info("tryToRemoveHitEvent for blue? " + blue + " hitEventType: " + hitEventType);
                    Pane thePane = CommonScoreboardController.this.pnRightHits;
                    if ((CommonScoreboardController.this.isBlueOnLeft() && blue) || (!CommonScoreboardController.this.isBlueOnLeft() && !blue))
                      thePane = CommonScoreboardController.this.pnLeftHits; 
                    if (thePane != null && thePane.getChildren().size() > 0) {
                      int pos = -1;
                      int n = 0;
                      for (Node theNode : thePane.getChildren()) {
                        if (theNode instanceof TkStrikeCombatHit) {
                          TkStrikeCombatHit combatHit = (TkStrikeCombatHit)theNode;
                          if (combatHit.getHitEventType().equals(HitEventType.PUNCH) && hitEventType
                            .equals(combatHit.getHitEventType())) {
                            pos = n;
                          } else if (hitEventType.equals(combatHit.getHitEventType()) && 
                            !combatHit.isJudgesTechValidated()) {
                            pos = n;
                          } 
                          n++;
                        } 
                      } 
                      if (pos > -1)
                        thePane.getChildren().remove(pos); 
                    } 
                  }
                });
          }
          
          public void tryToChangeHitTechEvent(final boolean blue, final HitEventType hitEventType, final boolean validated) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    Pane thePane = CommonScoreboardController.this.pnRightHits;
                    if ((CommonScoreboardController.this.isBlueOnLeft() && blue) || (!CommonScoreboardController.this.isBlueOnLeft() && !blue))
                      thePane = CommonScoreboardController.this.pnLeftHits; 
                    if (thePane != null && thePane.getChildren().size() > 0) {
                      int pos = -1;
                      int n = 0;
                      for (Node theNode : thePane.getChildren()) {
                        if (theNode instanceof TkStrikeCombatHit) {
                          TkStrikeCombatHit combatHit = (TkStrikeCombatHit)theNode;
                          if ((hitEventType.equals(combatHit.getHitEventType()) && !validated && combatHit
                            .isJudgesTechValidated()) || (validated && combatHit
                            
                            .getJudge1Status().equals(HitJudgeStatus.NOT_VALIDATED) && (combatHit
                            .getJudge2Status().equals(HitJudgeStatus.NOT_VALIDATED) || combatHit.getJudge2Status().equals(HitJudgeStatus.NOT_ENABLED)) && (combatHit
                            .getJudge3Status().equals(HitJudgeStatus.NOT_VALIDATED) || combatHit.getJudge3Status().equals(HitJudgeStatus.NOT_ENABLED))))
                            pos = n; 
                          n++;
                        } 
                      } 
                      if (pos > -1) {
                        TkStrikeCombatHit combatHit = (TkStrikeCombatHit)thePane.getChildren().get(pos);
                        if (combatHit != null) {
                          combatHit.changeJudge1HitStatus(validated ? HitJudgeStatus.VALIDATED : HitJudgeStatus.NOT_VALIDATED);
                          combatHit.changeJudge2HitStatus(validated ? HitJudgeStatus.VALIDATED : HitJudgeStatus.NOT_VALIDATED);
                          combatHit.changeJudge3HitStatus(validated ? HitJudgeStatus.VALIDATED : HitJudgeStatus.NOT_VALIDATED);
                        } 
                      } 
                    } 
                  }
                });
          }
          
          public void forceAddNewHitEvent(final HitEventValidator newValue) {
            if (newValue != null)
              Platform.runLater(new Runnable() {
                    public void run() {
                      try {
                        TkStrikeCombatHit combatHit = null;
                        Pane thePane = null;
                        boolean isRight = false;
                        if ((CommonScoreboardController.this.isBlueOnLeft() && newValue.isBlue()) || (!CommonScoreboardController.this.isBlueOnLeft() && !newValue.isBlue())) {
                          thePane = CommonScoreboardController.this.pnLeftHits();
                        } else {
                          thePane = CommonScoreboardController.this.pnRightHits();
                          isRight = true;
                        } 
                        combatHit = new TkStrikeCombatHit(CommonScoreboardController.this.scoreboardGraphicDetailType, newValue.getHitTimestamp(), newValue.getHitEventType(), newValue.getHitValue().intValue(), newValue.getJudgesEnabled(), CommonScoreboardController.this.getHitsControlDefaultFontSize(), Double.valueOf(CommonScoreboardController.this.getHitsControlHeight()), Double.valueOf(CommonScoreboardController.this.getHitsControlSpacing()), Double.valueOf(CommonScoreboardController.this.getHitsControlJudgesWidth()), Double.valueOf(CommonScoreboardController.this.getHitsControlHitWidth()), isRight, newValue.isBackupSystemEnabled());
                        combatHit.setParaTkdTechEvent(newValue.getParaTkdTechEvent());
                        combatHit.changeJudge1HitStatus(newValue.getJudge1HitStatus());
                        combatHit.changeJudge2HitStatus(newValue.getJudge2HitStatus());
                        combatHit.changeJudge3HitStatus(newValue.getJudge3HitStatus());
                        TkStrikeCombatHit otherHit = thePane.getChildren().stream().filter(t -> (param2HitEventValidator.getHitTimestamp() == ((TkStrikeCombatHit)t).getHitTimestamp())).findAny().orElse(null);
                        if (otherHit != null)
                          thePane.getChildren().remove(otherHit); 
                        thePane.getChildren().add(combatHit);
                        if (((newValue.getHitValue().intValue() > 100 && newValue.getHitEventType().equals(HitEventType.HEAD)) || (newValue
                          .getHitValue().intValue() > 100 && newValue.getHitEventType().equals(HitEventType.BODY)) || newValue
                          .getHitEventType().equals(HitEventType.PUNCH) || newValue.getHitEventType().equals(HitEventType.SPECIAL_HEAD)) && 
                          !newValue.getJudge1HitStatus().equals(HitJudgeStatus.VALIDATED) && 
                          !newValue.getJudge2HitStatus().equals(HitJudgeStatus.VALIDATED) && 
                          !newValue.getJudge3HitStatus().equals(HitJudgeStatus.VALIDATED))
                          thePane.getChildren().remove(combatHit); 
                      } catch (RuntimeException re) {
                        re.printStackTrace();
                      } 
                    }
                  }); 
          }
          
          public void removeGoldenPointNearMissHit(final boolean blue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    Pane thePane = null;
                    if ((CommonScoreboardController.this.isBlueOnLeft() && blue) || (!CommonScoreboardController.this.isBlueOnLeft() && !blue)) {
                      thePane = CommonScoreboardController.this.pnLeftHits();
                    } else {
                      thePane = CommonScoreboardController.this.pnRightHits();
                    } 
                    if (thePane.getChildren().size() > 0)
                      thePane.getChildren().remove(thePane.getChildren().size() - 1); 
                  }
                });
          }
          
          public void addGoldenPointNearMissHit(final boolean blue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    Pane thePane = null;
                    boolean isRight = false;
                    if ((CommonScoreboardController.this.isBlueOnLeft() && blue) || (!CommonScoreboardController.this.isBlueOnLeft() && !blue)) {
                      thePane = CommonScoreboardController.this.pnLeftHits();
                    } else {
                      thePane = CommonScoreboardController.this.pnRightHits();
                      isRight = true;
                    } 
                    TkStrikeCombatHit combatHit = new TkStrikeCombatHit(CommonScoreboardController.this.scoreboardGraphicDetailType, System.currentTimeMillis(), HitEventType.BODY, 6, 3, CommonScoreboardController.this.getHitsControlDefaultFontSize(), Double.valueOf(CommonScoreboardController.this.getHitsControlHeight()), Double.valueOf(CommonScoreboardController.this.getHitsControlSpacing()), Double.valueOf(CommonScoreboardController.this.getHitsControlJudgesWidth()), Double.valueOf(CommonScoreboardController.this.getHitsControlHitWidth()), isRight, CommonScoreboardController.this.getMatchWorker().isBackupSystemEnabled());
                    thePane.getChildren().addAll((Object[])new Node[] { (Node)combatHit });
                  }
                });
          }
          
          public void hasHitEventValidatorChange(final HitEventValidator newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    try {
                      TkStrikeCombatHit combatHit = null;
                      Pane thePane = null;
                      boolean isRight = false;
                      if ((CommonScoreboardController.this.isBlueOnLeft() && newValue.isBlue()) || (!CommonScoreboardController.this.isBlueOnLeft() && !newValue.isBlue())) {
                        thePane = CommonScoreboardController.this.pnLeftHits();
                      } else {
                        thePane = CommonScoreboardController.this.pnRightHits();
                        isRight = true;
                      } 
                      for (Node next : thePane.getChildren()) {
                        if (next instanceof TkStrikeCombatHit) {
                          TkStrikeCombatHit hitTemp = (TkStrikeCombatHit)next;
                          if (hitTemp.getHitTimestamp() == newValue.getHitTimestamp())
                            combatHit = hitTemp; 
                        } 
                      } 
                      if (combatHit == null) {
                        combatHit = new TkStrikeCombatHit(CommonScoreboardController.this.scoreboardGraphicDetailType, newValue.getHitTimestamp(), newValue.getHitEventType(), newValue.getHitValue().intValue(), newValue.getJudgesEnabled(), CommonScoreboardController.this.getHitsControlDefaultFontSize(), Double.valueOf(CommonScoreboardController.this.getHitsControlHeight()), Double.valueOf(CommonScoreboardController.this.getHitsControlSpacing()), Double.valueOf(CommonScoreboardController.this.getHitsControlJudgesWidth()), Double.valueOf(CommonScoreboardController.this.getHitsControlHitWidth()), isRight, newValue.isBackupSystemEnabled());
                        combatHit.setParaTkdTechEvent(newValue.getParaTkdTechEvent());
                        combatHit.changeJudge1HitStatus(newValue.getJudge1HitStatus());
                        combatHit.changeJudge2HitStatus(newValue.getJudge2HitStatus());
                        combatHit.changeJudge3HitStatus(newValue.getJudge3HitStatus());
                        thePane.getChildren().add(combatHit);
                        if (newValue.getHitValue().intValue() == -1 || newValue.isAutoRemove()) {
                          final Pane thePaneFinal = thePane;
                          final TkStrikeCombatHit combatHitFinal = combatHit;
                          TkStrikeExecutors.schedule(new Runnable() {
                                public void run() {
                                  Platform.runLater(new Runnable() {
                                        public void run() {
                                          thePaneFinal.getChildren().remove(combatHitFinal);
                                        }
                                      },  );
                                }
                              },  3L, TimeUnit.SECONDS);
                        } 
                      } else {
                        HitJudgeStatus judge1Status = combatHit.getJudge1Status();
                        HitJudgeStatus judge2Status = combatHit.getJudge2Status();
                        HitJudgeStatus judge3Status = combatHit.getJudge3Status();
                        combatHit.setParaTkdTechEvent(newValue.getParaTkdTechEvent());
                        combatHit.changeJudge1HitStatus(newValue.getJudge1HitStatus());
                        combatHit.changeJudge2HitStatus(newValue.getJudge2HitStatus());
                        combatHit.changeJudge3HitStatus(newValue.getJudge3HitStatus());
                        if ((HitEventType.SPECIAL_HEAD.equals(combatHit.getHitEventType()) && HitEventType.HEAD
                          .equals(newValue.getHitEventType())) || (HitEventType.SPECIAL_BODY
                          
                          .equals(combatHit.getHitEventType()) && HitEventType.BODY
                          .equals(newValue.getHitEventType())) || (HitEventType.PARA_TURNING
                          
                          .equals(combatHit.getHitEventType()) && HitEventType.BODY
                          .equals(newValue.getHitEventType())) || (HitEventType.PARA_SPINNING
                          
                          .equals(combatHit.getHitEventType()) && HitEventType.BODY
                          .equals(newValue.getHitEventType()))) {
                          thePane.getChildren().remove(combatHit);
                          HitEventValidator.ParaTkdTechEvent paraTkdTechEvent = combatHit.getParaTkdTechEvent();
                          combatHit = new TkStrikeCombatHit(CommonScoreboardController.this.scoreboardGraphicDetailType, newValue.getHitTimestamp(), newValue.getHitEventType(), newValue.getHitValue().intValue(), newValue.getJudgesEnabled(), CommonScoreboardController.this.getHitsControlDefaultFontSize(), Double.valueOf(CommonScoreboardController.this.getHitsControlHeight()), Double.valueOf(CommonScoreboardController.this.getHitsControlSpacing()), Double.valueOf(CommonScoreboardController.this.getHitsControlJudgesWidth()), Double.valueOf(CommonScoreboardController.this.getHitsControlHitWidth()), isRight, newValue.isBackupSystemEnabled(), judge1Status, judge2Status, judge3Status);
                          thePane.getChildren().addAll((Object[])new Node[] { (Node)combatHit });
                        } 
                      } 
                      if (((newValue.getHitValue().intValue() > 100 && newValue.getHitEventType().equals(HitEventType.HEAD)) || (newValue
                        .getHitValue().intValue() > 100 && newValue.getHitEventType().equals(HitEventType.BODY)) || newValue
                        .getHitEventType().equals(HitEventType.PUNCH) || newValue
                        .getHitEventType().equals(HitEventType.PARA_TURNING) || newValue
                        .getHitEventType().equals(HitEventType.PARA_SPINNING) || newValue
                        .getHitEventType().equals(HitEventType.SPECIAL_HEAD) || newValue
                        .getHitEventType().equals(HitEventType.SPECIAL_BODY)) && 
                        !newValue.getJudge1HitStatus().equals(HitJudgeStatus.VALIDATED) && 
                        !newValue.getJudge2HitStatus().equals(HitJudgeStatus.VALIDATED) && 
                        !newValue.getJudge3HitStatus().equals(HitJudgeStatus.VALIDATED))
                        thePane.getChildren().remove(combatHit); 
                    } catch (RuntimeException re) {
                      re.printStackTrace();
                    } 
                  }
                });
          }
        });
    getMatchWorker().bluePARATimeOutQuotaProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    VBox pane = CommonScoreboardController.this.pnRightRoundWins;
                    if (CommonScoreboardController.this.isBlueOnLeft())
                      pane = CommonScoreboardController.this.pnLeftRoundWins; 
                    if (newValue.booleanValue() && CommonScoreboardController.this.getMatchWorker().bluePARATimeOutQuotaValueProperty().get() > 0) {
                      StackPane stackPane = CommonScoreboardController.this.createRoundWinnerStackPane(true);
                      stackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent mouseEvent) {
                              if (mouseEvent.getClickCount() >= 1) {
                                MatchStatusId currentMatchStatus = CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus();
                                if (MatchStatusId.ROUND_WORKING.equals(currentMatchStatus) || MatchStatusId.NETWORK_ERROR
                                  .equals(currentMatchStatus) || MatchStatusId.ROUND_KYESHI
                                  .equals(currentMatchStatus) || MatchStatusId.ROUND_PAUSED
                                  .equals(currentMatchStatus)) {
                                  CommonScoreboardController.this.getMatchWorker().doPauseRound();
                                  CommonScoreboardController.this.getMatchWorker().doPARATimeOutByBlue();
                                } 
                              } 
                            }
                          });
                      pane.getChildren().add(stackPane);
                      pane.setVisible(true);
                    } else {
                      pane.getChildren().clear();
                    } 
                  }
                });
          }
        });
    getMatchWorker().redPARATimeOutQuotaProperty().addListener(new ChangeListener<Boolean>() {
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
            Platform.runLater(new Runnable() {
                  public void run() {
                    VBox pane = CommonScoreboardController.this.pnLeftRoundWins;
                    if (CommonScoreboardController.this.isBlueOnLeft())
                      pane = CommonScoreboardController.this.pnRightRoundWins; 
                    if (newValue.booleanValue() && CommonScoreboardController.this.getMatchWorker().redPARATimeOutQuotaValueProperty().get() > 0) {
                      StackPane stackPane = CommonScoreboardController.this.createRoundWinnerStackPane(false);
                      stackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent mouseEvent) {
                              if (mouseEvent.getClickCount() >= 1) {
                                MatchStatusId currentMatchStatus = CommonScoreboardController.this.getMatchWorker().getCurrentMatchStatus();
                                if (MatchStatusId.ROUND_WORKING.equals(currentMatchStatus) || MatchStatusId.NETWORK_ERROR
                                  .equals(currentMatchStatus) || MatchStatusId.ROUND_KYESHI
                                  .equals(currentMatchStatus) || MatchStatusId.ROUND_PAUSED
                                  .equals(currentMatchStatus)) {
                                  CommonScoreboardController.this.getMatchWorker().doPauseRound();
                                  CommonScoreboardController.this.getMatchWorker().doPARATimeOutByRed();
                                } 
                              } 
                            }
                          });
                      pane.getChildren().add(stackPane);
                      pane.setVisible(true);
                    } else {
                      pane.getChildren().clear();
                    } 
                  }
                });
          }
        });
    pnKyeShi().setVisible(false);
    pnTimeout().setVisible(false);
    pnNetworkError().setVisible(false);
    pnRest().setVisible(false);
    pnShowWinner().setVisible(false);
    String combatInfoExtra = "";
    if (StringUtils.isNotBlank(this.combatInfoFontSize) && StringUtils.isNumeric(this.combatInfoFontSize))
      combatInfoExtra = "-fx-font-size: " + this.combatInfoFontSize + "; "; 
    if (StringUtils.isNotBlank(this.combatInfoFontFamily))
      combatInfoExtra = combatInfoExtra + "-fx-font-family: " + this.combatInfoFontFamily + ";"; 
    if (StringUtils.isNotBlank(combatInfoExtra)) {
      logger.info("Setting extra StyleCustomization to CombatInfo: " + combatInfoExtra);
      this.lblMatchConfig.setStyle(combatInfoExtra);
    } 
    _commonInternalAfterPropertiesSet();
  }
  
  private void showOrHidePointGap(final boolean hideIfVisible, final boolean show) {
    Pane pointGapPanel = this.pnPointGap;
    String str = "PTG";
    if (MatchVictoryCriteria.BESTOF3.equals(getMatchWorker().getMatchVictoryCriteria())) {
      pointGapPanel = this.pnPointGapBOF3;
      if (getMatchWorker().isBestOf3WinnerLastRoundByPUN().booleanValue())
        str = "PUN"; 
    } 
    final Pane fiPane = pointGapPanel;
    final String fiStr = str;
    Platform.runLater(new Runnable() {
          public void run() {
            if (hideIfVisible) {
              if (fiPane.isVisible())
                fiPane.setVisible(false); 
            } else {
              CommonScoreboardController.this.lblBestOf3PointGap.setText(fiStr);
              fiPane.setVisible(show);
            } 
          }
        });
  }
  
  private void updateTieBreakerPenalties() {
    final int blueTotalPenalties = getMatchWorker().getBlueTotalPenalties();
    final int redTotalPenalties = getMatchWorker().getRedTotalPenalties();
    Platform.runLater(new Runnable() {
          public void run() {
            CommonScoreboardController.this.lblBlueGPTBPenalties.setText("" + blueTotalPenalties);
            CommonScoreboardController.this.lblBlueGPTBPenaltiesPARA.setText("" + blueTotalPenalties);
            CommonScoreboardController.this.lblRedGPTBPenalties.setText("" + redTotalPenalties);
            CommonScoreboardController.this.lblRedGPTBPenaltiesPARA.setText("" + redTotalPenalties);
          }
        });
  }
  
  private void updateBestOf3RoundSuperiorityWinnerInfo(MatchWinner roundWinner) {
    String winnerName = "";
    logger.info("updateBestOf3RoundSuperiorityWinnerInfo to winner " + roundWinner);
    this.lblWinnerRoundSUPBestOf3.getStyleClass().removeAll((Object[])new String[] { "sb-blue", "sb-red" });
    this.ivWinnerFlagRoundSUPBestOf3.setImage(null);
    if (MatchWinner.BLUE.equals(roundWinner)) {
      if (isBlueOnLeft()) {
        boolean showFlag = StringUtils.isNotBlank(this.txtLeftAbbr.getText());
        winnerName = txtLeftName().getText() + (showFlag ? (" (" + this.txtLeftAbbr.getText() + ")") : "");
        if (showFlag && pnLeftFlag().getChildren() != null && pnLeftFlag().getChildren().size() > 0) {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(((ImageView)pnLeftFlag().getChildren().get(0)).getImage());
        } else {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(null);
        } 
        this.lblWinnerRoundSUPBestOf3.getStyleClass().addAll((Object[])new String[] { "sb-blue" });
      } else {
        boolean showFlag = StringUtils.isNotBlank(this.txtRightAbbr.getText());
        winnerName = txtRightName().getText() + (showFlag ? (" (" + this.txtRightAbbr.getText() + ")") : "");
        if (showFlag && pnRightFlag().getChildren() != null && pnRightFlag().getChildren().size() > 0) {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(((ImageView)pnRightFlag().getChildren().get(0)).getImage());
        } else {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(null);
        } 
        this.lblWinnerRoundSUPBestOf3.getStyleClass().addAll((Object[])new String[] { "sb-blue" });
      } 
    } else if (MatchWinner.RED.equals(roundWinner)) {
      if (isBlueOnLeft()) {
        boolean showFlag = StringUtils.isNotBlank(this.txtRightAbbr.getText());
        winnerName = txtRightName().getText() + (showFlag ? (" (" + this.txtRightAbbr.getText() + ")") : "");
        if (showFlag && pnRightFlag().getChildren() != null && pnRightFlag().getChildren().size() > 0) {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(((ImageView)pnRightFlag().getChildren().get(0)).getImage());
        } else {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(null);
        } 
        this.lblWinnerRoundSUPBestOf3.getStyleClass().addAll((Object[])new String[] { "sb-red" });
      } else {
        boolean showFlag = StringUtils.isNotBlank(this.txtLeftAbbr.getText());
        winnerName = txtLeftName().getText() + (showFlag ? (" (" + this.txtLeftAbbr.getText() + ")") : "");
        if (showFlag && pnLeftFlag().getChildren() != null && pnLeftFlag().getChildren().size() > 0) {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(((ImageView)pnLeftFlag().getChildren().get(0)).getImage());
        } else {
          this.ivWinnerFlagRoundSUPBestOf3.setImage(null);
        } 
        this.lblWinnerRoundSUPBestOf3.getStyleClass().addAll((Object[])new String[] { "sb-red" });
      } 
    } 
    this.lblWinnerRoundSUPBestOf3.setText(winnerName.toUpperCase());
  }
  
  private StackPane createRoundWinnerStackPane(boolean blue) {
    StackPane res = new StackPane();
    res.setAlignment(Pos.CENTER);
    Circle circle = new Circle(15.0D);
    circle.setStrokeWidth(0.0D);
    circle.setFill((Paint)Color.web("#eaff00"));
    Circle circle2 = new Circle(12.0D);
    circle2.setStrokeWidth(4.0D);
    circle2.setStroke((Paint)Color.BLACK);
    circle2.setFill((Paint)Color.web("#eaff00"));
    StackPane.setAlignment((Node)circle, Pos.CENTER);
    StackPane.setAlignment((Node)circle2, Pos.CENTER);
    res.getChildren().addAll((Object[])new Node[] { (Node)circle, (Node)circle2 });
    return res;
  }
  
  private StackPane createEmptySpaceOnRoundWinner() {
    StackPane res = new StackPane();
    res.setAlignment(Pos.CENTER);
    Circle circle = new Circle(15.0D);
    circle.setStrokeWidth(0.0D);
    circle.setFill((Paint)Color.BLACK);
    Circle circle2 = new Circle(12.0D);
    circle2.setStrokeWidth(4.0D);
    circle2.setStroke((Paint)Color.BLACK);
    circle2.setFill((Paint)Color.BLACK);
    StackPane.setAlignment((Node)circle, Pos.CENTER);
    StackPane.setAlignment((Node)circle2, Pos.CENTER);
    res.getChildren().addAll((Object[])new Node[] { (Node)circle, (Node)circle2 });
    return res;
  }
  
  ChangeListener<Boolean> getShowGoldenPointTieBreakerOnScoreboardListener() {
    return new ChangeListener<Boolean>() {
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, final Boolean newValue) {
          Platform.runLater(new Runnable() {
                public void run() {
                  CommonScoreboardController.this.pnShowWinner().setVisible(false);
                  if (CommonScoreboardController.this.getMatchWorker().isParaTkdMatch()) {
                    CommonScoreboardController.this.putWinnerInfo(CommonScoreboardController.this.lblMatchWinnerTitleGDPTBPARA, CommonScoreboardController.this.lblFinalDecissionGDPTBPARA, CommonScoreboardController.this.lblWinnerGDPTBPARA, CommonScoreboardController.this.ivWinnerFlagGDPTBPARA);
                    CommonScoreboardController.this.updateTieBreakerPenalties();
                    CommonScoreboardController.this.pnGoldenPointTieBreakerPARA.setVisible(newValue.booleanValue());
                    if (CommonScoreboardController.this.pnGoldenPointTieBreaker.isVisible())
                      CommonScoreboardController.this.pnGoldenPointTieBreaker.setVisible(false); 
                  } else {
                    CommonScoreboardController.this.lblBlueGPTBHits.setVisible(true);
                    CommonScoreboardController.this.lblBlueGPTBPenalties.setVisible(true);
                    CommonScoreboardController.this.lblBlueGPTBRounds.setVisible(true);
                    CommonScoreboardController.this.lblRedGPTBHits.setVisible(true);
                    CommonScoreboardController.this.lblRedGPTBPenalties.setVisible(true);
                    CommonScoreboardController.this.lblRedGPTBRounds.setVisible(true);
                    CommonScoreboardController.this.createRoundWinnersOnTieBreaker(CommonScoreboardController.this.lblBlueGPTBRounds, CommonScoreboardController.this.lblRedGPTBRounds);
                    CommonScoreboardController.this.updateTieBreakerPenalties();
                    CommonScoreboardController.this.putWinnerInfo(CommonScoreboardController.this.lblMatchWinnerTitleGDPTB, CommonScoreboardController.this.lblFinalDecissionGDPTB, CommonScoreboardController.this.lblWinnerGDPTB, CommonScoreboardController.this.ivWinnerFlagGDPTB);
                    CommonScoreboardController.this.pnGoldenPointTieBreaker.setVisible(newValue.booleanValue());
                    if (CommonScoreboardController.this.pnGoldenPointTieBreakerPARA.isVisible())
                      CommonScoreboardController.this.pnGoldenPointTieBreakerPARA.setVisible(false); 
                  } 
                }
              });
        }
      };
  }
  
  protected void createRoundWinnersOnTieBreaker(Label lblBlueRounds, Label lblRedRounds) {
    int blue = 0;
    int red = 0;
    for (int round = 1; round <= getMatchWorker().getMatchRounds(); round++) {
      MatchWinner rWinner = getMatchWorker().getRoundWinner(Integer.valueOf(round));
      if (MatchWinner.BLUE.equals(rWinner)) {
        blue++;
      } else if (MatchWinner.RED.equals(rWinner)) {
        red++;
      } 
    } 
    lblBlueRounds.setText("" + blue);
    lblRedRounds.setText("" + red);
  }
  
  private void putWinnerInfo(Label matchInfo, Label finalDecission, Label winner, ImageView ivWinner) {
    MatchWinner matchWinner = getMatchWorker().getMatchWinner();
    matchInfo.setText(getMessage("message.matchResult", new String[] { this.txtMatchNumber.getText() }));
    finalDecission.setText(getMessage("label.winner").toUpperCase());
    String winnerName = getMessage("label.matchWinner.TIE");
    boolean tie = true;
    winner.getStyleClass().removeAll((Object[])new String[] { "sb-blue", "sb-red" });
    if (matchWinner.equals(MatchWinner.BLUE)) {
      if (isBlueOnLeft()) {
        boolean showFlag = StringUtils.isNotBlank(this.txtLeftAbbr.getText());
        winnerName = txtLeftName().getText() + (showFlag ? (" (" + this.txtLeftAbbr.getText() + ")") : "");
        if (showFlag && pnLeftFlag().getChildren() != null && pnLeftFlag().getChildren().size() > 0) {
          ivWinner.setImage(((ImageView)pnLeftFlag().getChildren().get(0)).getImage());
        } else {
          ivWinner.setImage(null);
        } 
        winner.getStyleClass().addAll((Object[])new String[] { "sb-blue" });
      } else {
        boolean showFlag = StringUtils.isNotBlank(this.txtRightAbbr.getText());
        winnerName = txtRightName().getText() + (showFlag ? (" (" + this.txtRightAbbr.getText() + ")") : "");
        if (showFlag && pnRightFlag().getChildren() != null && pnRightFlag().getChildren().size() > 0) {
          ivWinner.setImage(((ImageView)pnRightFlag().getChildren().get(0)).getImage());
        } else {
          ivWinner.setImage(null);
        } 
        winner.getStyleClass().addAll((Object[])new String[] { "sb-blue" });
      } 
      tie = false;
    } else if (matchWinner.equals(MatchWinner.RED)) {
      if (isBlueOnLeft()) {
        boolean showFlag = StringUtils.isNotBlank(this.txtRightAbbr.getText());
        winnerName = txtRightName().getText() + (showFlag ? (" (" + this.txtRightAbbr.getText() + ")") : "");
        if (showFlag && pnRightFlag().getChildren() != null && pnRightFlag().getChildren().size() > 0) {
          ivWinner.setImage(((ImageView)pnRightFlag().getChildren().get(0)).getImage());
        } else {
          ivWinner.setImage(null);
        } 
        winner.getStyleClass().addAll((Object[])new String[] { "sb-red" });
      } else {
        boolean showFlag = StringUtils.isNotBlank(this.txtLeftAbbr.getText());
        winnerName = txtLeftName().getText() + (showFlag ? (" (" + this.txtLeftAbbr.getText() + ")") : "");
        if (showFlag && pnLeftFlag().getChildren() != null && pnLeftFlag().getChildren().size() > 0) {
          ivWinner.setImage(((ImageView)pnLeftFlag().getChildren().get(0)).getImage());
        } else {
          ivWinner.setImage(null);
        } 
        winner.getStyleClass().addAll((Object[])new String[] { "sb-red" });
      } 
      tie = false;
    } 
    if (tie) {
      finalDecission.setText(getMessage("label.matchWinner.TIE").toUpperCase());
    } else {
      winner.setText(winnerName.toUpperCase());
    } 
    winner.setVisible(!tie);
    ivWinner.setVisible(!tie);
  }
  
  private void _refreshRoundsWinner(final Integer roundNumber) {
    Platform.runLater(new Runnable() {
          public void run() {
            if (!CommonScoreboardController.this.getMatchWorker().isParaTkdMatch()) {
              CommonScoreboardController.this.pnLeftRoundWins.getChildren().clear();
              CommonScoreboardController.this.pnRightRoundWins.getChildren().clear();
              for (int i = 1; i < roundNumber.intValue(); i++) {
                MatchWinner matchWinner = CommonScoreboardController.this.getMatchWorker().getRoundWinner(Integer.valueOf(i));
                if (matchWinner != null)
                  if (MatchWinner.RED.equals(matchWinner)) {
                    StackPane stackPane = CommonScoreboardController.this.createRoundWinnerStackPane(false);
                    if (CommonScoreboardController.this.isBlueOnLeft()) {
                      CommonScoreboardController.this.pnRightRoundWins.getChildren().add(stackPane);
                    } else {
                      CommonScoreboardController.this.pnLeftRoundWins.getChildren().add(stackPane);
                    } 
                  } else if (MatchWinner.BLUE.equals(matchWinner)) {
                    StackPane stackPane = CommonScoreboardController.this.createRoundWinnerStackPane(true);
                    if (CommonScoreboardController.this.isBlueOnLeft()) {
                      CommonScoreboardController.this.pnLeftRoundWins.getChildren().add(stackPane);
                    } else {
                      CommonScoreboardController.this.pnRightRoundWins.getChildren().add(stackPane);
                    } 
                  }  
              } 
            } 
          }
        });
  }
  
  private void _refreshWinnerInfo() {
    if (this.showWinnerProperty.getValue().booleanValue()) {
      Platform.runLater(new Runnable() {
            public void run() {
              CommonScoreboardController.this.putWinnerInfo(CommonScoreboardController.this.lblMatchWinnerTitle, CommonScoreboardController.this.lblFinalDecission, CommonScoreboardController.this.lblWinner, CommonScoreboardController.this.ivWinnerFlag);
              if (MatchVictoryCriteria.BESTOF3.equals(CommonScoreboardController.this.getMatchWorker().getMatchVictoryCriteria())) {
                CommonScoreboardController.this.lblFinalBlueResult.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueRoundsWins());
                CommonScoreboardController.this.lblFinalRedResult.setText("" + CommonScoreboardController.this.getMatchWorker().getRedRoundsWins());
              } else if (CommonScoreboardController.this.lblFinalBlueResult != null && CommonScoreboardController.this.lblFinalRedResult != null) {
                CommonScoreboardController.this.lblFinalBlueResult.setText("" + CommonScoreboardController.this.getMatchWorker().getBlueGeneralPoints());
                CommonScoreboardController.this.lblFinalRedResult.setText("" + CommonScoreboardController.this.getMatchWorker().getRedGeneralPoints());
              } 
              boolean isTie = !CommonScoreboardController.this.lblWinner.isVisible();
              CommonScoreboardController.this.lblMatchWinnerTitle.setVisible(true);
              CommonScoreboardController.this.lblFinalDecission().setVisible(true);
              if (!isTie) {
                CommonScoreboardController.this.lblWinner().setVisible(true);
                CommonScoreboardController.this.ivWinnerFlag().setVisible(true);
              } 
              CommonScoreboardController.this.pnShowWinner().setVisible(!CommonScoreboardController.this.getMatchWorker().superiorityByRoundsProperty().get());
            }
          });
    } else {
      pnShowWinner().setVisible(false);
    } 
  }
  
  private Node getNode4Impact(int impactValue) {
    return (impactValue >= 0) ? 
      TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().getNode4BodyImpact(this.scoreboardGraphicDetailType, getImpactImageHeight()) : 
      
      TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().getNode4HeadImpact(this.scoreboardGraphicDetailType, getImpactImageHeight());
  }
  
  private void _showImpact(final int position, final Pane container, final Integer impactValue, final boolean flash, final Timeline timelineFlash) {
    Platform.runLater(new Runnable() {
          public void run() {
            try {
              final StackPane stackPane = (StackPane)container.getChildren().get(position);
              stackPane.getChildren().clear();
              stackPane.getChildren().add(CommonScoreboardController.this.getNode4Impact(impactValue.intValue()));
              if (!CommonScoreboardController.this.getMatchWorker().isBackupSystemEnabled()) {
                Text impactText = new Text();
                impactText.getStyleClass().addAll((Object[])CommonScoreboardController.this.getTextImpactStyleClass());
                if (impactValue.intValue() == 100 || impactValue.intValue() == -100) {
                  impactText.setText("99");
                } else {
                  impactText.setText((impactValue.intValue() < 0) ? ("" + (impactValue.intValue() * -1)) : impactValue.toString());
                } 
                impactText.setTextAlignment(TextAlignment.CENTER);
                StackPane.setAlignment((Node)impactText, Pos.CENTER);
                stackPane.getChildren().add(impactText);
              } 
              stackPane.setUserData(impactValue);
              if (flash && timelineFlash != null && 
                CommonScoreboardController.this.showImpactWithFlash.booleanValue())
                Platform.runLater(new Runnable() {
                      public void run() {
                        if (Animation.Status.RUNNING.equals(timelineFlash.getStatus())) {
                          timelineFlash.stop();
                          stackPane.setVisible(true);
                        } 
                        timelineFlash.playFromStart();
                      }
                    }); 
            } catch (RuntimeException e) {
              e.printStackTrace();
            } 
          }
        });
  }
  
  protected abstract RoundCountdownController getRoundCountdownController();
  
  protected abstract Double getImpactImageHeight();
  
  protected abstract double getFlagImageHeight();
  
  protected abstract int getHitsControlDefaultFontSize();
  
  protected abstract double getHitsControlHeight();
  
  protected abstract double getHitsControlSpacing();
  
  protected abstract double getHitsControlJudgesWidth();
  
  protected abstract double getHitsControlHitWidth();
  
  protected abstract boolean isBlueOnLeft();
  
  protected abstract void setBlueOnLeft(boolean paramBoolean);
  
  protected abstract boolean cleanPointsWhenGoldenPointEnabled();
  
  abstract MW getMatchWorker();
  
  protected abstract void _commonInternalOnWindowShowEvent();
  
  protected abstract void _commonInternalBindUIControls();
  
  protected abstract void _commonInternalOnWindowCloseEvent();
  
  protected abstract void _commonInternalUnbindUIControls();
  
  protected abstract void _commonInternalInitialize(URL paramURL, ResourceBundle paramResourceBundle);
  
  protected abstract void _commonInternalAfterPropertiesSet() throws Exception;
  
  class AthletesPointsTextListener implements ChangeListener<String> {
    private final Timeline flash;
    
    private final Text txt;
    
    public AthletesPointsTextListener(Timeline flash, Text txt) {
      this.flash = flash;
      this.txt = txt;
    }
    
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      if (StringUtils.isNumeric(newValue) && Integer.parseInt(newValue) > 0)
        Platform.runLater(new Runnable() {
              public void run() {
                if (Animation.Status.RUNNING.equals(CommonScoreboardController.AthletesPointsTextListener.this.flash.getStatus())) {
                  CommonScoreboardController.AthletesPointsTextListener.this.flash.stop();
                  CommonScoreboardController.AthletesPointsTextListener.this.txt.setVisible(true);
                } 
                CommonScoreboardController.AthletesPointsTextListener.this.flash.playFromStart();
              }
            }); 
    }
  }
  
  class SimpleIntegerToLabelListener implements ChangeListener<Number> {
    private final Label theLabel;
    
    public SimpleIntegerToLabelListener(Label theLabel) {
      this.theLabel = theLabel;
    }
    
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
      Platform.runLater(new Runnable() {
            public void run() {
              if (newValue != null)
                CommonScoreboardController.SimpleIntegerToLabelListener.this.theLabel.setText("" + newValue); 
            }
          });
    }
  }
  
  class LastImpactListener implements ChangeListener<Number> {
    private final Boolean left;
    
    public LastImpactListener(Boolean left) {
      this.left = left;
    }
    
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newImpact) {
      if (newImpact.intValue() != 0 && !MatchVictoryCriteria.BESTOF3.equals(CommonScoreboardController.this.getMatchWorker().getMatchVictoryCriteria()))
        Platform.runLater(new Runnable() {
              public void run() {
                ObservableList<Integer> impacts2Show = CommonScoreboardController.this.leftImpacts2Show;
                HBox pnLastImpact = CommonScoreboardController.this.pnLeftLastImpact;
                if (!CommonScoreboardController.LastImpactListener.this.left.booleanValue()) {
                  impacts2Show = CommonScoreboardController.this.rightImpacts2Show;
                  pnLastImpact = CommonScoreboardController.this.pnRightLastImpact;
                } 
                int n = 0;
                if (impacts2Show.size() >= 1) {
                  n = 1;
                  StackPane node = (StackPane)pnLastImpact.getChildren().get(0);
                  if (node.getUserData() != null && node.getUserData() instanceof Integer)
                    CommonScoreboardController.this._showImpact(1, (Pane)pnLastImpact, (Integer)node.getUserData(), false, (Timeline)null); 
                  if (impacts2Show.size() == 2)
                    impacts2Show.remove(0); 
                } 
                impacts2Show.add(Integer.valueOf(newImpact.intValue()));
                n = 0;
                CommonScoreboardController.this._showImpact(n, (Pane)pnLastImpact, Integer.valueOf(newImpact.intValue()), true, (CommonScoreboardController.LastImpactListener.this.left.booleanValue() && n == 0) ? CommonScoreboardController.this.flashLeftImpact1 : ((CommonScoreboardController.LastImpactListener.this.left.booleanValue() && n == 1) ? CommonScoreboardController.this.flashLeftImpact2 : ((!CommonScoreboardController.LastImpactListener.this.left.booleanValue() && n == 0) ? CommonScoreboardController.this.flashRightImpact1 : ((!CommonScoreboardController.LastImpactListener.this.left.booleanValue() && n == 1) ? CommonScoreboardController.this.flashRightImpact2 : null))));
              }
            }); 
    }
  }
  
  static class BestOf3SupLeadPane extends BorderPane {
    private final Color COLOR_GREEN = Color.web("#00ff00");
    
    private final StackPane circleContainer;
    
    private final boolean blue;
    
    public BestOf3SupLeadPane(ReadOnlyObjectProperty<MatchWinner> bestOf3CurrentRoundPartialWinner, boolean blue) {
      this.blue = blue;
      setMinHeight(-1.0D);
      setPrefHeight(-1.0D);
      setMaxHeight(-1.0D);
      setMinWidth(-1.0D);
      setPrefWidth(-1.0D);
      setMaxWidth(-1.0D);
      setPadding(new Insets(5.0D));
      this.circleContainer = new StackPane();
      this.circleContainer.setAlignment(Pos.CENTER);
      this.circleContainer.setVisible(false);
      Circle circle = new Circle(15.0D);
      circle.setStrokeWidth(0.0D);
      circle.setFill((Paint)this.COLOR_GREEN);
      Circle circle2 = new Circle(12.0D);
      circle2.setStrokeWidth(4.0D);
      circle2.setStroke((Paint)Color.BLACK);
      circle2.setFill((Paint)this.COLOR_GREEN);
      StackPane.setAlignment((Node)circle, Pos.CENTER);
      StackPane.setAlignment((Node)circle2, Pos.CENTER);
      this.circleContainer.getChildren().addAll((Object[])new Node[] { (Node)circle, (Node)circle2 });
      updateByMatchWinner((MatchWinner)bestOf3CurrentRoundPartialWinner.getValue());
      bestOf3CurrentRoundPartialWinner.addListener(new ChangeListener<MatchWinner>() {
            public void changed(ObservableValue<? extends MatchWinner> observable, MatchWinner oldValue, MatchWinner newValue) {
              CommonScoreboardController.BestOf3SupLeadPane.this.updateByMatchWinner(newValue);
            }
          });
      BorderPane.setAlignment((Node)this.circleContainer, Pos.CENTER);
      setCenter((Node)this.circleContainer);
      Label supLeadLabel = new Label("SUP LEAD");
      supLeadLabel.getStyleClass().addAll((Object[])new String[] { "bestOf3SpecialLabel" });
      BorderPane.setAlignment((Node)supLeadLabel, Pos.CENTER);
      setTop((Node)supLeadLabel);
    }
    
    private void updateByMatchWinner(MatchWinner matchWinner) {
      boolean visible = false;
      if (matchWinner != null)
        if (this.blue && MatchWinner.BLUE.equals(matchWinner)) {
          visible = true;
        } else if (!this.blue && MatchWinner.RED.equals(matchWinner)) {
          visible = true;
        }  
      final boolean fiVisible = visible;
      Platform.runLater(new Runnable() {
            public void run() {
              CommonScoreboardController.BestOf3SupLeadPane.this.circleContainer.setVisible(fiVisible);
            }
          });
    }
  }
  
  static class BestOf3HitsPane extends BorderPane {
    private final Label labelHits;
    
    public BestOf3HitsPane(ReadOnlyIntegerProperty hitsProperty) {
      setMinHeight(-1.0D);
      setPrefHeight(-1.0D);
      setMaxHeight(-1.0D);
      setMinWidth(-1.0D);
      setPrefWidth(-1.0D);
      setMaxWidth(-1.0D);
      setPadding(new Insets(5.0D));
      this.labelHits = new Label((hitsProperty.getValue() != null) ? ("" + hitsProperty.getValue()) : "0");
      this.labelHits.getStyleClass().addAll((Object[])new String[] { "bestOf3HitsLabel" });
      BorderPane.setAlignment((Node)this.labelHits, Pos.CENTER);
      setCenter((Node)this.labelHits);
      hitsProperty.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
              Platform.runLater(new Runnable() {
                    public void run() {
                      if (newValue != null) {
                        CommonScoreboardController.BestOf3HitsPane.this.labelHits.setText(newValue.toString());
                      } else {
                        CommonScoreboardController.BestOf3HitsPane.this.labelHits.setText("0");
                      } 
                    }
                  });
            }
          });
      Label supLeadLabel = new Label("HITS");
      supLeadLabel.getStyleClass().addAll((Object[])new String[] { "bestOf3SpecialLabel" });
      BorderPane.setAlignment((Node)supLeadLabel, Pos.CENTER);
      setTop((Node)supLeadLabel);
    }
  }
}
