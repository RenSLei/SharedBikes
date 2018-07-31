package com.rsl.bike.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.rsl.bike.pojo.User;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	/* 
	 * 通过国家代码和电话号码，调用腾讯云的短信接口，给指定的手机发送短信。并将号码和验证码存储到redis数据库中，维持5分钟。
	 * @see com.rsl.bike.service.UserService#sendMsg(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean sendMsg(String countryCode, String phoneNum) {
		
		Boolean flag=true;
		
		// 调用腾讯云的短信API,从redis中取出appid和appkey,腾讯云根据这两个参数才能调用发送短信的功能，appid和appkey是
		int appid= Integer.parseInt(stringRedisTemplate.opsForValue().get("appid"));
		String appkey= stringRedisTemplate.opsForValue().get("appkey");
		
		//首先生成一个随机的4位数字
		String code = (int)(Math.random()*10000)+"";
		
		SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
	    
		try {
			//向用户对应的手机号发送短信验证码
			SmsSingleSenderResult result = ssender.send(0, countryCode, phoneNum, code+"为您的登录验证码。如非本人操作，请忽略本短信。", "", "");
			
			//将发送的手机号作为key，验证码作为value保存到redis中，且5分钟后数据从redis删除
			stringRedisTemplate.opsForValue().set(phoneNum, code, 300, TimeUnit.SECONDS);
			
		} catch (Exception e) {
			
			//一旦出现异常就将结果置为false
			flag=false;
			e.printStackTrace();
		} 
		
	    //System.out.println(result);
		return flag;
	}


	/* 校验用胡输入的验证码和数据库中的真实验证码是否一致。
	 * @see com.rsl.bike.service.UserService#verify(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean verify(String phoneNum, String verifyCode) {
		boolean flag=false;
		// 调用redisTemplate,根据手机号的key,查找对应的验证码
		//实际的真实的验证码
		String code=stringRedisTemplate.opsForValue().get(phoneNum);
		//将用户传入的验证码跟实际的验证码对比
		if(code!=null && code.equals(verifyCode)) {
			flag=true;
		}
		return flag;
		
	}


	/* 将用户的数据存入数据库中
	 * @see com.rsl.bike.service.UserService#register(com.rsl.bike.pojo.User)
	 */
	@Override
	public void register(User user) {
		
		// 调用mongodb的dao，将用户数据保存起来
		mongoTemplate.insert(user);
		
		
	}


	/* 模拟充值用户
	 * @see com.rsl.bike.service.UserService#update(com.rsl.bike.pojo.User)
	 */
	@Override
	public void update(User user) {
		// TODO 如果对应_id的数据不存在，就插入，存在就更新
		//根据手机号进行数据的更新
		//mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), Update.update("deposit", user.getDeposit()), User.class);
		//可以更新user的所有信息，不要一个里面写死
		
		//判断传过来的的参数中，某个参数是否为空，不为空则更新
		Update update=new Update();
		
		if(user.getDeposit()!= null) {
			update.set("deposit", user.getDeposit());
		}
		
		if(user.getStatus()!=null) {
			update.set("status", user.getStatus());
		}

		if(user.getName()!=null) {
			update.set("name", user.getName());
		}
		
		if(user.getIdNum()!=null) {
			update.set("idNum", user.getIdNum());
		}
		
		mongoTemplate.updateFirst(new Query(Criteria.where("phoneNum").is(user.getPhoneNum())), update, User.class);
	}
	
}
