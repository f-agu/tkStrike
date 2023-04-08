package com.xtremis.daedo.tkstrike.tools;

import java.util.LinkedList;

public class LimitedLastQueue<E> extends LinkedList<E> {

	private static final long serialVersionUID = 3757368699773514090L;

	private final int limit;

	public LimitedLastQueue(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("limit must at least 1: " + limit);
		}
		this.limit = limit;
	}

	@Override
	public boolean add(E o) {
		boolean added = super.add(o);
		while (added && size() > limit) {
			super.remove();
		}
		return added;
	}
}
