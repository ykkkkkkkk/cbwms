package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 工序
 *
 * @author qxp 2018-11-09
 *
 */
public class Procedure implements Serializable {

	private Integer id;
	/* 工序编号 */
	private String procedureNumber;
	/* 工序名称 */
	private String procedureName;
	/* 工序状态（0禁用、1启用） */
	private Integer procedureState;

	private String createDate;
	/* 修改日期 */
	private String fModifyDate;

	/* 创建者id */
	private Integer createrId;
	/* 创建这名称 */
	private String createrName;

	/* 用于标志勾选状态 */
	private int checkFlag;

	/* 用于设置顺序号 */
	private String standard;
	/*用于设置单价*/
	private Double price;
	private ProcessflowEntry pfEntry;
	//工序条码
	private String barCode;

	// 临时字段，不存表
	private int materialId;
	private String mtlPriceTypeId; // 物料单价类型id
	private String mtlPriceTypeName; // 物料单价类型Name

	public Procedure() {
		super();
	}

	public Procedure(Integer id, String procedureNumber, String procedureName, Integer procedureState,
					 String createDate, String fModifyDate, Integer createrId, String createrName) {
		super();
		this.id = id;
		this.procedureNumber = procedureNumber;
		this.procedureName = procedureName;
		this.procedureState = procedureState;
		this.createDate = createDate;
		this.fModifyDate = fModifyDate;
		this.createrId = createrId;
		this.createrName = createrName;
	}


	public ProcessflowEntry getPfEntry() {
		return pfEntry;
	}

	public void setPfEntry(ProcessflowEntry pfEntry) {
		this.pfEntry = pfEntry;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(String procedureNumber) {
		this.procedureNumber = procedureNumber;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Integer getProcedureState() {
		return procedureState;
	}

	public void setProcedureState(Integer procedureState) {
		this.procedureState = procedureState;
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

	public Integer getCreaterId() {
		return createrId;
	}

	public void setCreaterId(Integer createrId) {
		this.createrId = createrId;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public int getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(int checkFlag) {
		this.checkFlag = checkFlag;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public int getMaterialId() {
		return materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getMtlPriceTypeId() {
		return mtlPriceTypeId;
	}

	public void setMtlPriceTypeId(String mtlPriceTypeId) {
		this.mtlPriceTypeId = mtlPriceTypeId;
	}

	public String getMtlPriceTypeName() {
		return mtlPriceTypeName;
	}

	public void setMtlPriceTypeName(String mtlPriceTypeName) {
		this.mtlPriceTypeName = mtlPriceTypeName;
	}
}
