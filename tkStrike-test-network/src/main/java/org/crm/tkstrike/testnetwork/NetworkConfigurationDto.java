package org.crm.tkstrike.testnetwork;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class NetworkConfigurationDto implements Serializable {
	private static final long serialVersionUID = 5555158762440625924L;

	private Boolean networkWasStarted;

	private Integer channelNumber;

	private Integer judgesNumber;

	private String judge1NodeId;

	private String judge2NodeId;

	private String judge3NodeId;

	private Boolean headTechPointEnabled;

	private final Integer maxGroupsNumber;

	private Integer groupsNumber = Integer.valueOf(1);

	private Map<Integer, NetworkAthletesGroupConfigDto> groupsConfig;

	private Boolean group2Enabled;

	public NetworkConfigurationDto(Integer maxGroupsNumber) {
		this.maxGroupsNumber = maxGroupsNumber;
		this.groupsConfig = new HashMap<>(maxGroupsNumber.intValue());
		for (int i = 1; i <= maxGroupsNumber.intValue(); i++)
			this.groupsConfig.put(Integer.valueOf(i), new NetworkAthletesGroupConfigDto(Integer.valueOf(i)));
	}

	public Boolean getNetworkWasStarted() {
		return this.networkWasStarted;
	}

	public void setNetworkWasStarted(Boolean networkWasStarted) {
		this.networkWasStarted = networkWasStarted;
	}

	public Integer getChannelNumber() {
		return this.channelNumber;
	}

	public void setChannelNumber(Integer channelNumber) {
		this.channelNumber = channelNumber;
	}

	public Integer getJudgesNumber() {
		return this.judgesNumber;
	}

	public void setJudgesNumber(Integer judgesNumber) {
		this.judgesNumber = judgesNumber;
	}

	public String getJudge1NodeId() {
		return this.judge1NodeId;
	}

	public void setJudge1NodeId(String judge1NodeId) {
		this.judge1NodeId = judge1NodeId;
	}

	public String getJudge2NodeId() {
		return this.judge2NodeId;
	}

	public void setJudge2NodeId(String judge2NodeId) {
		this.judge2NodeId = judge2NodeId;
	}

	public String getJudge3NodeId() {
		return this.judge3NodeId;
	}

	public void setJudge3NodeId(String judge3NodeId) {
		this.judge3NodeId = judge3NodeId;
	}

	public Boolean getHeadTechPointEnabled() {
		return this.headTechPointEnabled;
	}

	public Integer getGroupsNumber() {
		return this.groupsNumber;
	}

	public void setGroupsNumber(Integer groupsNumber) {
		this.groupsNumber = groupsNumber;
	}

	public void setHeadTechPointEnabled(Boolean headTechPointEnabled) {
		this.headTechPointEnabled = headTechPointEnabled;
	}

	public NetworkAthletesGroupConfigDto getGroup1Config() {
		return this.groupsConfig.get(Integer.valueOf(1));
	}

//	public void setNetworkAthletesGroupConfig(NetworkAthletesGroupConfigDto groupConfig) {
//		if (groupConfig != null)
//			_setNetworkAthletesGroupConfig(groupConfig.getGroupNumber(), groupConfig);
//	}

	public NetworkAthletesGroupConfigDto getNetworkAthletesGroupConfig(Integer groupNumber) {
		return groupNumber != null && groupNumber.intValue() > 0 && groupNumber.intValue() <= maxGroupsNumber.intValue()
				? groupsConfig.get(groupNumber)
				: null;
	}

//  private void _setNetworkAthletesGroupConfig(Integer forceGroupNumber, NetworkAthletesGroupConfigDto groupConfig) {
//    if (this.groupsConfig != null && this.groupsConfig != null && forceGroupNumber != null && forceGroupNumber.intValue() <= this.maxGroupsNumber.intValue()) {
//      NetworkAthletesGroupConfigDto current = this.groupsConfig.get(forceGroupNumber);
//      if (current != null)
//        BeanUtils.copyProperties(groupConfig, current, new String[] { "groupNumber" }); 
//    } 
//  }

	public void setGroup1Config(NetworkAthletesGroupConfigDto group1Config) {
		// _setNetworkAthletesGroupConfig(Integer.valueOf(1), group1Config);
	}

	public Boolean getGroup2Enabled() {
		return Boolean.valueOf((this.groupsNumber != null && this.groupsNumber.intValue() >= 2));
	}

	public void setGroup2Enabled(Boolean group2Enabled) {
		this.group2Enabled = group2Enabled;
	}

	public NetworkAthletesGroupConfigDto getGroup2Config() {
		return this.groupsConfig.get(Integer.valueOf(2));
	}

//	public void setGroup2Config(NetworkAthletesGroupConfigDto group2Config) {
//		_setNetworkAthletesGroupConfig(Integer.valueOf(2), group2Config);
//	}

	public void calculateNumberOfJudges() {
		int numOfJudges = 0;
		if (this.judge1NodeId != null && !"0".equals(this.judge1NodeId))
			numOfJudges++;
		if (this.judge2NodeId != null && !"0".equals(this.judge2NodeId))
			numOfJudges++;
		if (this.judge3NodeId != null && !"0".equals(this.judge3NodeId))
			numOfJudges++;
		setJudgesNumber(Integer.valueOf(numOfJudges));
	}

	public void calculateNumberOfGroups() {
		int lastGroups = 0;
		for (int i = 1; i <= this.maxGroupsNumber.intValue(); i++) {
			if (isAthleteGroupConfigAllInitialized(this.groupsConfig.get(Integer.valueOf(i))))
				lastGroups++;
		}
		setGroupsNumber(Integer.valueOf(lastGroups));
	}

	public void cleanAllNodes() {
		this.judge1NodeId = null;
		this.judge2NodeId = null;
		this.judge3NodeId = null;
		for (NetworkAthletesGroupConfigDto networkAthletesGroupConfigDto : this.groupsConfig.values()) {
			networkAthletesGroupConfigDto.setBodyBlueNodeId(null);
			// networkAthletesGroupConfigDto.setBodyBlueNodeBadTimes(0);
			networkAthletesGroupConfigDto.setHeadBlueNodeId(null);
			// networkAthletesGroupConfigDto.setHeadBlueNodeBadTimes(0);
			networkAthletesGroupConfigDto.setBodyRedNodeId(null);
			// networkAthletesGroupConfigDto.setBodyRedNodeBadTimes(0);
			networkAthletesGroupConfigDto.setHeadRedNodeId(null);
			// networkAthletesGroupConfigDto.setHeadRedNodeBadTimes(0);
		}
	}

	public Boolean areAllNodesInitialized() {
		if (StringUtils.isBlank(this.judge1NodeId))
			return Boolean.FALSE;
		if (StringUtils.isBlank(this.judge2NodeId))
			return Boolean.FALSE;
		if (StringUtils.isBlank(this.judge3NodeId))
			return Boolean.FALSE;
		int groupsOk = 0;
		for (NetworkAthletesGroupConfigDto networkAthletesGroupConfigDto : this.groupsConfig.values()) {
			boolean groupOk = Boolean.TRUE.booleanValue();
			if (StringUtils.isBlank(networkAthletesGroupConfigDto.getBodyBlueNodeId()))
				groupOk = Boolean.FALSE.booleanValue();
			if (StringUtils.isBlank(networkAthletesGroupConfigDto.getHeadBlueNodeId()))
				groupOk = Boolean.FALSE.booleanValue();
			if (StringUtils.isBlank(networkAthletesGroupConfigDto.getBodyRedNodeId()))
				groupOk = Boolean.FALSE.booleanValue();
			if (StringUtils.isBlank(networkAthletesGroupConfigDto.getHeadRedNodeId()))
				groupOk = Boolean.FALSE.booleanValue();
			if (!groupOk && groupsOk >= 1)
				return Boolean.TRUE;
			if (groupOk)
				groupsOk++;
		}
		return Boolean.TRUE;
	}

	private boolean isAthleteGroupConfigAllInitialized(NetworkAthletesGroupConfigDto networkAthletesGroupConfigDto) {
		return (networkAthletesGroupConfigDto != null && ((networkAthletesGroupConfigDto

				.getBodyBlueNodeId() != null && !"0".equals(networkAthletesGroupConfigDto.getBodyBlueNodeId())
				&& networkAthletesGroupConfigDto.getBodyRedNodeId() != null
				&& !"0".equals(networkAthletesGroupConfigDto.getBodyRedNodeId()))
				|| (networkAthletesGroupConfigDto

						.getHeadBlueNodeId() != null && !"0".equals(networkAthletesGroupConfigDto.getHeadBlueNodeId())
						&& networkAthletesGroupConfigDto.getHeadRedNodeId() != null
						&& !"0".equals(networkAthletesGroupConfigDto.getHeadRedNodeId()))));
	}
}
