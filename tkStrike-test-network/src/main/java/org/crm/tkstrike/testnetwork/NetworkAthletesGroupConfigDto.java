package org.crm.tkstrike.testnetwork;

import java.io.Serializable;

public class NetworkAthletesGroupConfigDto implements Serializable {
	private static final long serialVersionUID = -7833590997560335364L;

	private final Integer groupNumber;

	private Boolean headSensorsEnabled;

	private Boolean bodySensorsEnabled;

	private String bodyBlueNodeId;

	private String headBlueNodeId;

	private String bodyRedNodeId;

	private String headRedNodeId;

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

	private void validateSensors() {
		setBodySensorsEnabled(Boolean.valueOf((this.bodyBlueNodeId != null && this.bodyRedNodeId != null
				&& !"0".equals(this.bodyBlueNodeId) && !"0".equals(this.bodyRedNodeId))));
		setHeadSensorsEnabled(Boolean.valueOf((this.headBlueNodeId != null && this.headRedNodeId != null
				&& !"0".equals(this.headBlueNodeId) && !"0".equals(this.headRedNodeId))));
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
