package com.xtremis.daedo.tkstrike.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.xtremis.daedo.tkstrike.communication.TkStrikeCommunicationService;
import com.xtremis.daedo.tkstrike.communication.TkStrikeSimulatorCommunicationServiceImpl;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeController;
import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;

import javafx.fxml.FXMLLoader;


public abstract class BaseTkStrikeSpringConfiguration {

	@Autowired
	@Qualifier("tkStrikeLanguage")
	private String tkStrikeLanguage;

	@Bean
	@Qualifier("tkStrikeCommunicationType")
	public TkStrikeCommunicationTypeValue getTkStrikeCommunicationType() {
		return TkStrikeCommunicationTypeUtil.getInstance().getTkStrikeCommunicationType();
	}

	@Bean
	@Scope("singleton")
	@Conditional({SimulatorCommunicationConditional.class})
	public TkStrikeCommunicationService getTkStrikeSimulatorCommunicationServiceImpl() {
		return new TkStrikeSimulatorCommunicationServiceImpl();
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
		TkStrikeBaseDirectoriesUtil tkStrikeBaseDirectoriesUtil = TkStrikeBaseDirectoriesUtil.getInstance();
		File fCommunicationType = new File(tkStrikeBaseDirectoriesUtil.getWorkBaseDir() + "tkStrike-ext.properties");
		Resource[] resources = new Resource[0];
		resources = (Resource[])ArrayUtils.add((Object[])resources, new ClassPathResource("/META-INF/app.properties"));
		if(fCommunicationType.exists())
			try {
				resources = (Resource[])ArrayUtils.add((Object[])resources, new UrlResource(fCommunicationType.toURI()));
			} catch(MalformedURLException e) {
				e.printStackTrace();
			}
		ppc.setLocations(resources);
		ppc.setIgnoreUnresolvablePlaceholders(true);
		return ppc;
	}

	protected Object loadController(String url) throws IOException {
		InputStream fxmlStream = null;
		try {
			fxmlStream = getClass().getResourceAsStream(url);
			FXMLLoader loader = new FXMLLoader();
			loader.setCharset(Charset.forName("UTF-8"));
			loader.setResources(ResourceBundle.getBundle("language.TkStrikeMain", Locale.forLanguageTag(this.tkStrikeLanguage)));
			loader.load(fxmlStream);
			return loader.getController();
		} finally {
			if(fxmlStream != null)
				fxmlStream.close();
		}
	}

	protected TkStrikeController loadController(TkStrikeController controller, String url) throws IOException {
		InputStream fxmlStream = null;
		try {
			fxmlStream = getClass().getResourceAsStream(url);
			FXMLLoader loader = new FXMLLoader();
			loader.setCharset(Charset.forName("UTF-8"));
			loader.setResources(ResourceBundle.getBundle("language.TkStrikeMain", Locale.forLanguageTag(this.tkStrikeLanguage)));
			loader.setController(controller);
			loader.load(fxmlStream);
			return (TkStrikeController)loader.getController();
		} finally {
			if(fxmlStream != null)
				fxmlStream.close();
		}
	}
}
