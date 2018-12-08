package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 物料装箱记录类子表
 * @author Administrator
 *
 */
public class MaterialBinningRecordEntry implements Serializable {

	/*id*/
	private int id;
	private int mbrId;
	private String barcode; // 条码号
	private String createDate;
	/* 创建人id  */
	private int	createUserId;
	/* 创建人名称  */
	private String createUserName;

	public MaterialBinningRecordEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public int getMbrId() {
		return mbrId;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setMbrId(int mbrId) {
		this.mbrId = mbrId;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

}
