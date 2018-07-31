package com.rsl.bike.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

//Bike这个类以后跟mongodb中的bikes  collection关联上了，没有bikes就会自动创建
@Document(collection="bikes")
public class Bike {
	
	//主键（唯一、建立索引），id对应的是mongodb中的_id
	@Id
	private String id;
	
//	private double longitude;
//	private double latitude;
	
	//表示经纬度的数组[精度，纬度]
	//注解Geo索引
	@GeoSpatialIndexed(type=GeoSpatialIndexType.GEO_2DSPHERE)//球面索引
	private double[] location;
	
	//建立索引，
	@Indexed
	private long bikeNo;
	
	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	private int status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public double getLongitude() {
//		return longitude;
//	}
//
//	public void setLongitude(double longitude) {
//		this.longitude = longitude;
//	}
//
//	public double getLatitude() {
//		return latitude;
//	}
//
//	public void setLatitude(double latitude) {
//		this.latitude = latitude;
//	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getBikeNo() {
		return bikeNo;
	}

	public void setBikeNo(long bikeNo) {
		this.bikeNo = bikeNo;
	}
	
}
