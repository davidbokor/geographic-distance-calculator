package com.bokor.distance.impl;

import org.junit.Test;

import java.awt.geom.Point2D;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CheapRulerTester {

	@Test
	public void distance() {
		// Empire State Building
		double x1 = -73.985656;
		double y1 = 40.748433;

		// Mid-Hudson Bridge
		double x2 = -73.946111;
		double y2 = 41.703056;

		double d = CheapRuler.getInstance(y1).distance(x1, y1, x2, y2);

		double vicentyDistance = 106070.61959002404;
		assertThat(d, is(closeTo(vicentyDistance, vicentyDistance * .0001))); // within .1% of Vicenty
	}

	@Test
	public void pointAtBearing() {
		// Empire State Building
		double x1 = -73.985656;
		double y1 = 40.748433;

		Point2D pt = CheapRuler.getInstance(y1).pointAtBearing(x1, y1, 120, 10000);
		assertThat(pt.getX(), is(closeTo(-73.88318142686842, 0.0001))); // within .01% of Vicenty
		assertThat(pt.getY(), is(closeTo(40.703362166137616, 0.0001)));
	}

}