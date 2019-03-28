package ykk.cb.com.cbwms.model.stockBusiness;

import java.io.Serializable;

public class StkTransferOutEntryApplyRecord implements Serializable {

	/*id*/
	private int id;
	/*单据Id*/
	private int stkEntryId;
	/*物料id*/
	private double applyNum;
	/*物料编码*/
	private String createDate;
	/*物料名称*/
	private int createUserId;
	/*调入仓库id*/
	private String createUserName;
	

	public StkTransferOutEntryApplyRecord() {
		super();
	}


	public Integer getId() {
		return id;
	}


	public Integer getStkEntryId() {
		return stkEntryId;
	}


	public double getApplyNum() {
		return applyNum;
	}


	public String getCreateDate() {
		return createDate;
	}


	public int getCreateUserId() {
		return createUserId;
	}


	public String getCreateUserName() {
		return createUserName;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public void setStkEntryId(Integer stkEntryId) {
		this.stkEntryId = stkEntryId;
	}


	public void setApplyNum(double applyNum) {
		this.applyNum = applyNum;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}


	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	
}
