package com.xtremis.daedo.tkstrike.communication;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.xtremis.daedo.tkstrike.om.NetworkErrorCause;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;


public abstract class BaseCommonGlobalNetworkStatusControllerImpl implements CommonGlobalNetworkStatusController, TkStrikeCommunicationListener {

	static final Logger gnscLogger = Logger.getLogger("GLOBAL_NETWORK_STATUS_CONTROLLER");

	private CopyOnWriteArrayList<GlobalNetworkStatusControllerListener> listeners = new CopyOnWriteArrayList<>();

	private final TkStrikeCommunicationService tkStrikeCommunicationService;

	NetworkConfigurationDto networkConfigurationDto;

	private long VALIDAT_NETWORK_EVENT_CYCLE = 1000L;

	@Value("${tkStrike.timeAllowed4NodeNextStatus}")
	private Long timeAllowedNextStatus;

	@Value("${tkStrike.validateNetworkWithNTimesAllowedOfflineNode}")
	private Boolean validateNetworkWithNTimesAllowedOfflineNode;

	@Value("${tkStrike.maxNTimesAllowedOfflineNode}")
	private Integer maxNTimesAllowedOfflineNode;

	ScheduledExecutorService schedulerValidateNetwork = null;

	private ScheduledFuture<?> scheduleValidateNetworkAtFixedRate = null;

	int nodesInNetwork = 0;

	int numberOfJudges = 0;

	private JudgeNode judge1Node = null;

	private JudgeNode judge2Node = null;

	private JudgeNode judge3Node = null;

	@Autowired
	public BaseCommonGlobalNetworkStatusControllerImpl(TkStrikeCommunicationService tkStrikeCommunicationService) {
		this.tkStrikeCommunicationService = tkStrikeCommunicationService;
	}

	final void initializeScheduleValidateNetwork() {
		this.schedulerValidateNetwork = Executors.newSingleThreadScheduledExecutor();
		this.scheduleValidateNetworkAtFixedRate = this.schedulerValidateNetwork.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				long current = System.currentTimeMillis();
				boolean allOk = true;
				BaseCommonGlobalNetworkStatusControllerImpl.this.validateNode(BaseCommonGlobalNetworkStatusControllerImpl.this.judge1Node, current);
				BaseCommonGlobalNetworkStatusControllerImpl.this.validateNode(BaseCommonGlobalNetworkStatusControllerImpl.this.judge2Node, current);
				BaseCommonGlobalNetworkStatusControllerImpl.this.validateNode(BaseCommonGlobalNetworkStatusControllerImpl.this.judge3Node, current);
				if( ! BaseCommonGlobalNetworkStatusControllerImpl.this._validateAthletesNodes(current))
					allOk = false;
				if(allOk)
					BaseCommonGlobalNetworkStatusControllerImpl.this.fireNetworkOkEvent(new GlobalNetworkStatusControllerListener.NetworkOkEvent(Long
							.valueOf(current)));
			}
		}, 0L, this.VALIDAT_NETWORK_EVENT_CYCLE, TimeUnit.MILLISECONDS);
	}

	final boolean validateNode(BaseNode node, long currentTime) {
		boolean res = true;
		if(node != null) {
			NetworkErrorCause.NetworkErrorCauseType networkErrorCause = null;
			if(this.validateNetworkWithNTimesAllowedOfflineNode.booleanValue()) {
				if(node.getNodeOfflineTimes() >= this.maxNTimesAllowedOfflineNode.intValue()) {
					if(gnscLogger.isDebugEnabled())
						gnscLogger.debug("**** Node " + node.getNodeId() + " times Offline " + node.getNodeOfflineTimes() + " is >= "
								+ this.maxNTimesAllowedOfflineNode);
					res = false;
					node.setNodeStatusOk(Boolean.FALSE);
					networkErrorCause = NetworkErrorCause.NetworkErrorCauseType.LOST_CONNECTION;
				}
			} else if(currentTime - node.getLastTimestampStatusOk() >= this.timeAllowedNextStatus.longValue() * ((this.nodesInNetwork > 5)
					? this.nodesInNetwork
					: 5L)) {
				if(gnscLogger.isDebugEnabled())
					gnscLogger.debug("**** Node " + node.getNodeId() + " timeValidation " + (currentTime - node.getLastTimestampStatusOk())
							+ " is >= " + (this.timeAllowedNextStatus.longValue() * ((this.nodesInNetwork > 5) ? this.nodesInNetwork : 5L)));
				res = false;
				node.setNodeStatusOk(Boolean.FALSE);
				networkErrorCause = NetworkErrorCause.NetworkErrorCauseType.LOST_CONNECTION;
			}
			if(res && ! (node instanceof JudgeNode)) {
				res = node.getNodeStatusOk().booleanValue();
				if( ! res)
					networkErrorCause = NetworkErrorCause.NetworkErrorCauseType.SENSOR_ERROR;
			}
			if( ! res) {
				if(gnscLogger.isDebugEnabled())
					gnscLogger.debug("Error node " + node.getNodeId() + " cause " + networkErrorCause);
				fireNewNodeNetworkErrorEvent(new GlobalNetworkStatusControllerListener.NodeNetworkErrorEvent(Long.valueOf(currentTime), Long.valueOf(
						node.getLastTimestampStatusOk()), node, networkErrorCause));
			}
		}
		return res;
	}

	final void updateNetworkConfiguration(NetworkConfigurationDto networkConfigurationDto) {
		if(gnscLogger.isDebugEnabled())
			gnscLogger.debug("updateNetworkConfiguration() ");
		this.nodesInNetwork = 0;
		if(networkConfigurationDto != null) {
			this.numberOfJudges = 0;
			if(networkConfigurationDto.getJudgesNumber().intValue() >= 1) {
				this.judge1Node = new JudgeNode(networkConfigurationDto.getJudge1NodeId(), 1);
				this.nodesInNetwork++;
				this.numberOfJudges++;
			} else {
				this.judge1Node = null;
			}
			if(networkConfigurationDto.getJudgesNumber().intValue() >= 2) {
				this.judge2Node = new JudgeNode(networkConfigurationDto.getJudge2NodeId(), 2);
				this.nodesInNetwork++;
				this.numberOfJudges++;
			} else {
				this.judge2Node = null;
			}
			if(networkConfigurationDto.getJudgesNumber().intValue() >= 3) {
				this.judge3Node = new JudgeNode(networkConfigurationDto.getJudge3NodeId(), 3);
				this.nodesInNetwork++;
				this.numberOfJudges++;
			} else {
				this.judge3Node = null;
			}
			_initializeAthletesNodes(networkConfigurationDto);
		}
	}

	private void fireNewStatusEvent(StatusEvent newStatusEvent) {
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.listeners.size());
		this.listeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.hasNewGlobalStatusEvent(newStatusEvent);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void fireNewNodeNetworkErrorEvent(GlobalNetworkStatusControllerListener.NodeNetworkErrorEvent nodeNetworkErrorEvent) {
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.listeners.size());
		this.listeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.hasNewNodeNetworkErrorEvent(nodeNetworkErrorEvent);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void fireNetworkOkEvent(GlobalNetworkStatusControllerListener.NetworkOkEvent networkOkEvent) {
		ArrayList<Callable<Void>> tasks = new ArrayList<>(this.listeners.size());
		this.listeners.forEach(lis -> tasks.add(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				lis.hasNetworkOkEvent(networkOkEvent);
				return null;
			}
		}));
		try {
			TkStrikeExecutors.executeInParallel(tasks);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public final void hasNewDataEvent(DataEvent dataEvent) {}

	@Override
	public final void hasNewStatusEvent(StatusEvent statusEvent) {
		if(statusEvent != null && statusEvent.getNodeId() != null) {
			boolean sensorOk = statusEvent.getSensorOk().booleanValue();
			double nodeBatteryPct = statusEvent.getNodeBatteryPct().doubleValue();
			boolean connOffline = statusEvent.getConnOffline().booleanValue();
			long lastTimeValidateTime = this.timeAllowedNextStatus.longValue() * ((this.nodesInNetwork > 5) ? this.nodesInNetwork : 5L);
			JudgeNode judgeNode = lookup4NodeInJudges(statusEvent.getNodeId());
			if(judgeNode != null) {
				if( ! connOffline) {
					judgeNode.setNodeOfflineTimes(0);
					judgeNode.setLastTimestampStatusOk(System.currentTimeMillis());
				} else {
					judgeNode.setNodeOfflineTimes(judgeNode.getNodeOfflineTimes() + 1);
					judgeNode.setLastTimestampStatusOk(System.currentTimeMillis() - lastTimeValidateTime);
				}
				judgeNode.setBatteryPct(nodeBatteryPct);
				judgeNode.setNodeStatusOk(Boolean.valueOf(sensorOk));
			} else {
				BaseNode node = _lookup4NodeInAthletes(statusEvent.getNodeId());
				if(node != null) {
					if( ! statusEvent.getConnOffline().booleanValue()) {
						node.setNodeOfflineTimes(0);
						node.setLastTimestampStatusOk(System.currentTimeMillis());
					} else {
						node.setNodeOfflineTimes(node.getNodeOfflineTimes() + 1);
						node.setLastTimestampStatusOk(System.currentTimeMillis() - lastTimeValidateTime);
					}
					node.setBatteryPct(statusEvent.getNodeBatteryPct().doubleValue());
					node.setNodeStatusOk(statusEvent.getSensorOk());
				}
			}
		}
		fireNewStatusEvent(statusEvent);
	}

	private JudgeNode lookup4NodeInJudges(String nodeId) {
		if(this.judge1Node != null && nodeId.equals(this.judge1Node.getNodeId()))
			return this.judge1Node;
		if(this.judge2Node != null && nodeId.equals(this.judge2Node.getNodeId()))
			return this.judge2Node;
		if(this.judge3Node != null && nodeId.equals(this.judge3Node.getNodeId()))
			return this.judge3Node;
		return null;
	}

	@Override
	public final void hasChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {}

	@Override
	public final void hasChangeNetworkConfigurationEvent(ChangeNetworkConfigurationEvent changeNetworkConfigurationEvent) {
		if(changeNetworkConfigurationEvent != null && changeNetworkConfigurationEvent.getNewNetworkConfigurationDto() != null) {
			this.networkConfigurationDto = changeNetworkConfigurationEvent.getNewNetworkConfigurationDto();
			updateNetworkConfiguration(changeNetworkConfigurationEvent.getNewNetworkConfigurationDto());
			if(this.schedulerValidateNetwork != null)
				this.schedulerValidateNetwork.shutdownNow();
			initializeScheduleValidateNetwork();
		}
	}

	@Override
	public final void addListener(GlobalNetworkStatusControllerListener listener) {
		if( ! this.listeners.contains(listener))
			this.listeners.add(listener);
	}

	@Override
	public final void removeListener(GlobalNetworkStatusControllerListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public NetworkStatus getCurrentNetworkStatus() throws TkStrikeCommunicationException {
		return this.tkStrikeCommunicationService.getCurrentNetworkStatus();
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		this.tkStrikeCommunicationService.addListener(this);
		_afterPropertiesSet();
	}

	abstract boolean _validateAthletesNodes(long paramLong);

	abstract void _initializeAthletesNodes(NetworkConfigurationDto paramNetworkConfigurationDto);

	abstract BaseNode _lookup4NodeInAthletes(String paramString);

	abstract void _afterPropertiesSet() throws Exception;
}
