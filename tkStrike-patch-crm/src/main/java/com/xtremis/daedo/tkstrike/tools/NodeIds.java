package com.xtremis.daedo.tkstrike.tools;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author f.agu
 */
public class NodeIds {

	public enum Color {
		BLUE, RED
	}

	public enum Part {
		BODY, HEAD
	}

	// TODO judge

	private final Map<Color, Map<Integer, Map<Part, String>>> SENSORS = createMap();

	public String getSensorId(Color color, int group, Part part) {
		Objects.requireNonNull(color);
		Objects.requireNonNull(part);
		if (group < 1 || group > 2) {
			throw new IllegalArgumentException("group " + group);
		}
		return SENSORS.get(color).get(group).get(part);
	}

	public void setSensorId(Color color, int group, Part part, String id) {
		Objects.requireNonNull(color);
		Objects.requireNonNull(part);
		if (group < 1 || group > 2) {
			throw new IllegalArgumentException("group " + group);
		}
		set(SENSORS, color, group, part, id);
	}

	public Map<Color, Map<Integer, Map<Part, String>>> getSensors() {
		return SENSORS;
	}

	// ************************************************************

	private static Map<Color, Map<Integer, Map<Part, String>>> createMap() {
		Map<Color, Map<Integer, Map<Part, String>>> map = new EnumMap<>(Color.class);
		set(map, Color.BLUE, 1, Part.BODY, "1105630150");
		set(map, Color.BLUE, 1, Part.HEAD, "1100430114");
		set(map, Color.BLUE, 2, Part.BODY, "1100440204");
		set(map, Color.BLUE, 2, Part.HEAD, "1088720315");
		set(map, Color.RED, 1, Part.BODY, "1105630157");
		set(map, Color.RED, 1, Part.HEAD, "1100430121");
		set(map, Color.RED, 2, Part.BODY, "1100440205");
		set(map, Color.RED, 2, Part.HEAD, "1088720316");
		return map;
	}

	private static void set(Map<Color, Map<Integer, Map<Part, String>>> map, Color color, int group, Part part,
			String id) {
		map.computeIfAbsent(color, k -> new HashMap<>(2)) //
				.computeIfAbsent(group, l -> new EnumMap<>(Part.class)) //
				.put(part, id);
	}

}
