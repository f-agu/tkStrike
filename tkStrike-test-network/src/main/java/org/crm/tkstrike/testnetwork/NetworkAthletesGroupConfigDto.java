package org.crm.tkstrike.testnetwork;

import java.io.Serializable;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class NetworkAthletesGroupConfigDto implements Serializable {
	private static final long serialVersionUID = -7833590997560335364L;

	private final Integer groupNumber;

	private Boolean headSensorsEnabled;

	private Boolean bodySensorsEnabled;

	private String bodyBlueNodeId;

	private String headBlueNodeId;

	private String bodyRedNodeId;

	private String headRedNodeId;

	private SimpleIntegerProperty bodyBlueNodeBadTimes = new SimpleIntegerProperty(this, "bodyBlueNodeBadTimes", 0);

	private SimpleIntegerProperty headBlueNodeBadTimes = new SimpleIntegerProperty(this, "headBlueNodeBadTimes", 0);

	private SimpleIntegerProperty bodyRedNodeBadTimes = new SimpleIntegerProperty(this, "bodyRedNodeBadTimes", 0);

	private SimpleIntegerProperty headRedNodeBadTimes = new SimpleIntegerProperty(this, "headRedNodeBadTimes", 0);

	private SimpleBooleanProperty bodyBlueNodeInitialized = new SimpleBooleanProperty(this, "bodyBlueNodeInitialized",
			false);

	private SimpleBooleanProperty headBlueNodeInitialized = new SimpleBooleanProperty(this, "headBlueNodeInitialized",
			false);

	private SimpleBooleanProperty bodyRedNodeInitialized = new SimpleBooleanProperty(this, "bodyRedNodeInitialized",
			false);

	private SimpleBooleanProperty headRedNodeInitialized = new SimpleBooleanProperty(this, "headRedNodeInitialized",
			false);

	public NetworkAthletesGroupConfigDto(Integer groupNumber) {
		this.groupNumber = groupNumber;
	}

	public Integer getGroupNumber() {
		return this.groupNumber;
	}

	public Boolean getHeadSensorsEnabled() {
		return this.headSensorsEnabled;
	}

	public void setHeadSensorsEnabled(Boolean headSensorsEnabled) {
		this.headSensorsEnabled = headSensorsEnabled;
	}

	public Boolean getBodySensorsEnabled() {
		return this.bodySensorsEnabled;
	}

	public void setBodySensorsEnabled(Boolean bodySensorsEnabled) {
		this.bodySensorsEnabled = bodySensorsEnabled;
	}

	public String getBodyBlueNodeId() {
		return this.bodyBlueNodeId;
	}

	public void setBodyBlueNodeId(String bodyBlueNodeId) {
		this.bodyBlueNodeId = bodyBlueNodeId;
		validateSensors();
	}

	public String getHeadBlueNodeId() {
		return this.headBlueNodeId;
	}

	public void setHeadBlueNodeId(String headBlueNodeId) {
		this.headBlueNodeId = headBlueNodeId;
		validateSensors();
	}

	public String getBodyRedNodeId() {
		return this.bodyRedNodeId;
	}

	public void setBodyRedNodeId(String bodyRedNodeId) {
		this.bodyRedNodeId = bodyRedNodeId;
		validateSensors();
	}

	public String getHeadRedNodeId() {
		return this.headRedNodeId;
	}

	public void setHeadRedNodeId(String headRedNodeId) {
		this.headRedNodeId = headRedNodeId;
		validateSensors();
	}

	public int getBodyBlueNodeBadTimes() {
		return this.bodyBlueNodeBadTimes.get();
	}

	public SimpleIntegerProperty bodyBlueNodeBadTimesProperty() {
		return this.bodyBlueNodeBadTimes;
	}

	public void setBodyBlueNodeBadTimes(int bodyBlueNodeBadTimes) {
		this.bodyBlueNodeBadTimes.set(bodyBlueNodeBadTimes);
	}

	public int getHeadBlueNodeBadTimes() {
		return this.headBlueNodeBadTimes.get();
	}

	public SimpleIntegerProperty headBlueNodeBadTimesProperty() {
		return this.headBlueNodeBadTimes;
	}

	public void setHeadBlueNodeBadTimes(int headBlueNodeBadTimes) {
		this.headBlueNodeBadTimes.set(headBlueNodeBadTimes);
	}

	public int getBodyRedNodeBadTimes() {
		return this.bodyRedNodeBadTimes.get();
	}

	public SimpleIntegerProperty bodyRedNodeBadTimesProperty() {
		return this.bodyRedNodeBadTimes;
	}

	public void setBodyRedNodeBadTimes(int bodyRedNodeBadTimes) {
		this.bodyRedNodeBadTimes.set(bodyRedNodeBadTimes);
	}

	public int getHeadRedNodeBadTimes() {
		return this.headRedNodeBadTimes.get();
	}

	public SimpleIntegerProperty headRedNodeBadTimesProperty() {
		return this.headRedNodeBadTimes;
	}

	public void setHeadRedNodeBadTimes(int headRedNodeBadTimes) {
		this.headRedNodeBadTimes.set(headRedNodeBadTimes);
	}

	private void validateSensors() {
		setBodySensorsEnabled(Boolean.valueOf((this.bodyBlueNodeId != null && this.bodyRedNodeId != null
				&& !"0".equals(this.bodyBlueNodeId) && !"0".equals(this.bodyRedNodeId))));
		setHeadSensorsEnabled(Boolean.valueOf((this.headBlueNodeId != null && this.headRedNodeId != null
				&& !"0".equals(this.headBlueNodeId) && !"0".equals(this.headRedNodeId))));
	}

	public boolean isBodyBlueNodeInitialized() {
		return this.bodyBlueNodeInitialized.get();
	}

	public SimpleBooleanProperty bodyBlueNodeInitializedProperty() {
		return this.bodyBlueNodeInitialized;
	}

	public void setBodyBlueNodeInitialized(boolean bodyBlueNodeInitialized) {
		this.bodyBlueNodeInitialized.set(bodyBlueNodeInitialized);
	}

	public boolean isHeadBlueNodeInitialized() {
		return this.headBlueNodeInitialized.get();
	}

	public SimpleBooleanProperty headBlueNodeInitializedProperty() {
		return this.headBlueNodeInitialized;
	}

	public void setHeadBlueNodeInitialized(boolean headBlueNodeInitialized) {
		this.headBlueNodeInitialized.set(headBlueNodeInitialized);
	}

	public boolean isBodyRedNodeInitialized() {
		return this.bodyRedNodeInitialized.get();
	}

	public SimpleBooleanProperty bodyRedNodeInitializedProperty() {
		return this.bodyRedNodeInitialized;
	}

	public void setBodyRedNodeInitialized(boolean bodyRedNodeInitialized) {
		this.bodyRedNodeInitialized.set(bodyRedNodeInitialized);
	}

	public boolean isHeadRedNodeInitialized() {
		return this.headRedNodeInitialized.get();
	}

	public SimpleBooleanProperty headRedNodeInitializedProperty() {
		return this.headRedNodeInitialized;
	}

	public void setHeadRedNodeInitialized(boolean headRedNodeInitialized) {
		this.headRedNodeInitialized.set(headRedNodeInitialized);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof NetworkAthletesGroupConfigDto))
			return false;
		NetworkAthletesGroupConfigDto that = (NetworkAthletesGroupConfigDto) o;
		return this.groupNumber.equals(that.groupNumber);
	}

	@Override
	public int hashCode() {
		return this.groupNumber.hashCode();
	}
}
