package model;

public class Branch {
	private Integer id;
	private String name;
	private String address;
	private String ifsc;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIfsc() {
		return ifsc;
	}
	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	@Override
	public String toString() {
		return "Branch [id=" + id + ", name=" + name + ", address=" + address + ", ifsc=" + ifsc + "]";
	}
	
	
	
}
