package com.xtremis.daedo.tkstrike.ui;

import java.io.IOException;

import com.xtremis.daedo.tkstrike.configuration.PatchedConfiguration;
import com.xtremis.daedo.tkstrike.tools.TkExtProperties;

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
		try {
			switch (TkExtProperties.getInstance().getGenVersion()) {
			case GEN1:
				return "/images/TkStrikeSplash-patched-gen1.jpg";
			case GEN2:
				return "/images/TkStrikeSplash-patched-gen2.jpg";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/images/TkStrikeSplash-patched.jpg";
	}

	public static void main(String... args) {
		launch(args);
	}

}
