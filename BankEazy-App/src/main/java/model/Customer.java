package model;

import java.io.Serializable;

public class Customer extends User implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer userId;
	private String pan;
	private String aadhar;

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String getAadhar() {
		return aadhar;
	}
	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}
	@Override
	public String toString() {
		return super.toString() + "\nCustomer [pan=" + pan + "\naadhar=" + aadhar + "]\n\n";
	}
	
	
	
}
