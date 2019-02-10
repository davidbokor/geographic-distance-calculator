# Geographic Calculator
Calculating the distance between two points on the Earth is one of the most common operations in GIS. For users, it's 
always going to be a trade-off between accuracy and performance.

The goal of this project is to demonstrate various types of distance calculations and how they differ with respect to 
accuracy and performance.

## Vicenty
The Vicenty calculation is an iterative calculation on the ellipsoid that is extremely accurate. In fact, it is 
more accurate than the WGS-84 ellipsoid's approximation of the Earth.

References:
* http://www.movable-type.co.uk/scripts/latlong-vincenty.html
* https://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf

## Haversine
The Haversine calculation uses a spherical approximation of the Earth and distances are calculated along great circles.

References:
* https://en.wikipedia.org/wiki/Haversine_formula
* https://www.movable-type.co.uk/scripts/gis-faq-5.1.html

## Cheap Ruler
The cheap calculator takes advantage of the fact that longitudinal distances are consistent at different latitudes 
but the opposite is not true.

This approach was developed by Vladimir Agofonkin at MapBox and is described in detail in his 
blog here: https://blog.mapbox.com/fast-geodesic-approximations-with-cheap-ruler-106f229ad016

Since the cheap ruler is most precise at smaller distances and fastest when those distances are all around the same 
latitude, it is most useful for very localized distance calculations.

References:
* https://blog.mapbox.com/fast-geodesic-approximations-with-cheap-ruler-106f229ad016