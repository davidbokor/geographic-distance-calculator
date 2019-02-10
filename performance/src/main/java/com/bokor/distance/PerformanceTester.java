package com.bokor.distance;

import com.bokor.distance.impl.CheapRuler;
import com.bokor.distance.impl.Haversine;
import com.bokor.distance.impl.Vicenty;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Tested using the US dataset from https://simplemaps.com/data/us-cities
 */
public class PerformanceTester {

	public static void main(String[] args) throws IOException {
		List<Point2D> points = readPointsFromFile().subList(0, 10000);

		DistanceCalculator vicentyDistance = Vicenty.getInstance();
		DistanceCalculator haversineDistance = Haversine.getInstance();
		DistanceCalculator cheapDistance = CheapRuler.getInstance(38.5); // since we're testing performance, any number is fine

		for (int n = 1; n <= 2; n++) {
			System.out.println("Round " + n + "...");

			System.out.println("Vicenty: " + testPerformance(vicentyDistance, points));
			System.out.println("Haversine: " + testPerformance(haversineDistance, points));
			System.out.println("CheapRuler: " + testPerformance(cheapDistance, points));
		}
	}

	private static long testPerformance(DistanceCalculator calculator, List<Point2D> points) {
		long startTime = System.currentTimeMillis();
		for (Point2D point1 : points) {
			for (Point2D point2 : points) {
				calculator.distance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
			}
		}
		long endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	private static List<Point2D> readPointsFromFile() throws IOException {
		try (InputStream in = PerformanceTester.class.getResourceAsStream("/uscitiesv1.4.csv")) {
			CSVFormat format = CSVFormat.DEFAULT
					.withRecordSeparator(',')
					.withQuote('\"')
					.withFirstRecordAsHeader();

			Iterable<CSVRecord> records = format.parse(new InputStreamReader(in));
			List<Point2D> result = new ArrayList<>();
			for (CSVRecord record : records) {
				String longitude = record.get("lng");
				String latitude = record.get("lat");
				result.add(new Point2D.Double(
						Double.parseDouble(longitude),
						Double.parseDouble(latitude)
				));
			}
			return result;
		}
	}
}
