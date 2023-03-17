package com.xtremis.daedo.tkstrike.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;

import org.apache.log4j.Logger;

import com.xtremis.daedo.tkstrike.tools.NodeIds.Color;
import com.xtremis.daedo.tkstrike.tools.NodeIds.Part;
import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;

/**
 * @author f.agu
 * @created 15 mars 2023 16:56:31
 */
public class TkStrikeUtil {

	private static final Logger _log = Logger.getLogger(TkStrikeUtil.class);

	private static final String FILENAME = "tkStrikeWork.tks";

	private static final String KEY_GENERATION = "tkStrike.patch.generation";

	private static final String KEY_NODEIDS_PREFIX = "tkStrike.nodeids";

	private static final Generation DEFAULT = Generation.GEN2;

	private static final TkStrikeUtil INSTANCE = new TkStrikeUtil();

	private final File file;

	private TkStrikeUtil() {
		file = new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + FILENAME);
	}

	public static TkStrikeUtil getInstance() {
		return INSTANCE;
	}

	public Generation getGeneration() {
		return loadTkStrikeWorkFile()
				.map(p -> Generation.valueOf(p.getProperty(KEY_GENERATION, DEFAULT.name()).toUpperCase()))
				.orElse(DEFAULT);
	}

	public void setGeneration(Generation generation) throws IOException {
		if (generation == null) {
			return;
		}
		File file = getTkStrikeWorkFile();
		if (!file.exists()) {
			file.createNewFile();
		}
		_log.info("Writing " + file.getAbsolutePath());
		Properties properties = loadTkStrikeWorkFile().orElseThrow(RuntimeException::new);
		properties.setProperty(KEY_GENERATION, generation.name());
		write(properties);
	}

	public NodeIds getNodeIds() throws IOException {
		boolean updated = false;
		File file = getTkStrikeWorkFile();
		if (!file.exists()) {
			file.createNewFile();
		}
		_log.info("Reading " + file.getAbsolutePath());
		Properties properties = loadTkStrikeWorkFile().orElseThrow(RuntimeException::new);
		NodeIds nodeIds = new NodeIds();
		// sensors
		for (Color color : Color.values()) {
			for (int group = 1; group <= 2; ++group) {
				for (Part part : Part.values()) {
					StringJoiner joiner = new StringJoiner(".");
					joiner.add(KEY_NODEIDS_PREFIX);
					joiner.add(color.name().toLowerCase());
					joiner.add(Integer.toString(group));
					joiner.add(part.name().toLowerCase());
					String id = properties.getProperty(joiner.toString());
					if (id == null) {
						updated = true;
						properties.setProperty(joiner.toString(), nodeIds.getSensorId(color, group, part));
					} else {
						nodeIds.setSensorId(color, group, part, id);
					}
				}
			}
		}
		if (updated) {
			write(properties);
		}
		return nodeIds;
	}

	public void setNodeIds(NodeIds nodeIds) throws IOException {
		File file = getTkStrikeWorkFile();
		if (!file.exists()) {
			file.createNewFile();
		}
		_log.info("Writing " + file.getAbsolutePath());
		Properties properties = loadTkStrikeWorkFile().orElseThrow(RuntimeException::new);
		writeNodeIds(nodeIds, properties);
	}

	public File getTkStrikeWorkFile() {
		return file;
	}

	public Optional<Properties> loadTkStrikeWorkFile() {
		File file = getTkStrikeWorkFile();
		if (file.exists()) {
			_log.info("Reading " + file.getAbsolutePath());
			Properties properties = new Properties();
			try (InputStream inputStream = new FileInputStream(file)) {
				properties.load(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return Optional.of(properties);
		}
		return Optional.empty();
	}

	// **********************************************

	private void writeNodeIds(NodeIds nodeIds, Properties properties) throws IOException {
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
		write(properties);
	}

	private void write(Properties properties) throws IOException {
		try (OutputStream outputStream = new FileOutputStream(file)) {
			properties.store(outputStream, (String) null);
		}
	}

}
