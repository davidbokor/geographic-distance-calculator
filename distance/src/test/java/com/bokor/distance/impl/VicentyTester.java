package com.bokor.distance.impl;

import com.bokor.coordsys.Ellipsoid;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VicentyTester {

	@Test
	public void distance() {
		// New York
		double x1 = -74.2598655;
		double y1 = 40.6971494;

		// San Francisco
		double x2 = -122.4726193;
		double y2 = 37.7576948;

		double d = Vicenty.distance(Ellipsoid.WGS84, x1, y1, x2, y2);
		assertThat(d, is(closeTo(4123543.156776, 0.0000001)));
	}

	@Test
	public void pointAtBearing() {
		// New York
		double x1 = -74.2598655;
		double y1 = 40.6971494;

		Point2D pt = Vicenty.pointAtBearing(Ellipsoid.WGS84, x1, y1, 281.5926782512639, 4123543.156776);

		// San Francisco
		double x2 = -122.4726193;
		double y2 = 37.7576948;
		assertThat(pt.getX(), is(closeTo(x2, 0.0000001)));
		assertThat(pt.getY(), is(closeTo(y2, 0.0000001)));
	}
}
