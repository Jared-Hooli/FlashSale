package com.czj.dev.result;

public class Result<T> {
	private Integer code;
	private String msg;
	private T data;

	public static <T> Result<T> success(T data) {
		// code为0，msg为“success”
		return new Result<>(data);
	}

	public static <T> Result<T> error(CodeMsg codeMsg) {
		return new Result<>(codeMsg);
	}

	// 封装错误
	private Result(CodeMsg codeMsg) {
		if (codeMsg == null) {
			return;
		}
		this.code = codeMsg.getCode();
		this.msg = codeMsg.getMsg();
	}

	// 封装成功
	private Result(T data) {
		this.code = 0;
		this.msg = "success";
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}