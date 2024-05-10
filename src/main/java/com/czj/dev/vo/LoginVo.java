package com.czj.dev.vo;

public class LoginVo {
	private String mobile;
	private String password;
	private Integer vercode;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getVercode() {
		return vercode;
	}

	public void setVercode(Integer vercode) {
		this.vercode = vercode;
	}

	@Override
	public String toString() {
		return "LoginVo{" +
				"mobile='" + mobile + '\'' +
				", password='" + password + '\'' +
				", vercode='" + vercode + '\'' +
				'}';
	}
}
