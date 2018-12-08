package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @Description:计价工资类型
 *
 * @author qxp 2018年11月19日 上午11:19:08
 */
public class ValuationType implements Serializable {

	private int id;
	/* 计价工资类型标记：1个人几件，2集体计价，3计时几件 */
	private String signType;
	/* 描述 */
	private String description;

	private String createDate;

	private String fModifyDate;

	private int createrId;

	private String createrName;

	public ValuationType() {
		super();
	}

	public ValuationType(int id, String signType, String description, String createDate, String fModifyDate,
						 int createrId, String createrName) {
		super();
		this.id = id;
		this.signType = signType;
		this.description = description;
		this.createDate = createDate;
		this.fModifyDate = fModifyDate;
		this.createrId = createrId;
		this.createrName = createrName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getfModifyDate() {
		return fModifyDate;
	}

	public void setfModifyDate(String fModifyDate) {
		this.fModifyDate = fModifyDate;
	}

	public int getCreaterId() {
		return createrId;
	}

	public void setCreaterId(int createrId) {
		this.createrId = createrId;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	@Override
	public String toString() {
		return "ValuationType [id=" + id + ", signType=" + signType + ", description=" + description + ", createDate="
				+ createDate + ", fModifyDate=" + fModifyDate + ", createrId=" + createrId + ", createrName="
				+ createrName + "]";
	}

}
