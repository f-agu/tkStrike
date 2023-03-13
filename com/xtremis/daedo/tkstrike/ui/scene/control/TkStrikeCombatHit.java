package com.xtremis.daedo.tkstrike.ui.scene.control;

import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailType;
import com.xtremis.daedo.tkstrike.configuration.TkStrikeScoreboardGraphicDetailTypeUtil;
import com.xtremis.daedo.tkstrike.om.combat.HitEventType;
import com.xtremis.daedo.tkstrike.om.combat.HitEventValidator;
import com.xtremis.daedo.tkstrike.om.combat.HitJudgeStatus;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;


public class TkStrikeCombatHit extends HBox {

	private static final Image IMAGE_HEAD_HIT = new Image(TkStrikeCombatHit.class.getResourceAsStream("/images/headGearLittle.png"));

	private static final Image IMAGE_BODY_HIT = new Image(TkStrikeCombatHit.class.getResourceAsStream("/images/trunkLittle.png"));

	private static final Image IMAGE_PUNCH_HIT = new Image(TkStrikeCombatHit.class.getResourceAsStream("/images/punchLittle.png"));

	private static final Color COLOR_PUNCH = Color.web("#eaff00");

	private static final Color COLOR_HEAD = Color.web("#00ff00");

	private static final Color COLOR_SPECIAL_HEAD = Color.WHITE;

	private static final Color COLOR_SPINNING_HEAD = Color.web("#bf12f6");

	private static final Color COLOR_BODY = Color.web("#00ff00");

	private final TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType;

	private final long hitTimestamp;

	private final HitEventType hitEventType;

	private final boolean backupSystemEnabled;

	private HitEventValidator.ParaTkdTechEvent paraTkdTechEvent = HitEventValidator.ParaTkdTechEvent.NONE;

	private final int judgesEnabled;

	private StackPane stJudge1;

	private Shape ciJudge1;

	private SimpleObjectProperty<HitJudgeStatus> judge1HitStatus = new SimpleObjectProperty(this, "judge1HitStatus", HitJudgeStatus.NOT_ENABLED);

	private HitJudgeStatus prevJudge1HitStatus = HitJudgeStatus.NOT_ENABLED;

	private StackPane stJudge2;

	private Shape ciJudge2;

	private SimpleObjectProperty<HitJudgeStatus> judge2HitStatus = new SimpleObjectProperty(this, "judge2HitStatus", HitJudgeStatus.NOT_ENABLED);

	private HitJudgeStatus prevJudge2HitStatus = HitJudgeStatus.NOT_ENABLED;

	private StackPane stJudge3;

	private Shape ciJudge3;

	private SimpleObjectProperty<HitJudgeStatus> judge3HitStatus = new SimpleObjectProperty(this, "judge3HitStatus", HitJudgeStatus.NOT_ENABLED);

	private HitJudgeStatus prevJudge3HitStatus = HitJudgeStatus.NOT_ENABLED;

	private StackPane stHit;

	public TkStrikeCombatHit(TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType, long hitTimestamp, HitEventType hitEventType,
			int hitValue, int judgesEnabled, int defaultFontSize, Double controlHeight, Double controlSpacing, Double controlJudgesWidth,
			Double controlHitWidth, boolean hitFirst, boolean backupSystemEnabled, HitJudgeStatus prevJudge1HitStatus,
			HitJudgeStatus prevJudge2HitStatus, HitJudgeStatus prevJudge3HitStatus) {
		this.scoreboardGraphicDetailType = scoreboardGraphicDetailType;
		this.hitTimestamp = hitTimestamp;
		this.hitEventType = hitEventType;
		this.judgesEnabled = judgesEnabled;
		this.backupSystemEnabled = backupSystemEnabled;
		this.prevJudge1HitStatus = prevJudge1HitStatus;
		this.prevJudge2HitStatus = prevJudge2HitStatus;
		this.prevJudge3HitStatus = prevJudge3HitStatus;
		setHeight(controlHeight.doubleValue());
		setMaxHeight(controlHeight.doubleValue());
		setMinHeight(controlHeight.doubleValue());
		setPrefHeight(controlHeight.doubleValue());
		setSpacing(controlSpacing.doubleValue());
		setAlignment(Pos.TOP_CENTER);
		this.stJudge1 = new StackPane();
		this.stJudge1.setPrefWidth(controlJudgesWidth.doubleValue());
		this.stJudge1.setMinWidth(controlJudgesWidth.doubleValue());
		this.stJudge1.setMaxWidth(controlJudgesWidth.doubleValue());
		this.stJudge1.setAlignment(Pos.CENTER);
		this.ciJudge1 = getShapeForHit(hitEventType, controlHitWidth, controlHeight);
		Text txtJ1 = new Text("1");
		txtJ1.setStyle("-fx-font-size: " + defaultFontSize + "; -fx-font-weight: bold;");
		txtJ1.getStyleClass().addAll(new String[] {"df-listHitValues"});
		this.stJudge1.getChildren().add(this.ciJudge1);
		this.stJudge1.getChildren().add(txtJ1);
		this.judge1HitStatus.addListener(new JudgeHitStatusChangeListener(1, this.ciJudge1, txtJ1, this.stJudge1, controlJudgesWidth));
		this.judge1HitStatus.set(HitJudgeStatus.NOT_VALIDATED);
		this.stJudge2 = new StackPane();
		this.stJudge2.setPrefWidth(controlJudgesWidth.doubleValue());
		this.stJudge2.setMinWidth(controlJudgesWidth.doubleValue());
		this.stJudge2.setMaxWidth(controlJudgesWidth.doubleValue());
		this.stJudge2.setAlignment(Pos.CENTER);
		if(judgesEnabled >= 2) {
			this.ciJudge2 = getShapeForHit(hitEventType, controlHitWidth, controlHeight);
			Text txtJ2 = new Text("2");
			txtJ2.getStyleClass().addAll(new String[] {"df-listHitValues"});
			txtJ2.setStyle("-fx-font-size: " + defaultFontSize + "; -fx-font-weight: bold;");
			this.stJudge2.getChildren().addAll(new Node[] {this.ciJudge2, txtJ2});
			this.judge2HitStatus.addListener(new JudgeHitStatusChangeListener(2, this.ciJudge2, txtJ2, this.stJudge2, controlJudgesWidth));
			this.judge2HitStatus.set(HitJudgeStatus.NOT_VALIDATED);
		}
		this.stJudge3 = new StackPane();
		this.stJudge3.setPrefWidth(controlJudgesWidth.doubleValue());
		this.stJudge3.setMinWidth(controlJudgesWidth.doubleValue());
		this.stJudge3.setMaxWidth(controlJudgesWidth.doubleValue());
		this.stJudge3.setAlignment(Pos.CENTER);
		if(judgesEnabled >= 3) {
			this.ciJudge3 = getShapeForHit(hitEventType, controlHitWidth, controlHeight);
			Text txtJ3 = new Text("3");
			txtJ3.getStyleClass().addAll(new String[] {"df-listHitValues"});
			txtJ3.setStyle("-fx-font-size: " + defaultFontSize + "; -fx-font-weight: bold;");
			this.stJudge3.getChildren().addAll(new Node[] {this.ciJudge3, txtJ3});
			this.judge3HitStatus.addListener(new JudgeHitStatusChangeListener(3, this.ciJudge3, txtJ3, this.stJudge3, controlJudgesWidth));
			this.judge3HitStatus.set(HitJudgeStatus.NOT_VALIDATED);
		}
		this.stHit = new StackPane();
		this.stHit.setPrefWidth(controlHitWidth.doubleValue());
		this.stHit.setMinWidth(controlHitWidth.doubleValue());
		this.stHit.setMaxWidth(controlHitWidth.doubleValue());
		this.stHit.setAlignment(Pos.CENTER);
		if(hitValue >= 0 || isBackupSystemEnabled())
			switch(this.hitEventType) {
				case PUNCH:
				case BODY:
					if(hitValue <= 100 || this.hitEventType
							.equals(HitEventType.SPECIAL_HEAD) ||
							isBackupSystemEnabled())
						this.stHit.getChildren().add(TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().getNode4HeadImpact(
								scoreboardGraphicDetailType, controlHeight));
					break;
				case HEAD:
				case SPECIAL_HEAD:
				case SPECIAL_BODY:
				case PARA_SPINNING:
					if(hitValue <= 100 || this.hitEventType.equals(HitEventType.SPECIAL_BODY) ||
							isBackupSystemEnabled())
						this.stHit.getChildren().add(TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().getNode4BodyImpact(
								scoreboardGraphicDetailType, controlHeight));
					break;
				case PARA_TURNING:
					this.stHit.getChildren().add(TkStrikeScoreboardGraphicDetailTypeUtil.getInstance().getNode4PunchImpact(
							scoreboardGraphicDetailType, controlHeight));
					break;
			}
		if(HitEventType.PARA_SPINNING.equals(this.hitEventType)) {
			this.paraTkdTechEvent = HitEventValidator.ParaTkdTechEvent.SPINNING_KICK_TECH;
		} else if(HitEventType.PARA_TURNING.equals(this.hitEventType)) {
			this.paraTkdTechEvent = HitEventValidator.ParaTkdTechEvent.TURNING_KICK_TECH;
		}
		if(hitFirst) {
			getChildren().addAll(new Node[] {this.stHit, this.stJudge1, this.stJudge2, this.stJudge3});
		} else {
			getChildren().addAll(new Node[] {this.stJudge1, this.stJudge2, this.stJudge3, this.stHit});
		}
	}

	public TkStrikeCombatHit(TkStrikeScoreboardGraphicDetailType scoreboardGraphicDetailType, long hitTimestamp, HitEventType hitEventType,
			int hitValue, int judgesEnabled, int defaultFontSize, Double controlHeight, Double controlSpacing, Double controlJudgesWidth,
			Double controlHitWidth, boolean hitFirst, boolean backupSystemEnabled) {
		this(scoreboardGraphicDetailType, hitTimestamp, hitEventType, hitValue, judgesEnabled, defaultFontSize, controlHeight, controlSpacing,
				controlJudgesWidth, controlHitWidth, hitFirst, backupSystemEnabled, HitJudgeStatus.NOT_ENABLED, HitJudgeStatus.NOT_ENABLED,
				HitJudgeStatus.NOT_ENABLED);
	}

	public int getJudgesEnabled() {
		return this.judgesEnabled;
	}

	public boolean isJudgesTechValidated() {
		switch(this.judgesEnabled) {
			case 1:
				return this.judge1HitStatus.getValue().equals(HitJudgeStatus.VALIDATED);
			case 2:
				return (this.judge1HitStatus.getValue().equals(HitJudgeStatus.VALIDATED) && this.judge2HitStatus.getValue().equals(
						HitJudgeStatus.VALIDATED));
			case 3:
				return ((this.judge1HitStatus.getValue().equals(HitJudgeStatus.VALIDATED) && this.judge2HitStatus.getValue().equals(
						HitJudgeStatus.VALIDATED)) || (this.judge1HitStatus
								.getValue().equals(HitJudgeStatus.VALIDATED) && this.judge3HitStatus.getValue().equals(HitJudgeStatus.VALIDATED))
						|| (this.judge2HitStatus
								.getValue().equals(HitJudgeStatus.VALIDATED) && this.judge3HitStatus.getValue().equals(HitJudgeStatus.VALIDATED)));
		}
		return false;
	}

	public HitEventType getHitEventType() {
		return this.hitEventType;
	}

	public boolean isBackupSystemEnabled() {
		return this.backupSystemEnabled;
	}

	public void clearAllJudgesStatus() {
		this.judge1HitStatus.set(HitJudgeStatus.NOT_VALIDATED);
		if(this.judgesEnabled >= 2)
			this.judge2HitStatus.set(HitJudgeStatus.NOT_VALIDATED);
		if(this.judgesEnabled >= 3)
			this.judge3HitStatus.set(HitJudgeStatus.NOT_VALIDATED);
	}

	public void setPrevJudge1HitStatus(HitJudgeStatus prevJudge1HitStatus) {
		this.prevJudge1HitStatus = prevJudge1HitStatus;
	}

	public void setPrevJudge2HitStatus(HitJudgeStatus prevJudge2HitStatus) {
		this.prevJudge2HitStatus = prevJudge2HitStatus;
	}

	public void setPrevJudge3HitStatus(HitJudgeStatus prevJudge3HitStatus) {
		this.prevJudge3HitStatus = prevJudge3HitStatus;
	}

	public void changeJudge1HitStatus(HitJudgeStatus newHitJudgeStatus) {
		if(newHitJudgeStatus != null)
			this.judge1HitStatus.set(newHitJudgeStatus);
	}

	public void changeJudge2HitStatus(HitJudgeStatus newHitJudgeStatus) {
		if(newHitJudgeStatus != null)
			this.judge2HitStatus.set(newHitJudgeStatus);
	}

	public void changeJudge3HitStatus(HitJudgeStatus newHitJudgeStatus) {
		if(newHitJudgeStatus != null)
			this.judge3HitStatus.set(newHitJudgeStatus);
	}

	public HitJudgeStatus getJudge1Status() {
		return this.judge1HitStatus.get();
	}

	public HitJudgeStatus getJudge2Status() {
		return this.judge2HitStatus.get();
	}

	public HitJudgeStatus getJudge3Status() {
		return this.judge3HitStatus.get();
	}

	public HitEventValidator.ParaTkdTechEvent getParaTkdTechEvent() {
		return this.paraTkdTechEvent;
	}

	public void setParaTkdTechEvent(HitEventValidator.ParaTkdTechEvent paraTkdTechEvent) {
		this.paraTkdTechEvent = paraTkdTechEvent;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if( ! (o instanceof TkStrikeCombatHit))
			return false;
		TkStrikeCombatHit that = (TkStrikeCombatHit)o;
		if(this.hitTimestamp != that.hitTimestamp)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return (int)(this.hitTimestamp ^ this.hitTimestamp >>> 32L);
	}

	@Override
	public String toString() {
		return "TkStrikeCombatHit{hitTimestamp=" + this.hitTimestamp + ", hitEventType=" + this.hitEventType + "} ";
	}

	private Shape getShapeForHit(HitEventType hitEventType, Double controlHitWidth, Double controlHeight) {
		Ellipse ellipse1;
		Rectangle rectangle1 = null;
		Ellipse eliHit;
		Rectangle rect, recHitPunch;
		Shape res = null;
		switch(hitEventType) {
			case PUNCH:
				eliHit = new Ellipse();
				eliHit.setRadiusX(controlHitWidth.doubleValue() / 2.0D);
				eliHit.setRadiusY(controlHeight.doubleValue() / 2.0D);
				ellipse1 = eliHit;
				break;
			case BODY:
			case SPECIAL_BODY:
			case PARA_SPINNING:
			case PARA_TURNING:
				rect = new Rectangle();
				rect.setWidth(controlHitWidth.doubleValue());
				rect.setHeight(controlHeight.doubleValue());
				rectangle1 = rect;
				break;
			case HEAD:
			case SPECIAL_HEAD:
				recHitPunch = new Rectangle();
				recHitPunch.setWidth(controlHitWidth.doubleValue());
				recHitPunch.setHeight(controlHeight.doubleValue());
				rectangle1 = recHitPunch;
				break;
		}
		return rectangle1;
	}

	public long getHitTimestamp() {
		return this.hitTimestamp;
	}

	private final class JudgeHitStatusChangeListener implements ChangeListener<HitJudgeStatus> {

		private Shape ciJudge;

		private final int judgeNumber;

		private final Text text;

		private final StackPane parent;

		private final Double controlWidth;

		public JudgeHitStatusChangeListener(int judgeNumber, Shape ciJudge, Text text, StackPane parent, Double controlWidth) {
			this.judgeNumber = judgeNumber;
			this.ciJudge = ciJudge;
			this.text = text;
			this.parent = parent;
			this.controlWidth = controlWidth;
		}

		@Override
		public void changed(ObservableValue<? extends HitJudgeStatus> observableValue, HitJudgeStatus hitJudgeStatus, HitJudgeStatus newJudgeStatus) {
			Color fillColor = Color.BLACK;
			if(newJudgeStatus.equals(HitJudgeStatus.NOT_VALIDATED)) {
				boolean showWhite = false;
				switch(this.judgeNumber) {
					case 1:
						showWhite = HitJudgeStatus.VALIDATED.equals(TkStrikeCombatHit.this.prevJudge1HitStatus);
						break;
					case 2:
						showWhite = HitJudgeStatus.VALIDATED.equals(TkStrikeCombatHit.this.prevJudge2HitStatus);
						break;
					case 3:
						showWhite = HitJudgeStatus.VALIDATED.equals(TkStrikeCombatHit.this.prevJudge3HitStatus);
						break;
				}
				if(showWhite) {
					fillColor = Color.WHITE;
					this.ciJudge.setVisible(true);
					this.text.setVisible(true);
				} else {
					this.ciJudge.setVisible(false);
					this.text.setVisible(false);
				}
			} else if(newJudgeStatus.equals(HitJudgeStatus.VALIDATED)) {
				double base;
				Polygon polygon;
				switch(TkStrikeCombatHit.this.hitEventType) {
					case SPINNING_KICK_TECH:
						fillColor = TkStrikeCombatHit.COLOR_HEAD;
						break;
					case TURNING_KICK_TECH:
					case null:
						fillColor = TkStrikeCombatHit.COLOR_SPECIAL_HEAD;
						break;
					case NONE:
					case null:
					case null:
						switch(TkStrikeCombatHit.this.paraTkdTechEvent) {
							case SPINNING_KICK_TECH:
								fillColor = TkStrikeCombatHit.COLOR_SPINNING_HEAD;
								base = this.controlWidth.doubleValue() / 2.0D;
								polygon = new Polygon();
								polygon.getPoints().addAll(new Double[] {Double.valueOf(base * - 1.0D), Double.valueOf(base),
										Double.valueOf(base), Double.valueOf(base),
										Double.valueOf(0.0D), Double.valueOf(base * - 1.0D)});
								this.ciJudge = polygon;
								this.parent.getChildren().set(0, this.ciJudge);
								break;
							case TURNING_KICK_TECH:
							case NONE:
								fillColor = TkStrikeCombatHit.COLOR_BODY;
								break;
						}
						break;
					case null:
						fillColor = TkStrikeCombatHit.COLOR_PUNCH;
						break;
				}
				this.ciJudge.setVisible(true);
				this.text.setVisible(true);
			}
			this.ciJudge.setFill(fillColor);
		}
	}
}
