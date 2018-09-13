package ykk.cb.com.cbwms.model;


import java.io.Serializable;

/**
 * 质检要素实体类，作为质检项目的子类，记录质检项目具体的子项信息，如外观项下的颜色、包装等信息及其要求
 * @author Administrator
 *
 */
public class QualityElement implements Serializable{

	/*id*/
	private int id;

	/*要素代码*/
	private String qualityElementNumber;

	/*要素名称*/
	private String qualityElementName;

	/*检验项目id*/
	private int qualityItemId;

	/*检验项目*/
	private QualityItem qualityItem;

	/*创建人*/
	private String qualityElementCreater;

	/*创建时间*/
	private String qualityElementCreateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQualityElementNumber() {
		return qualityElementNumber;
	}

	public void setQualityElementNumber(String qualityElementNumber) {
		this.qualityElementNumber = qualityElementNumber;
	}

	public String getQualityElementName() {
		return qualityElementName;
	}

	public void setQualityElementName(String qualityElementName) {
		this.qualityElementName = qualityElementName;
	}

	public int getQualityItemId() {
		return qualityItemId;
	}

	public void setQualityItemId(int qualityItemId) {
		this.qualityItemId = qualityItemId;
	}

	public QualityItem getQualityItem() {
		return qualityItem;
	}

	public void setQualityItem(QualityItem qualityItem) {
		this.qualityItem = qualityItem;
	}

	public String getQualityElementCreater() {
		return qualityElementCreater;
	}

	public void setQualityElementCreater(String qualityElementCreater) {
		this.qualityElementCreater = qualityElementCreater;
	}

	public String getQualityElementCreateTime() {
		return qualityElementCreateTime;
	}

	public void setQualityElementCreateTime(String qualityElementCreateTime) {
		this.qualityElementCreateTime = qualityElementCreateTime;
	}

	public QualityElement() {
		super();
	}

	@Override
	public String toString() {
		return "QualityElement [id=" + id + ", qualityElementNumber=" + qualityElementNumber + ", qualityElementName="
				+ qualityElementName + ", qualityItemId=" + qualityItemId + ", qualityItem="
				+ qualityItem + ", qualityElementCreater=" + qualityElementCreater + ", qualityElementCreateTime="
				+ qualityElementCreateTime + "]";
	}

}
