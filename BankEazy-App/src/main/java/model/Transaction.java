package model;

import enums.TransactionStatus;
import enums.TransactionType;
import utilities.Utilities;

public class Transaction {
	private Long transactionId;
	private Integer customerId;
	private Long accountNo;
	private Long transactionAccountNo;
	private String description;
	private Integer type;		//1 ->	 Deposit, 2 -> Withdraw, 3 -> debit, 4 -> credit
	private Integer status;		//0 -> Failure, 1 -> Success
	private Long time;
	private Double closingBalance;
	private Double amount;
	private Long referenceNo;
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public Long getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}
	public Long getTransactionAccountNo() {
		return transactionAccountNo;
	}
	public void setTransactionAccountNo(long transactionAccountNo) {
		this.transactionAccountNo = transactionAccountNo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Integer getType() {
		return type;
	}
	public void setTypeFromEnum(TransactionType type) {
		this.type = type.getType();
	}
	public String getTypeAsString() {
		return TransactionType.getTransactionType(this.type).toString();
	}
	public void setType(int type) {
		this.type = type;
	}
	public Integer getStatus() {
		return status;
	}
	public String getStatusAsString() {
		return TransactionStatus.getTransactionStatus(status).toString();
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setStatusFromEnum(TransactionStatus status) {
		this.status = status.getStatus();
	}
	public Long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Double getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(double closingBalance) {
		this.closingBalance = ((double)Math.round(closingBalance * 100.0)) / 100.0;
	}
	public Long getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(Long referenceNo) {
		this.referenceNo = referenceNo;
	}
	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", customerId=" + customerId
				+ ", accountNo=" + accountNo + ", transactionAccountNo=" + transactionAccountNo + ", description="
				+ description + ", type=" + getTypeAsString() + ", status=" + getStatusAsString() + ", time=" + Utilities.getDateTimeString(time) + ", closingBalance="
				+ closingBalance + ", amount=" + amount + " referenceNo=" + referenceNo + "]";
	}
}
