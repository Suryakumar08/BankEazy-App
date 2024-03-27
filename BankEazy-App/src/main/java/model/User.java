package model;

import enums.UserStatus;
import enums.UserType;
import utilities.Utilities;

public class User {
	
	
	private Integer id;
	private String name;
	private String password;
	private String mobile;
	private String gender;
	private Long dob;
	private Integer type;		//1 -> Employee, 0 -> Customer
	private Integer status;		//1 -> Active, 0 -> Inactive
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Long getDob() {
		return dob;
	}
	public void setDob(long dob) {
		this.dob = dob;
	}
	public Integer getType() {
		return this.type;
	}
	public String getTypeAsString() {
		return UserType.getUserType(this.type).toString();
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setTypeFromEnum(UserType type) {
		this.type = type.getType();
	}
	public Integer getStatus() {
		return this.status;
	}
	public String getStatusAsString() {
		return UserStatus.getUserStatus(this.status).toString();
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setStatusFromEnum(UserStatus status) {
		this.status = status.getStatus();
	}
	
	@Override
	public String toString() {
		return "User [userId=" + id + "\nname=" + name + "\nmobile=" + mobile + "\ndob="
				+ Utilities.getDateString(dob) + "\ntype=" + getTypeAsString() +"\nstatus=" + getStatusAsString() + "]\n";
	}
	
	
	
}
