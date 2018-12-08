package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @Description：工艺路线
 *
 * @author qxp 2018年11月10日 上午9:58:35
 */
public class Processflow implements Serializable {

	private Integer id;

	/* 工艺路线编号 */
	private String processflowNumber;
	/* 工艺路线名称 */
	private String processflowName;
	/* 工艺路线状态（0禁用，1启用） */
	private Integer processflowState;
	/* 工艺路线创建时间 */
	private String createDate;
	/* 工艺路线修改时间 */
	private String fModifyDate;

	/* 创建者id */
	private Integer createrId;
	/* 创建这名称 */
	private String createrName;

	public Processflow() {
		super();
	}

	public Processflow(Integer id, String processflowNumber, String processflowName, Integer processflowState,
					   String createDate, String fModifyDate, Integer createrId, String createrName) {
		super();
		this.id = id;
		this.processflowNumber = processflowNumber;
		this.processflowName = processflowName;
		this.processflowState = processflowState;
		this.createDate = createDate;
		this.fModifyDate = fModifyDate;
		this.createrId = createrId;
		this.createrName = createrName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProcessflowNumber() {
		return processflowNumber;
	}

	public void setProcessflowNumber(String processflowNumber) {
		this.processflowNumber = processflowNumber;
	}

	public String getProcessflowName() {
		return processflowName;
	}

	public void setProcessflowName(String processflowName) {
		this.processflowName = processflowName;
	}

	public Integer getProcessflowState() {
		return processflowState;
	}

	public void setProcessflowState(Integer processflowState) {
		this.processflowState = processflowState;
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

	@Override
	public String toString() {
		return "Processflow [id=" + id + ", processflowNumber=" + processflowNumber + ", processflowName="
				+ processflowName + ", processflowState=" + processflowState + ", createDate=" + createDate
				+ ", fModifyDate=" + fModifyDate + ", createrId=" + createrId + ", createrName=" + createrName + "]";
	}

}
