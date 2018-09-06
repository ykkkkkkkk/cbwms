package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 质检方案实体类，用于保存方案代码名称等信息
 * @author Administrator
 *
 */
public class QualityPlan implements Serializable {

	/*id*/
	private Integer id;

	/*方案代码*/
	private String qualityPlanNumber;

	/*方案名称*/
	private String qualityPlanName;

	/*严苛程度 1代表宽松、2代表标准、3代表严格*/
	private Integer qualityPlanLevel;

	/*状态 1代表启用、2代表禁用*/
	private Integer qualityPlanStatus;

	/*抽样类型 1代表抽检、2代表全检*/
	private Integer qualityPlanStyle;

	/*创建人*/
	private String qualityPlanCreater;

	/*创建时间*/
	private String qualityPlanCreateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQualityPlanNumber() {
		return qualityPlanNumber;
	}

	public void setQualityPlanNumber(String qualityPlanNumber) {
		this.qualityPlanNumber = qualityPlanNumber;
	}

	public String getQualityPlanName() {
		return qualityPlanName;
	}

	public void setQualityPlanName(String qualityPlanName) {
		this.qualityPlanName = qualityPlanName;
	}

	public Integer getQualityPlanLevel() {
		return qualityPlanLevel;
	}

	public void setQualityPlanLevel(Integer qualityPlanLevel) {
		this.qualityPlanLevel = qualityPlanLevel;
	}

	public Integer getQualityPlanStatus() {
		return qualityPlanStatus;
	}

	public void setQualityPlanStatus(Integer qualityPlanStatus) {
		this.qualityPlanStatus = qualityPlanStatus;
	}

	public Integer getQualityPlanStyle() {
		return qualityPlanStyle;
	}

	public void setQualityPlanStyle(Integer qualityPlanStyle) {
		this.qualityPlanStyle = qualityPlanStyle;
	}

	public String getQualityPlanCreater() {
		return qualityPlanCreater;
	}

	public void setQualityPlanCreater(String qualityPlanCreater) {
		this.qualityPlanCreater = qualityPlanCreater;
	}

	public String getQualityPlanCreateTime() {
		return qualityPlanCreateTime;
	}

	public void setQualityPlanCreateTime(String qualityPlanCreateTime) {
		this.qualityPlanCreateTime = qualityPlanCreateTime;
	}

	public QualityPlan() {
		super();
	}

	@Override
	public String toString() {
		return "QualityPlan [id=" + id + ", qualityPlanNumber=" + qualityPlanNumber + ", qualityPlanName="
				+ qualityPlanName + ", qualityPlanLevel=" + qualityPlanLevel + ", qualityPlanStatus="
				+ qualityPlanStatus + ", qualityPlanStyle=" + qualityPlanStyle + ", qualityPlanCreater="
				+ qualityPlanCreater + ", qualityPlanCreateTime=" + qualityPlanCreateTime + "]";
	}
	
}
