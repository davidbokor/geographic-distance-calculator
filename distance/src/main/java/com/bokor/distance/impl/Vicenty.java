package com.bokor.distance.impl;

import com.bokor.coordsys.Ellipsoid;
import com.bokor.distance.DistanceCalculator;
import com.bokor.distance.PointAtBearingCalculator;

import java.awt.geom.Point2D;

import static java.lang.Double.isNaN;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Based on http://www.movable-type.co.uk/scripts/latlong-vincenty.html
 */
public class Vicenty implements DistanceCalculator, PointAtBearingCalculator {

	private static final double TOLERANCE = 1e-12;

	private final Ellipsoid m_ellipsoid;

	private Vicenty(Ellipsoid ellipsoid) {
		m_ellipsoid = ellipsoid;
	}

	public static Vicenty getInstance() {
		return getInstance(Ellipsoid.WGS84);
	}

	public static Vicenty getInstance(Ellipsoid ellipsoid) {
		return new Vicenty(ellipsoid);
	}

	@Override
	public double distance(double x1, double y1, double x2, double y2) {
		return distance(m_ellipsoid, x1, y1, x2, y2);
	}

	@Override
	public Point2D pointAtBearing(double x, double y, double angle, double distance) {
		return pointAtBearing(m_ellipsoid, x, y, angle, distance);
	}

	/**
	 * Calculate the distance between two points.
	 *
	 * @param e  The ellipsoid to use for the calculation.
	 * @param x1 The longitude of the first point.
	 * @param y1 The latitude of the first point.
	 * @param x2 The longitude of the second point.
	 * @param y2 The latitude of the second point.
	 * @return The distance (in meters) between the two points.
	 */
	public static double distance(Ellipsoid e, double x1, double y1, double x2, double y2) {
		final double lambda1 = toRadians(x1);
		final double lambda2 = toRadians(x2);
		final double phi1 = toRadians(y1);
		final double phi2 = toRadians(y2);

		final double L = lambda2 - lambda1;

		final double tanU1 = (1 - e.flattening()) * tan(phi1);
		final double cosU1 = 1 / sqrt((1 + tanU1 * tanU1));
		final double sinU1 = tanU1 * cosU1;
		final double tanU2 = (1 - e.flattening()) * tan(phi2);
		final double cosU2 = 1 / sqrt((1 + tanU2 * tanU2));
		final double sinU2 = tanU2 * cosU2;

		double lambda = L; // first approximation
		double lambda_prime;
		int iterationLimit = 100;

		double sin_lambda;
		double cos_lambda;
		double sin_sigma;
		double cos_sigma;
		double sigma;
		double cosSq_alpha;
		double cos2sigmaM;
		do {
			sin_lambda = sin(lambda);
			cos_lambda = cos(lambda);
			double sinSq_sigma = (cosU2 * sin_lambda) * (cosU2 * sin_lambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cos_lambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cos_lambda);
			if (sinSq_sigma == 0) {
				return 0;  // co-incident points
			}

			sin_sigma = sqrt(sinSq_sigma);
			cos_sigma = sinU1 * sinU2 + cosU1 * cosU2 * cos_lambda;
			sigma = atan2(sin_sigma, cos_sigma);
			double sin_alpha = cosU1 * cosU2 * sin_lambda / sin_sigma;
			cosSq_alpha = 1 - sin_alpha * sin_alpha;
			cos2sigmaM = cos_sigma - 2 * sinU1 * sinU2 / cosSq_alpha;

			if (isNaN(cos2sigmaM)) {
				cos2sigmaM = 0;  // equatorial line: cos2sigma=0 (§6)
			}

			double C = e.flattening() / 16 * cosSq_alpha * (4 + e.flattening() * (4 - 3 * cosSq_alpha));
			lambda_prime = lambda;
			lambda = L + (1 - C) * e.flattening() * sin_alpha * (sigma + C * sin_sigma * (cos2sigmaM + C * cos_sigma * (-1 + 2 * cos2sigmaM * cos2sigmaM)));
			iterationLimit--;
		}
		while (abs(lambda - lambda_prime) > TOLERANCE && iterationLimit > 0);

		if (iterationLimit == 0) {
			throw new IllegalStateException("Formula failed to converge");
		}

		final double b2 = e.b() * e.b();
		final double uSq = cosSq_alpha * (e.a() * e.a() - b2) / b2;
		final double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		final double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		final double delta_sigma = calculateDeltaSigma(B, sin_sigma, cos_sigma, cos2sigmaM);

//		double fwdAz = atan2(cosU2 * sin_lambda, cosU1 * sinU2 - sinU1 * cosU2 * cos_lambda);
//		double revAz = atan2(cosU1 * sin_lambda, -sinU1 * cosU2 + cosU1 * sinU2 * cos_lambda);

		return e.b() * A * (sigma - delta_sigma);
	}

	/**
	 * Given a starting point on the earth, calculate a second point given an angle and distance.
	 *
	 * @param e        The ellipsoid to use for the calculation.
	 * @param x        The longitude of the starting point.
	 * @param y        The latitude of the starting point.
	 * @param angle    The bearing from the point (in degrees)
	 * @param distance The distance from the point (in meters)
	 * @return The point at the specified bearing and distance.
	 */
	public static Point2D pointAtBearing(Ellipsoid e, double x, double y, double angle, double distance) {
		final double lambda1 = toRadians(x);
		final double phi1 = toRadians(y);

		final double alpha = toRadians(angle);
		final double sin_alpha1 = sin(alpha);
		final double cos_alpha1 = cos(alpha);

		final double tanU1 = (1 - e.flattening()) * tan(phi1);
		final double cosU1 = 1 / sqrt((1 + tanU1 * tanU1));
		final double sinU1 = tanU1 * cosU1;
		final double sigma1 = atan2(tanU1, cos_alpha1);
		final double sin_alpha = cosU1 * sin_alpha1;
		final double cosSq_alpha = 1 - sin_alpha * sin_alpha;

		final double b2 = e.b() * e.b();
		final double uSq = cosSq_alpha * (e.a() * e.a() - b2) / b2;
		final double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		final double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

		double sigma = distance / (e.b() * A); // first approximation
		double sigma_prime;
		double cos2sigmaM;
		double sin_sigma;
		double cos_sigma;
		do {
			cos2sigmaM = cos(2 * sigma1 + sigma);
			sin_sigma = sin(sigma);
			cos_sigma = cos(sigma);
			double delta_sigma = calculateDeltaSigma(B, sin_sigma, cos_sigma, cos2sigmaM);
			sigma_prime = sigma;
			sigma = distance / (e.b() * A) + delta_sigma;
		}
		while (abs(sigma - sigma_prime) > TOLERANCE);

		final double tmp = sinU1 * sin_sigma - cosU1 * cos_sigma * cos_alpha1;
		final double phi2 = atan2(sinU1 * cos_sigma + cosU1 * sin_sigma * cos_alpha1, (1 - e.flattening()) * sqrt(sin_alpha * sin_alpha + tmp * tmp));
		final double lambda = atan2(sin_sigma * sin_alpha1, cosU1 * cos_sigma - sinU1 * sin_sigma * cos_alpha1);
		final double C = e.flattening() / 16 * cosSq_alpha * (4 + e.flattening() * (4 - 3 * cosSq_alpha));
		final double L = lambda - (1 - C) * e.flattening() * sin_alpha *
				(sigma + C * sin_sigma * (cos2sigmaM + C * cos_sigma * (-1 + 2 * cos2sigmaM * cos2sigmaM)));
		final double lambda2 = (lambda1 + L + 3 * PI) % (2 * PI) - PI;  // normalise to -180...+180

//		double revAz = atan2(sin_alpha, -tmp);

		return new Point2D.Double(
				toDegrees(lambda2),
				toDegrees(phi2)
		);
	}

	private static double calculateDeltaSigma(double B, double sin_sigma, double cos_sigma, double cos2sigmaM) {
		double cos2sigmaM_2 = cos2sigmaM * cos2sigmaM;
		return B * sin_sigma * (cos2sigmaM + B / 4 * (cos_sigma * (-1 + 2 * cos2sigmaM_2) -
				B / 6 * cos2sigmaM * (-3 + 4 * sin_sigma * sin_sigma) * (-3 + 4 * cos2sigmaM_2)));
	}
}
