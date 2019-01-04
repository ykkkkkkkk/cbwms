package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * k3辅助资料类（物流公司）
 * @author Administrator
 *
 */
public class ExpressCompany implements Serializable {

	private Integer id; // id
	private String categoryName; // 类别名称
	private String uniquenessId; // 唯一id
	private String expressName; // 物流公司名称
	private String expressNumber; // 物流公司编码
	private String description;//备注

	public ExpressCompany() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getUniquenessId() {
		return uniquenessId;
	}

	public void setUniquenessId(String uniquenessId) {
		this.uniquenessId = uniquenessId;
	}

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public String getExpressNumber() {
		return expressNumber;
	}

	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ExpressCompany [id=" + id + ", categoryName=" + categoryName + ", uniquenessId=" + uniquenessId
				+ ", expressName=" + expressName + ", expressNumber=" + expressNumber + ", description=" + description
				+ "]";
	}

}