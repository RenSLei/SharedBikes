package com.rsl.bike.service;



import java.util.List;

import org.springframework.data.geo.GeoResult;

import com.rsl.bike.pojo.Bike;

public interface BikeService {

	public void save(Bike bike);

	public List<GeoResult<Bike>> findNear(double longitude, double latitude);

}
