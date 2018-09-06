package ykk.cb.com.cbwms.model;

/**
 * 质检任务结果实体类
 * @author Administrator
 *
 */
public class QualityMissionEntryResult {

	/*id*/
	private Integer id;
	/*质检任务单单据体id*/
	private Integer qualityMissionEntryId;
	/*质检任务单单据体实体类*/
	private QualityMissionEntry qualityMissionEntry;
	/*质检方案明细id*/
	private Integer qualityPlanDetailId;
	/*质检方案明细*/
//	private QualityPlanDetail qualityPlanDetail;
	/*检验数量*/
	private double qualityCheckFqty;
	/*合格数量*/
	private double resultQualifiedFqty;
	/*不合格数量*/
	private double resultUnQualifiedFqty;
	/*检验结果 1、质检通过，2、不通过*/
	private Integer checkResult;
	/*不合格原因*/
	private String unQualifiedReason;
	/*备注*/
	private String resultRemark;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getQualityMissionEntryId() {
		return qualityMissionEntryId;
	}
	public void setQualityMissionEntryId(Integer qualityMissionEntryId) {
		this.qualityMissionEntryId = qualityMissionEntryId;
	}
	public QualityMissionEntry getQualityMissionEntry() {
		return qualityMissionEntry;
	}
	public void setQualityMissionEntry(QualityMissionEntry qualityMissionEntry) {
		this.qualityMissionEntry = qualityMissionEntry;
	}
	public Integer getQualityPlanDetailId() {
		return qualityPlanDetailId;
	}
	public void setQualityPlanDetailId(Integer qualityPlanDetailId) {
		this.qualityPlanDetailId = qualityPlanDetailId;
	}
//	public QualityPlanDetail getQualityPlanDetail() {
//		return qualityPlanDetail;
//	}
//	public void setQualityPlanDetail(QualityPlanDetail qualityPlanDetail) {
//		this.qualityPlanDetail = qualityPlanDetail;
//	}
	public double getQualityCheckFqty() {
		return qualityCheckFqty;
	}
	public void setQualityCheckFqty(double qualityCheckFqty) {
		this.qualityCheckFqty = qualityCheckFqty;
	}
	public double getResultQualifiedFqty() {
		return resultQualifiedFqty;
	}
	public void setResultQualifiedFqty(double resultQualifiedFqty) {
		this.resultQualifiedFqty = resultQualifiedFqty;
	}
	public double getResultUnQualifiedFqty() {
		return resultUnQualifiedFqty;
	}
	public void setResultUnQualifiedFqty(double resultUnQualifiedFqty) {
		this.resultUnQualifiedFqty = resultUnQualifiedFqty;
	}
	public Integer getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(Integer checkResult) {
		this.checkResult = checkResult;
	}
	public String getUnQualifiedReason() {
		return unQualifiedReason;
	}
	public void setUnQualifiedReason(String unQualifiedReason) {
		this.unQualifiedReason = unQualifiedReason;
	}
	public String getResultRemark() {
		return resultRemark;
	}
	public void setResultRemark(String resultRemark) {
		this.resultRemark = resultRemark;
	}

	public QualityMissionEntryResult() {
		super();
	}


}
