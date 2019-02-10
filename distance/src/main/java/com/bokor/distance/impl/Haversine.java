package com.bokor.distance.impl;

import com.bokor.distance.DistanceCalculator;
import com.bokor.distance.PointAtBearingCalculator;

import java.awt.geom.Point2D;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Based on https://www.movable-type.co.uk/scripts/latlong.html
 */
public class Haversine implements DistanceCalculator, PointAtBearingCalculator {

	private final static double DEFAULT_R = 6371e3;
	private final static Haversine INSTANCE = new Haversine(DEFAULT_R);

	private final double R;

	private Haversine(double R) {
		this.R = R;
	}

	public static Haversine getInstance() {
		return INSTANCE;
	}

	@Override
	public double distance(double x1, double y1, double x2, double y2) {
		return distance(R, x1, y1, x2, y2);
	}

	@Override
	public Point2D pointAtBearing(double x, double y, double angle, double distance) {
		return pointAtBearing(R, x, y, angle, distance);
	}

	/**
	 * Calculate the distance between two points.
	 * @param x1 The longitude of the first point.
	 * @param y1 The latitude of the first point.
	 * @param x2 The longitude of the second point.
	 * @param y2 The latitude of the second point.
	 * @return The distance (in meters) between the two points.
	 */
	public static double distance(double R, double x1, double y1, double x2, double y2) {
		double lambda1 = toRadians(x1);
		double lambda2 = toRadians(x2);
		double phi1 = toRadians(y1);
		double phi2 = toRadians(y2);

		double delta_phi = phi2 - phi1;
		double delta_lambda = lambda2 - lambda1;

		double sin_delta_phi = sin(delta_phi / 2);
		double sin_delta_lambda = sin(delta_lambda / 2);

		double a = sin_delta_phi * sin_delta_phi + cos(phi1) * cos(phi2) * sin_delta_lambda * sin_delta_lambda;
		double c = 2 * atan2(sqrt(a), sqrt(1 - a));
		return R * c;
	}

	/**
	 * Given a starting point on the earth, calculate a second point given an angle and distance.
	 * @param x The longitude of the starting point.
	 * @param y The latitude of the starting point.
	 * @param angle The bearing from the point (in degrees)
	 * @param distance The distance from the point (in meters)
	 * @return The point at the specified bearing and distance.
	 */
	public static Point2D pointAtBearing(double R, double x, double y, double angle, double distance) {
		double lambda1 = toRadians(x);
		double phi1 = toRadians(y);

		double theta = toRadians(angle);
		double delta = distance / R;

		double sin_phi1 = sin(phi1);
		double cos_phi1 = cos(phi1);
		double sin_delta = sin(delta);
		double cos_delta = cos(delta);
		double sin_theta = sin(theta);
		double cos_theta = cos(theta);

		double sin_phi2 = sin_phi1 * cos_delta + cos_phi1 * sin_delta * cos_theta;
		double phi2 = asin(sin_phi2);
		double lambda2 = lambda1 + atan2(sin_theta * sin_delta * cos_phi1, cos_delta - sin_phi1 * sin_phi2);

		return new Point2D.Double(
				toDegrees(lambda2),
				toDegrees(phi2));
	}
}
