package com.xtremis.daedo.tkstrike.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.xtremis.daedo.tkstrike.utils.TkStrikeBaseDirectoriesUtil;


/**
 * @author f.agu
 * @created 15 mars 2023 16:56:31
 */
public class TkStrikeUtil {

	private static final Logger _log = Logger.getLogger(TkStrikeUtil.class);

	private static final String FILENAME = "tkStrikeWork.tks";

	private static final String KEY_GENERATION = "tkStrike.patch.generation";

	private static final Generation DEFAULT = Generation.GEN2;

	private static final TkStrikeUtil INSTANCE = new TkStrikeUtil();

	public static TkStrikeUtil getInstance() {
		return INSTANCE;
	}

	public Generation getGeneration() {
		return loadTkStrikeWorkFile()
				.map(p -> Generation.valueOf(p.getProperty(KEY_GENERATION, DEFAULT.name()).toUpperCase()))
				.orElse(DEFAULT);
	}

	public void setGeneration(Generation generation) throws IOException {
		if(generation == null) {
			return;
		}
		File file = getTkStrikeWorkFile();
		if( ! file.exists()) {
			file.createNewFile();
		}
		_log.info("Writing " + file.getAbsolutePath());
		Properties properties = loadTkStrikeWorkFile().orElseThrow(RuntimeException::new);
		properties.setProperty(KEY_GENERATION, generation.name());
		try (OutputStream outputStream = new FileOutputStream(file)) {
			properties.store(outputStream, (String)null);
		}
	}

	public File getTkStrikeWorkFile() {
		return new File(TkStrikeBaseDirectoriesUtil.getInstance().getWorkBaseDir() + FILENAME);
	}

	public Optional<Properties> loadTkStrikeWorkFile() {
		File file = getTkStrikeWorkFile();
		if(file.exists()) {
			_log.info("Reading " + file.getAbsolutePath());
			Properties properties = new Properties();
			try (InputStream inputStream = new FileInputStream(file)) {
				properties.load(inputStream);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			return Optional.of(properties);
		}
		return Optional.empty();
	}

}
