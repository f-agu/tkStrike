package com.xtremis.daedo.tkstrike.tools;

import java.lang.reflect.Field;

import org.springframework.context.ApplicationContext;

import com.xtremis.daedo.tkstrike.ui.controller.CommonTkStrikeBaseController;

/**
 * @author f.agu
 */
public class UpdateGen {

	private UpdateGen() {
	}

	public static void updateTkStrikeGenVersion(ApplicationContext applicationContext, String tkStrikeGenVersion)
			throws Exception {
		Field field = CommonTkStrikeBaseController.class.getDeclaredField("tkStrikeGenVersion");
		field.setAccessible(true);
		applicationContext.getBeansOfType(CommonTkStrikeBaseController.class).values().forEach(cc -> {
			try {
				field.set(cc, tkStrikeGenVersion);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
