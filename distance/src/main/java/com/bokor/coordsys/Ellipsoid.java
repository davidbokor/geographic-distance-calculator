package com.bokor.coordsys;

public class Ellipsoid {

	public static final Ellipsoid WGS84 = getInstance(6378137,298.257223563);

	private final double m_a;
	private final double m_b;
	private final double m_flattening;
	private final double m_inverseFlattening;

	private Ellipsoid(double semiMajorAxis, double inverseFlattening) {
		m_a = semiMajorAxis;
		m_inverseFlattening = inverseFlattening;
		m_flattening = 1.0 / m_inverseFlattening;
		m_b = m_a - (m_flattening * m_a);
	}

	public static Ellipsoid getInstance(double semiMajorAxis, double inverseFlattening) {
		return new Ellipsoid(semiMajorAxis, inverseFlattening);
	}

	public double a() {
		return m_a;
	}

	public double b() {
		return m_b;
	}

	public double flattening() {
		return m_flattening;
	}

	public double inverseFlattening() {
		return m_inverseFlattening;
	}
}
