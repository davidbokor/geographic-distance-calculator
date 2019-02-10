package com.bokor.distance.impl;

import org.junit.Test;

import java.awt.geom.Point2D;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class HaversineTester {

	@Test
	public void distance() {
		// New York
		double x1 = -74.2598655;
		double y1 = 40.6971494;

		// San Francisco
		double x2 = -122.4726193;
		double y2 = 37.7576948;

		double d = Haversine.getInstance().distance(x1, y1, x2, y2);
		assertThat(d, is(closeTo(4113526.4425189125, 0.000000001)));
	}

	@Test
	public void pointAtBearing() {
		// New York
		double x1 = -74.2598655;
		double y1 = 40.6971494;

		Point2D pt = Haversine.getInstance().pointAtBearing(x1, y1, 281.5672222222222, 4113526.4425189125);
		assertThat(pt.getX(), is(closeTo(-122.47114411, 0.00000001)));
		assertThat(pt.getY(), is(closeTo(37.754497091, 0.00000001)));
	}
}
