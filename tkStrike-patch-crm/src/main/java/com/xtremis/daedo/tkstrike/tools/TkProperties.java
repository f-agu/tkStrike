package com.xtremis.daedo.tkstrike.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.xtremis.daedo.tkstrike.tools.NodeIds.Color;
import com.xtremis.daedo.tkstrike.tools.NodeIds.Part;
import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;

/**
 * @author f.agu
 */
public class TkProperties {

	private static final Logger _log = Logger.getLogger(TkProperties.class);

	private static final String FILENAME = "crm.properties";
	private static final String GAMJOM = "defaultMaxGamjomAllowed";
	private static final String DIFFERENTIAL_SCORE = "defaultDifferentialScore";
	private static final String KEY_GENERATION = "generation";
	private static final String LOGGING_LEVEL_PREFIX = "logging.level.";
	private static final String KEY_NODEIDS_PREFIX = "nodeids";
	private static final Generation DEFAULT = Generation.GEN2;

	private static final TkProperties INSTANCE = new TkProperties();

	private final File file;

	private final Properties properties;

	private TkProperties() {
		file = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + FILENAME);
		this.properties = loadTkStrikeWorkFile(file);
	}

	public static TkProperties getInstance() {
		return INSTANCE;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getString(String key, String defaultValue) {
		return getAndWriteProperty(key, defaultValue, new StringConverter()).writeIfNotExist();
	}

	public void setString(String key, String value) {
		properties.setProperty(key, value);
		write();
	}

	public int getInt(String key, int defaultValue) {
		return getAndWriteProperty(key, defaultValue, new IntegerConverter()).writeIfNotExist();
	}

	public void setInt(String key, int value) {
		properties.setProperty(key, Integer.toString(value));
		write();
	}

	public int getGamJom() {
		return getInt(GAMJOM, 5);
	}

	public int getDifferentialScore() {
		return getInt(DIFFERENTIAL_SCORE, 12);
	}

	public Level getLogLevel(String key, Level defaultLevel) {
		return getAndWriteProperty(LOGGING_LEVEL_PREFIX + key, defaultLevel, new LevelConverter()).writeIfNotExist();
	}

//	public Generation getGeneration() {
//		return Generation.valueOf(getString(KEY_GENERATION, DEFAULT.name()).toUpperCase());
//	}
//
//	public void setGeneration(Generation generation) throws IOException {
//		if (generation == null) {
//			return;
//		}
//		setString(KEY_GENERATION, generation.name());
//	}

	public NodeIds getNodeIds() throws IOException {
		boolean updated = false;
		NodeIds nodeIds = new NodeIds();
		// judge
		for (int j = 1; j <= 3; ++j) {
			StringJoiner joiner = new StringJoiner(".");
			joiner.add(KEY_NODEIDS_PREFIX);
			joiner.add("judge" + j);
			final int jj = j;
			updated |= !getAndWriteProperty(joiner.toString(), nodeIds.getJudge(j), new StringConverter())
					.ifExists(id -> nodeIds.setJudge(jj, id)).exists;
		}
		// sensors
		for (Color color : Color.values()) {
			for (int group = 1; group <= 2; ++group) {
				for (Part part : Part.values()) {
					StringJoiner joiner = new StringJoiner(".");
					joiner.add(KEY_NODEIDS_PREFIX);
					joiner.add(color.name().toLowerCase());
					joiner.add(Integer.toString(group));
					joiner.add(part.name().toLowerCase());
					final int g = group;
					updated |= !getAndWriteProperty(joiner.toString(), nodeIds.getSensorId(color, group, part),
							new StringConverter()).ifExists(id -> nodeIds.setSensorId(color, g, part, id)).exists;
				}
			}
		}

		if (updated) {
			write();
		}
		return nodeIds;
	}

	public void setNodeIds(NodeIds nodeIds) throws IOException {
		if (nodeIds == null) {
			return;
		}
		// judges
		for (Entry<Integer, String> entry : nodeIds.getJudges().entrySet()) {
			StringJoiner joiner = new StringJoiner(".");
			joiner.add(KEY_NODEIDS_PREFIX);
			joiner.add("judge" + entry.getKey());
			properties.setProperty(joiner.toString(), entry.getValue());
		}

		// sensors
		Map<Color, Map<Integer, Map<Part, String>>> sensors = nodeIds.getSensors();
		for (Entry<Color, Map<Integer, Map<Part, String>>> sensorEntry : sensors.entrySet()) {
			for (Entry<Integer, Map<Part, String>> groupEntry : sensorEntry.getValue().entrySet()) {
				for (Entry<Part, String> partEntry : groupEntry.getValue().entrySet()) {
					StringJoiner joiner = new StringJoiner(".");
					joiner.add(KEY_NODEIDS_PREFIX);
					joiner.add(sensorEntry.getKey().name().toLowerCase());
					joiner.add(groupEntry.getKey().toString());
					joiner.add(partEntry.getKey().name().toLowerCase());
					properties.setProperty(joiner.toString(), partEntry.getValue());
				}
			}
		}
		write();
	}

	public void write() {
		try (OutputStream outputStream = new FileOutputStream(file)) {
			properties.store(outputStream, (String) null);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	// **************************************

	private <T> ValueAndWriter<T> getAndWriteProperty(String key, T defaultValue, Converter<T> converter) {
		String property = properties.getProperty(key);
		if (property == null) {
			properties.setProperty(key, converter.toString(defaultValue));
			return new ValueAndWriter<>(defaultValue, false);
		}
		return new ValueAndWriter<>(converter.parse(property), true);
	}

	private static Properties loadTkStrikeWorkFile(File file) {
		Properties p = new Properties() {
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unchecked")
			public Set<Entry<Object, Object>> entrySet() {
				Set<Entry<String, Object>> sortedSet = new TreeSet<>(Comparator.comparing(Entry::getKey));
				sortedSet.addAll((Set) super.entrySet());
				return (Set) sortedSet;
			}

			@Override
			public Set<Object> keySet() {
				return new TreeSet<>(super.keySet());
			}

			@Override
			public synchronized Enumeration<Object> keys() {
				return Collections.enumeration(new TreeSet<>(super.keySet()));
			}

		};
		if (file.exists()) {
			_log.info("Reading " + file.getAbsolutePath());
			try (InputStream inputStream = new FileInputStream(file)) {
				p.load(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return p;
		}
		return p;
	}

	// ---------------------------------------------------

	private static interface Converter<T> {

		T parse(String s);

		String toString(T t);
	}

	// ---------------------------------------------------

	private static class StringConverter implements Converter<String> {
		@Override
		public String parse(String s) {
			return s;
		}

		@Override
		public String toString(String t) {
			return t;
		}
	}

	// ---------------------------------------------------

	private static class IntegerConverter implements Converter<Integer> {
		@Override
		public Integer parse(String s) {
			return s != null ? Integer.parseInt(s) : null;
		}

		@Override
		public String toString(Integer t) {
			return t != null ? t.toString() : null;
		}
	}

	// ---------------------------------------------------

	private static class LevelConverter implements Converter<Level> {
		@Override
		public Level parse(String s) {
			return s != null ? Level.toLevel(s.toUpperCase().trim()) : null;
		}

		@Override
		public String toString(Level t) {
			return t != null ? t.toString() : null;
		}
	}

	// ---------------------------------------------------

	private class ValueAndWriter<T> {

		private final boolean exists;

		private final T value;

		private ValueAndWriter(T value, boolean exists) {
			this.value = value;
			this.exists = exists;
		}

		ValueAndWriter<T> ifExists(Consumer<T> consumer) {
			if (exists) {
				consumer.accept(value);
			}
			return this;
		}

		T writeIfNotExist() {
			if (!exists) {
				write();
			}
			return value;
		}
	}
}
