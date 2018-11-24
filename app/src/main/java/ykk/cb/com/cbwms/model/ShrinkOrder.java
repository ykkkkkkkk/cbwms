package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 保存订单的部分信息	ykk
 * @author Administrator
 *
 */
public class ShrinkOrder implements Serializable {
	private int fId; // 单据id,
	private String fbillno; // 单据编号,
	private int mtlId; // 物料id
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private double fqty; // 数量
	private int entryId;

	public ShrinkOrder() {
		super();
	}

	public int getfId() {
		return fId;
	}

	public String getFbillno() {
		return fbillno;
	}

	public int getMtlId() {
		return mtlId;
	}

	public String getMtlFnumber() {
		return mtlFnumber;
	}

	public String getMtlFname() {
		return mtlFname;
	}

	public double getFqty() {
		return fqty;
	}

	public int getEntryId() {
		return entryId;
	}

	public void setfId(int fId) {
		this.fId = fId;
	}

	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public void setMtlFnumber(String mtlFnumber) {
		this.mtlFnumber = mtlFnumber;
	}

	public void setMtlFname(String mtlFname) {
		this.mtlFname = mtlFname;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}



}
