package com.xtremis.daedo.tkstrike.tools;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;


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

	private final SortedMap<Integer, String> judges = judges();

	private final Map<Color, Map<Integer, Map<Part, String>>> sensors = sensors();

	public String getJudge(int num) {
		return judges.get(num);
	}

	public void setJudge(int num, String id) {
		judges.put(num, id);
	}

	public String getSensorId(Color color, int group, Part part) {
		Objects.requireNonNull(color);
		Objects.requireNonNull(part);
		if(group < 1 || group > 2) {
			throw new IllegalArgumentException("group " + group);
		}
		return sensors.get(color).get(group).get(part);
	}

	public void setSensorId(Color color, int group, Part part, String id) {
		Objects.requireNonNull(color);
		Objects.requireNonNull(part);
		if(group < 1 || group > 2) {
			throw new IllegalArgumentException("group " + group);
		}
		set(sensors, color, group, part, id);
	}

	public SortedMap<Integer, String> getJudges() {
		return judges;
	}

	public Map<Color, Map<Integer, Map<Part, String>>> getSensors() {
		return sensors;
	}

	// ************************************************************

	private static SortedMap<Integer, String> judges() {
		SortedMap<Integer, String> map = new TreeMap<>();
		map.put(1, "TODO-1");
		map.put(2, "TODO-2");
		map.put(3, "TODO-3");
		return map;
	}

	private static Map<Color, Map<Integer, Map<Part, String>>> sensors() {
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
