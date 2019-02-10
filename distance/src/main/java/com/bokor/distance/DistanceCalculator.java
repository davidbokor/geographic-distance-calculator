package com.bokor.distance;

public interface DistanceCalculator {

	/**
	 * Calculate the distance between two points.
	 *
	 * @param x1 The longitude of the first point.
	 * @param y1 The latitude of the first point.
	 * @param x2 The longitude of the second point.
	 * @param y2 The latitude of the second point.
	 * @return The distance (in meters) between the two points.
	 */
	double distance(double x1, double y1, double x2, double y2);
}
