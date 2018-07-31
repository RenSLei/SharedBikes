package com.rsl.bike.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rsl.bike.pojo.User;
import com.rsl.bike.service.UserService;

/**
 * @author rsl
 *
 */
@Controller
public class UserController {
	
	@Autowired//按类型注入
	private UserService userService;
	
	
	
	/**
	 * 响应用户获取验证码的处理方法：调用腾讯云接口，发送短信
	 * @param countryCode 国家代码
	 * @param phoneNum 电话号码
	 * @return 发送成功则返回true,否则false
	 */
	@RequestMapping("/user/genCode")
	@ResponseBody//以Ajax的方式返回
	public boolean genVerifyCode(String countryCode,String phoneNum) {
		boolean flag = userService.sendMsg(countryCode,phoneNum);
		System.out.println(phoneNum);
		return flag;
	}
	
	
	
	/**
	 * 校验用户的验证码
	 * @param phoneNum 用户手机号
	 * @param verifyCode 用户输入的参数
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/user/verify")
	public boolean verify(String phoneNum,String verifyCode) {
		//调用userService层，进行验证码的校验
		return userService.verify(phoneNum,verifyCode);
		
	}
	
	
	/** 处理用户注册的请求，将接受到的数据通过jpa的方式自动持久化到数据库mongodb中
	 * @return
	 * @param user user对象，
	 * 注：此处，接受参数的前面加一个注解：@RequestBody,接受json类型的参数，在小程序里面的程序里使用post
	 * 提交数据的时候就不用改变请求头了，因为post的默认请求头是返回json类型
	 * set到对应的实体类中的属性
	 * 
	 */
	@RequestMapping("/user/register")
	@ResponseBody
	public boolean reg(@RequestBody User user) {
		boolean flag=true;
		//调用service，将用户的数据保存起来
		try {
			userService.register(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag=false;
		}
		return flag;
	}
	
	
	/**
	 * 交押金的更新操作
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/user/deposit")
	public boolean deposit(@RequestBody User user) {
		boolean flag=true;
		try {
			userService.update(user);
		} catch (Exception e) {
			// 一旦出现异常，就认为该方法充值失败
			e.printStackTrace();
			flag=false;
		}
		return flag;
	}
	
	/**
	 * 更新用户的信息，姓名以及身份证号以及状态status
	 * @param user 以user对象来接受传递的参数，并注解@RequsetBody表示以post提交的方式以json的方式返回，若不加，则必须修改请求头
	 * @return
	 */
	@RequestMapping("/user/identify")
	@ResponseBody
	public boolean indentify(@RequestBody User user) {
		boolean flag=true;
		
		//将用户的实名信息添加到数据库中users中，注意是更新
		try {
			userService.update(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag=false;
		}
		return flag;
	} 
}
