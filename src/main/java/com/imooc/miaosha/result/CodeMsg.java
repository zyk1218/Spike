package com.imooc.miaosha.result;

public class CodeMsg {
	private int code;
	private String msg;
	
	//通用异常
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500001, "服务端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500002,"参数校验异常：%s");
	public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500003,"非法请求");
	//登录模块 5002XX
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500200,"登录密码不可为空");
	public static CodeMsg MOBILE_EMPTY= new CodeMsg(500201,"手机号码不可为空");
	public static CodeMsg MOBILE_PATTERN = new CodeMsg(500203,"手机号码格式错误");
	public static CodeMsg MOBILE_NOT_EXISTS = new CodeMsg(500204,"手机号码不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500205,"用户密码错误");
	public static CodeMsg USER_ERROR = new CodeMsg(500206,"用户不存在");
	//商品模块 5003XX
	
	//订单模块 5004XX
	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500401,"订单不存在");


	//秒杀模块 5005XX
	public static CodeMsg MIAOSHA_OVER = new CodeMsg(500500,"商品秒杀结束");
	public static CodeMsg REPEAT_MIAOSHA = new CodeMsg(500501,"不可重复秒杀");




	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public CodeMsg fillArgs(Object... args){
		int code = this.code;
		String message = String.format(this.msg,args);
		return new CodeMsg(code,message);
	}

	
	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
}
