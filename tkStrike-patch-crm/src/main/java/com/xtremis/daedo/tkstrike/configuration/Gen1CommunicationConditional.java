package com.xtremis.daedo.tkstrike.configuration;

import java.io.IOException;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.xtremis.daedo.tkstrike.tools.Generation;
import com.xtremis.daedo.tkstrike.tools.TkExtProperties;

public class Gen1CommunicationConditional implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		try {
			return TkExtProperties.getInstance().getGenVersion().equals(Generation.GEN1);
		} catch (IOException e) {
			return false;
		}
	}
}
