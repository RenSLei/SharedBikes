package com.rsl.bike.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rsl.bike.pojo.Bike;
import com.rsl.bike.service.BikeService;

/**
 *  标记这个类是一个用于接收请求和响应用户的一个控制器
 *  加上@Controller注解以后，Spring容器就会对它实例化
 * @author rsl
 *
 */
@Controller
public class BikeController {
	
	//到spring容器中查找BikeService类型的实例，注入到BikeController这个实例里面
	@Autowired
	private BikeService bikeService;
	
	/**
	 * 加上@RequestBody是告诉它可以接收json对象
	 * @param bike
	 * @return
	 */
	@RequestMapping("/bike/add")
	@ResponseBody
	public String add(@RequestBody Bike bike) {
		
		//调用service层，将数据保存在mongodb中
		bikeService.save(bike);
//		System.out.println(bike);
		return "success";
	}
	
	
	@RequestMapping("/bike/findNear")
	@ResponseBody
	public List<GeoResult<Bike>> findNear(double longitude,double latitude) {
		
		//调用service层，将数据保存在mongodb中
		List<GeoResult<Bike>> bikes= bikeService.findNear(longitude,latitude);
		
		return bikes;
	}
	
}
