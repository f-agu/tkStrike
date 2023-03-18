package com.xtremis.daedo.tkstrike.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xtremis.daedo.tkstrike.communication.LogNetworkListener;

/**
 * @author f.agu
 * @created 15 mars 2023 10:55:24
 */
@Configuration
public class MoreConfiguration {

	@Bean
	LogNetworkListener logNetworkListener() {
		return new LogNetworkListener();
	}

}
