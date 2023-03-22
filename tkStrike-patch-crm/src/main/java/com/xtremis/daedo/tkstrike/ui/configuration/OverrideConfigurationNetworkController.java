package com.xtremis.daedo.tkstrike.ui.configuration;

import java.util.concurrent.Callable;

import com.xtremis.daedo.tkstrike.communication.NetworkConfigurationDto;
import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationException;
import com.xtremis.daedo.tkstrike.service.TkStrikeServiceException;
import com.xtremis.daedo.tkstrike.tools.utils.TkStrikeExecutors;
import com.xtremis.daedo.tkstrike.ui.controller.configuration.ConfigurationNetworkController;

/**
 * @author Oodrive
 * @author f.agu
 * @created 22 mars 2023 11:44:04
 */
public class OverrideConfigurationNetworkController extends ConfigurationNetworkController {

	@Override
	public void doStartNetwork() {
		if (isFormValid()) {
			showProgressIndicator(true);
			TkStrikeExecutors.executeInThreadPool(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					NetworkConfigurationDto ncDto = currentNetworkConfiguration.getNetworkConfigurationDto();
					try {
						try {
							networkConfigurationService.update(currentNetworkConfiguration.getNetworkConfiguration());
						} catch (TkStrikeServiceException e) {
							logger.error(e.getMessage(), e);
						}
						tkStrikeCommunicationService.startNetwork(ncDto);
						getAppStatusWorker().setNetworkConfigurationEntry(currentNetworkConfiguration);
						currentNetworkConfiguration.networkWasStartedProperty.set(true);
						showProgressIndicator(false);
					} catch (TkStrikeCommunicationException e) {
						logger.error(e.getMessage(), e);
						showErrorDialog(getMessage("title.default.error"), getMessage("message.error.serialComm"));
					} finally {
						showProgressIndicator(false);
					}
					return null;
				}
			});
		}
	}

}
