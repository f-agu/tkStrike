package org.crm.tkstrike.testnetwork;

import java.util.concurrent.TimeUnit;

import org.crm.tkstrike.testnetwork.NodeIds.Color;
import org.crm.tkstrike.testnetwork.NodeIds.Part;

/**
 * @author f.agu
 */
public class Application {

	public static void main(String... args) throws Exception {
		NodeIds nodeIds = new NodeIds();

		NetworkConfigurationDto networkConfigurationDto = new NetworkConfigurationDto(1);
		networkConfigurationDto.setJudgesNumber(1);
		networkConfigurationDto.setJudge1NodeId(nodeIds.getJudge(1));
		networkConfigurationDto.setJudge2NodeId(nodeIds.getJudge(2));
		networkConfigurationDto.setJudge3NodeId(nodeIds.getJudge(3));

		NetworkAthletesGroupConfigDto group1Config = networkConfigurationDto.getGroup1Config();
		group1Config.setBodySensorsEnabled(true);
		group1Config.setBodyBlueNodeId(nodeIds.getSensorId(Color.BLUE, 1, Part.BODY));
		group1Config.setBodyRedNodeId(nodeIds.getSensorId(Color.RED, 1, Part.BODY));
		group1Config.setHeadSensorsEnabled(true);
		group1Config.setHeadBlueNodeId(nodeIds.getSensorId(Color.BLUE, 1, Part.HEAD));
		group1Config.setHeadRedNodeId(nodeIds.getSensorId(Color.RED, 1, Part.HEAD));

		try (TkStrikeCommunication tkStrikeCommunication = new TkStrikeCommunication()) {
			tkStrikeCommunication.startNetwork(networkConfigurationDto);
			TimeUnit.SECONDS.sleep(1000000L);
		}
	}
}
