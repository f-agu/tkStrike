package com.xtremis.daedo.tkstrike.ui;

import com.xtremis.daedo.tkstrike.configuration.PatchedConfiguration;


/**
 * @author f.agu
 * @created 15 mars 2023 09:54:52
 */
public class PatchedTkStrikeAppMain extends TkStrikeAppMain {

	@Override
	Class getTkStrikeSpringConfigurationClass() {
		return PatchedConfiguration.class;
	}

	@Override
	String getTkStrikeSplashUrl() {
		return "/images/TkStrikeSplash-patched.jpg";
	}

	public static void main(String... args) {
		launch(args);
	}

}
