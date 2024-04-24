package model;

import java.io.Serializable;

import enums.AccountStatus;
import exception.CustomBankException;
import utilities.Validators;

public class Account implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final double MIN_BALANCE = 0;
	private static final double MAX_BALANCE = 999999999;
	private Long accountNo;
	private Integer customerId;
	private Double balance;
	private Integer branchId;
	private Integer status;     //1 -> Active 0 -> InActive
	private Long lastModifiedOn;
	private Integer lastModifiedBy;
	public Long getLastModifiedOn() {
		return lastModifiedOn;
	}
	public void setLastModifiedOn(Long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	public Integer getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(Integer lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public Long getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(double balance) throws CustomBankException{
		Validators.checkRangeBound(balance, Account.MIN_BALANCE, Account.MAX_BALANCE, "Invalid balance!");
		this.balance = ((double)Math.round(balance * 100.0) / 100.0);
	}
	public Integer getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	public Integer getStatus() {
		return status;
	}
	public String getStatusAsString() {
		return AccountStatus.getAccountStatus(status).toString();
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Account [accountNo=" + accountNo + ", customerId=" + customerId + ", balance=" + balance + ", branchId="
				+ branchId + ", status=" + getStatusAsString() + "]";
	}
	
	
	
}
