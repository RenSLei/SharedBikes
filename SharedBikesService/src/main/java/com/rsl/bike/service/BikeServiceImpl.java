package com.rsl.bike.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.rsl.bike.pojo.Bike;

@Service
public class BikeServiceImpl implements BikeService {
	
	//dao将实体类的属性持久化到数据库中
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void save(Bike bike) {
		// 调用具体的业务
		//在没有将bikes与Bike实体类关联之前：mongoTemplate.insert(bike, "bikes");
		
		//在Bike类中，添加了注解，注解中保存了映射关系，而且如果mongodb中的bike数据库中若没有bikes这个collection，jpa技术会在mongodb中根据Bike类中的属性创建一个collection
		mongoTemplate.insert(bike);
				
	}

	/* 根据当前的经纬度查找附近的单车
	 * @see com.rsl.bike.service.BikeService#findNear(double, double)
	 */
	@Override
	public List<GeoResult<Bike>> findNear(double longitude, double latitude) {
		//查找所有的单车
		//return mongoTemplate.findAll(Bike.class);
		
		//指定要查找的经纬度的所在的位置
		NearQuery nearQuery = NearQuery.near(longitude, latitude);
		
		//查找的范围和距离单位：200米
		nearQuery.maxDistance(0.2,Metrics.KILOMETERS);
		
		
		//
		GeoResults<Bike> geoResults = mongoTemplate.geoNear(nearQuery.query(new Query(Criteria.where("status").is(0)).limit(20)), Bike.class);
			return geoResults.getContent();
	}
	
	
}
