package com.bokor.distance.impl;

import com.bokor.distance.DistanceCalculator;
import com.bokor.distance.PointAtBearingCalculator;

import java.awt.geom.Point2D;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

/**
 * Based on the Cheap Ruler described here: https://blog.mapbox.com/fast-geodesic-approximations-with-cheap-ruler-106f229ad016
 */
public class CheapRuler implements DistanceCalculator, PointAtBearingCalculator {

	private final double kx;
	private final double ky;

	private CheapRuler(double latitude) {
		double cos = cos(toRadians(latitude));
		double cos2 = 2 * cos * cos - 1;
		double cos3 = 2 * cos * cos2 - cos;
		double cos4 = 2 * cos * cos3 - cos2;
		double cos5 = 2 * cos * cos4 - cos3;

		kx = 1000 * (111.41513 * cos - 0.09455 * cos3 + 0.00012 * cos5);
		ky = 1000 * (111.13209 - 0.56605 * cos2 + 0.0012 * cos4);
	}

	public static CheapRuler getInstance(double latitude) {
		return new CheapRuler(latitude);
	}

	@Override
	public double distance(double x1, double y1, double x2, double y2) {
		double dx = (x1 - x2) * kx;
		double dy = (y1 - y2) * ky;
		return sqrt(dx * dx + dy * dy);
	}

	@Override
	public Point2D pointAtBearing(double x, double y, double angle, double distance) {
		double a = toRadians(angle);
		double dx = sin(a) * distance;
		double dy = cos(a) * distance;
		return new Point2D.Double(
				x + dx / kx,
				y + dy / ky
		);
	}
}
