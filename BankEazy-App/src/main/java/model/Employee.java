package model;

import utilities.Utilities;

public class Employee extends User {
	private Integer userId;
	private Integer salary;
	private Long joiningDate;
	private Integer branchId;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	public Long getJoiningDate() {
		return joiningDate;
	}
	public void setJoiningDate(long joinedDate) {
		this.joiningDate = joinedDate;
	}
	public Integer getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	@Override
	public String toString() {
		return super.toString() + "Employee [salary=" + salary + "\njoinedDate=" + Utilities.getDateString(joiningDate) + "\nbranchId=" + branchId + "]\n";
	}
	
	
	
}
