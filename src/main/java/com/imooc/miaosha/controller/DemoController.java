package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;

import javax.jws.soap.SOAPBinding;
import java.util.Stack;

@Controller
@RequestMapping("/demo")
public class DemoController {

	@Autowired
	UserService userService;

	@Autowired
	RedisService redisService;

	@Autowired
	MQSender mqSender;

		@RequestMapping("/header")
		@ResponseBody
		public Result<String> header() {
			mqSender.sendHeader("11hello,header");
			return Result.success("hello,header");
		}

		@RequestMapping("/mq/fanout")
		@ResponseBody
		public Result<String> fanout() {
			mqSender.sendFanout("hello,fanout");
			return Result.success("hello,fanout");
		}

		@RequestMapping("/mq/topic")
		@ResponseBody
		public Result<String> topic() {
			mqSender.sendTopic("hello,isip");
			return Result.success("hello,isip");
		}


		@RequestMapping("/mq")
		@ResponseBody
		public Result<String> mq() {
			mqSender.send("hello,isip");
			return Result.success(null);
		}
	
	 	@RequestMapping("/")
	    @ResponseBody
	    String home() {
	        return "Hello World!";
	    }
	 	//1.rest api json输出 2.页面
	 	@RequestMapping("/hello")
	    @ResponseBody
	    public Result<String> hello() {
	 		return Result.success("hello,imooc");
	       // return new Result(0, "success", "hello,imooc");
	    }
	 	
	 	@RequestMapping("/helloError")
	    @ResponseBody
	    public Result<String> helloError() {
	 		return Result.error(CodeMsg.SERVER_ERROR);
	 		//return new Result(500102, "XXX");
	    }
	 	
	 	@RequestMapping("/thymeleaf")
	    public String  thymeleaf(Model model) {
	 		model.addAttribute("name", "zyk");
	 		return "hello";
	    }

		@RequestMapping("/db/get")
		@ResponseBody
		public Result<User> getDB() {
			User user = userService.getById(1);
			return Result.success(user);
		}

		@RequestMapping("/db/tx")
		@ResponseBody
		public Result<Boolean> dbTx() {
			userService.tx();
			return Result.success(true);
		}

		@RequestMapping("/redis/get")
		@ResponseBody
		public Result<User> redisGet() {
			User u = redisService.get(UserKey.getById,"1",User.class);
			return Result.success(u);
		}

		@RequestMapping("/redis/set")
		@ResponseBody
		public Result<Boolean> redisSet() {
			User user = new User();
			user.setName("zhangsan");
			user.setId(1);
			redisService.set(UserKey.getById,""+1,user);
			return Result.success(true);
		}

	 	
}
