package com.xtremis.daedo.tkstrike.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;

/**
 * @author f.agu
 */
public final class TkExtProperties {

	private static final String FILENAME = "tkStrike-ext.properties";

	public static final String GEN_VERSION = "tkStrike.genVersion";
	public static final String STYLE = "tkStrike.style";

	private static final TkExtProperties INSTANCE = new TkExtProperties();

	public static TkExtProperties getInstance() {
		return INSTANCE;
	}

	public String get(String key) throws IOException {
		TkStrikeBaseDirectoriesUtil tkStrikeBaseDirectoriesUtil = TkStrikeBaseDirectoriesUtil.getInstance();
		File targetFile = new File(tkStrikeBaseDirectoriesUtil.getWorkBaseDir() + FILENAME);
		if (!targetFile.exists()) {
			return null;
		}
		return load(targetFile).getProperty(key);
	}

	public void set(String key, String value) throws IOException {
		TkStrikeBaseDirectoriesUtil tkStrikeBaseDirectoriesUtil = TkStrikeBaseDirectoriesUtil.getInstance();
		File targetFile = new File(tkStrikeBaseDirectoriesUtil.getWorkBaseDir() + FILENAME);
		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}
		Properties properties = load(targetFile);
		properties.setProperty(key, value);
		try (OutputStream fis = new FileOutputStream(targetFile)) {
			properties.store(fis, (String) null);
		}
	}

	public Generation getGenVersion() throws IOException {
		return Optional.ofNullable(get(GEN_VERSION)).map(s -> Generation.valueOf(s.toUpperCase()))
				.orElse(Generation.GEN2);
	}

	public void setGenVersion(Generation generation) throws IOException {
		set(GEN_VERSION, generation.name().toLowerCase());
	}

	public Discipline getStyle() throws IOException {
		return Optional.ofNullable(get(STYLE)).map(s -> Discipline.valueOf(s.toUpperCase())).orElse(Discipline.WT);
	}

	public void setStyle(Discipline style) throws IOException {
		set(STYLE, style.name().toLowerCase());
	}

	// ********************************************************************

	private Properties load(File targetFile) {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(targetFile)) {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return properties;
	}
}
