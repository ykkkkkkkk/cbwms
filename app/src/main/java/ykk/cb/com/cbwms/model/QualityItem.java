package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 质检项目实体类，用于记录质量检验的项目分组，如外观、耐疲劳度等
 * @author Administrator
 *
 */
public class QualityItem implements Serializable{

	/*id*/
	private int id;

	/*项目代码*/
	private String qualityItemNumber;

	/*项目名称*/
	private String qualityItemName;

	/*创建人*/
	private String qualityItemCreater;

	/*创建时间*/
	private String qualityItemCreateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQualityItemNumber() {
		return qualityItemNumber;
	}

	public void setQualityItemNumber(String qualityItemNumber) {
		this.qualityItemNumber = qualityItemNumber;
	}

	public String getQualityItemName() {
		return qualityItemName;
	}

	public void setQualityItemName(String qualityItemName) {
		this.qualityItemName = qualityItemName;
	}

	public String getQualityItemCreater() {
		return qualityItemCreater;
	}

	public void setQualityItemCreater(String qualityItemCreater) {
		this.qualityItemCreater = qualityItemCreater;
	}

	public String getQualityItemCreateTime() {
		return qualityItemCreateTime;
	}

	public void setQualityItemCreateTime(String qualityItemCreateTime) {
		this.qualityItemCreateTime = qualityItemCreateTime;
	}

	public QualityItem() {
		super();
	}

	@Override
	public String toString() {
		return "QualityItem [id=" + id + ", qualityItemNumber=" + qualityItemNumber + ", qualityItemName="
				+ qualityItemName + ", qualityItemCreater=" + qualityItemCreater + ", qualityItemCreateTime="
				+ qualityItemCreateTime + "]";
	}

}
