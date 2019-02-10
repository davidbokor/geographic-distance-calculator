package com.bokor.distance;

import java.awt.geom.Point2D;

public interface PointAtBearingCalculator {

	/**
	 * Given a starting point on the earth, calculate a second point given an angle and distance.
	 * @param x The longitude of the starting point.
	 * @param y The latitude of the starting point.
	 * @param angle The bearing from the point (in degrees).
	 * @param distance The distance from the point (in meters).
	 * @return The point at the specified bearing and distance.
	 */
	Point2D pointAtBearing(double x, double y, double angle, double distance);
}
