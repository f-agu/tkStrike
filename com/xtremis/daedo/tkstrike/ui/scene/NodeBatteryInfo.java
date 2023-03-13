package com.xtremis.daedo.tkstrike.ui.scene;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import com.xtremis.daedo.tkstrike.om.SensorNodeType;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;


public class NodeBatteryInfo extends StackPane {

	private static final DecimalFormat pctFormat = new DecimalFormat("0.00 '%'");

	private String nodeId;

	private final Integer groupNumber;

	private final SensorNodeType sensorNodeType;

	private final Boolean blue;

	private final boolean showPct;

	private Rectangle rectangle;

	private Text text;

	private Boolean sensorOk = Boolean.FALSE;

	private Boolean offLine = Boolean.TRUE;

	private Double battery = Double.valueOf(0.0D);

	private SimpleBooleanProperty changeOnInfo = new SimpleBooleanProperty(this, "changeOnInfo");

	private final Double height;

	private final Double width;

	private String textStyleClass;

	public NodeBatteryInfo(String nodeId, boolean showPct, Double height, Double width, String textStyleClass) {
		this(Integer.valueOf(0), nodeId, SensorNodeType.JUDGE, null, showPct, height, width, textStyleClass);
	}

	public NodeBatteryInfo(Integer groupNumber, String nodeId, SensorNodeType sensorNodeType, Boolean blue, boolean showPct, Double height,
			Double width, String textStyleClass) {
		this.groupNumber = groupNumber;
		this.nodeId = nodeId;
		this.sensorNodeType = sensorNodeType;
		this.blue = blue;
		this.showPct = showPct;
		this.height = height;
		this.width = width;
		this.textStyleClass = textStyleClass;
		this.rectangle = new Rectangle();
		this.rectangle.setArcHeight(5.0D);
		this.rectangle.setArcWidth(5.0D);
		this.rectangle.setHeight(height.doubleValue());
		this.rectangle.setWidth(width.doubleValue());
		this.rectangle.setStrokeType(StrokeType.INSIDE);
		this.rectangle.setStrokeWidth(2.0D);
		this.rectangle.setStroke(SensorNodeType.JUDGE.equals(this.sensorNodeType) ? (Paint)Color.WHITE
				: (this.blue.booleanValue() ? (Paint)Color.BLUE : (Paint)Color.RED));
		this.rectangle.setFill(Color.RED);
		this.text = new Text("");
		this.text.setFill(Color.WHITE);
		this.text.setStrokeType(StrokeType.OUTSIDE);
		this.text.setStrokeWidth(0.0D);
		if(StringUtils.isNotBlank(textStyleClass))
			this.text.getStyleClass().addAll(new String[] {textStyleClass});
		StackPane.setAlignment(this.rectangle, Pos.CENTER);
		StackPane.setAlignment(this.text, Pos.CENTER);
		getChildren().addAll(new Node[] {this.rectangle, this.text});
		this.changeOnInfo.addListener(new ChangeOnComponentListener());
	}

	public Integer getGroupNumber() {
		return this.groupNumber;
	}

	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public SensorNodeType getSensorNodeType() {
		return this.sensorNodeType;
	}

	public Boolean getBlue() {
		return this.blue;
	}

	public void setSensorOk(Boolean sensorOk) {
		this.sensorOk = sensorOk;
		this.changeOnInfo.set(Boolean.TRUE.booleanValue());
		this.changeOnInfo.set(Boolean.FALSE.booleanValue());
	}

	public void setOffLine(Boolean offLine) {
		this.offLine = offLine;
		this.changeOnInfo.set(Boolean.TRUE.booleanValue());
		this.changeOnInfo.set(Boolean.FALSE.booleanValue());
	}

	public void setBattery(Double battery) {
		this.battery = battery;
		this.changeOnInfo.set(Boolean.TRUE.booleanValue());
		this.changeOnInfo.set(Boolean.FALSE.booleanValue());
	}

	public void updateComponent(Boolean sensorOk, Boolean offLine, Double battery) {
		this.sensorOk = sensorOk;
		this.offLine = offLine;
		this.battery = battery;
		this.changeOnInfo.set(Boolean.TRUE.booleanValue());
		this.changeOnInfo.set(Boolean.FALSE.booleanValue());
	}

	class ChangeOnComponentListener implements ChangeListener<Boolean> {

		@Override
		public void changed(ObservableValue observable, Boolean oldValue, final Boolean newValue) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					if(newValue.booleanValue()) {
						if( ! NodeBatteryInfo.this.showPct) {
							NodeBatteryInfo.this.text.setText((NodeBatteryInfo.this.battery.doubleValue() <= 20.0D) ? "LOW" : "OK");
						} else {
							try {
								NodeBatteryInfo.this.text.setText((NodeBatteryInfo.this.battery != null) ? NodeBatteryInfo.pctFormat.format(
										NodeBatteryInfo.this.battery) : "");
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
						if(NodeBatteryInfo.this.offLine.booleanValue()) {
							NodeBatteryInfo.this.text.setText("");
							NodeBatteryInfo.this.rectangle.setFill(Color.RED);
						} else if(NodeBatteryInfo.this.sensorOk.booleanValue() && NodeBatteryInfo.this.battery.doubleValue() > 20.0D) {
							NodeBatteryInfo.this.rectangle.setFill(Color.GREEN);
						} else {
							NodeBatteryInfo.this.rectangle.setFill(Color.ORANGE);
						}
					}
				}
			});
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if( ! (o instanceof NodeBatteryInfo))
			return false;
		NodeBatteryInfo that = (NodeBatteryInfo)o;
		return this.nodeId.equals(that.nodeId);
	}

	@Override
	public int hashCode() {
		return this.nodeId.hashCode();
	}
}
