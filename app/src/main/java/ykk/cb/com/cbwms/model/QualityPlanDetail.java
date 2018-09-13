package ykk.cb.com.cbwms.model;


/**
 * 质检方案明细实体类用于保存质检项目和质检要素的组合信息
 *
 * @author Administrator
 */
public class QualityPlanDetail {

    /*id*/
    private int id;

    /*质检方案id*/
    private int qualityPlanId;

    /*质检方案*/
    private QualityPlan qualityPlan;

    /*质检项目id*/
    private int qualityItemId;

    /*质检项目*/
    private QualityItem qualityItem;

    /*质检要素id*/
    private int qualityElementId;

    /*质检要素*/
    private QualityElement qualityElement;
    private int qualityMissionEntryId;
    /*质检项目结果 */
    private int qualityMissionEntryResultId;
    private QualityMissionEntryResult qualityMissionEntryResult;

    /*值标准*/
    private String standard;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQualityPlanId() {
        return qualityPlanId;
    }

    public void setQualityPlanId(int qualityPlanId) {
        this.qualityPlanId = qualityPlanId;
    }

    public QualityPlan getQualityPlan() {
        return qualityPlan;
    }

    public void setQualityPlan(QualityPlan qualityPlan) {
        this.qualityPlan = qualityPlan;
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

    public int getQualityElementId() {
        return qualityElementId;
    }

    public void setQualityElementId(int qualityElementId) {
        this.qualityElementId = qualityElementId;
    }

    public QualityElement getQualityElement() {
        return qualityElement;
    }

    public void setQualityElement(QualityElement qualityElement) {
        this.qualityElement = qualityElement;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public QualityMissionEntryResult getQualityMissionEntryResult() {
        return qualityMissionEntryResult;
    }

    public void setQualityMissionEntryResult(QualityMissionEntryResult qualityMissionEntryResult) {
        this.qualityMissionEntryResult = qualityMissionEntryResult;
    }

    public int getQualityMissionEntryId() {
        return qualityMissionEntryId;
    }

    public int getQualityMissionEntryResultId() {
        return qualityMissionEntryResultId;
    }

    public void setQualityMissionEntryId(int qualityMissionEntryId) {
        this.qualityMissionEntryId = qualityMissionEntryId;
    }

    public void setQualityMissionEntryResultId(int qualityMissionEntryResultId) {
        this.qualityMissionEntryResultId = qualityMissionEntryResultId;
    }

    public QualityPlanDetail() {
        super();
    }

}
