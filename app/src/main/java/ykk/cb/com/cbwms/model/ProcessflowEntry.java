package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @Description:工艺体
 *
 * @author qxp 2018年11月10日 上午11:14:24
 */
public class ProcessflowEntry implements Serializable {

	private Integer id;
	/* 工序id */
	private Integer procedureId;
	/* 工艺路线id */
	private Integer processflowId;
	/* 工艺体编号 */
	private String processflowElementNumber;
	/* 图片地址 */
	private String imgUrl;
	/* 工序类 */
	private Procedure procedure;

	/* 工艺头 */
	private Processflow processflow;

	// 临时字段，不存表
	private String strDetail;

	public ProcessflowEntry() {
		super();
	}

	public ProcessflowEntry(Integer id, Integer procedureId, Integer processflowId, String processflowElementNumber,
							String imgUrl, Procedure procedure, Processflow processflow) {
		super();
		this.id = id;
		this.procedureId = procedureId;
		this.processflowId = processflowId;
		this.processflowElementNumber = processflowElementNumber;
		this.imgUrl = imgUrl;
		this.procedure = procedure;
		this.processflow = processflow;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public Processflow getProcessflow() {
		return processflow;
	}

	public void setProcessflow(Processflow processflow) {
		this.processflow = processflow;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Integer procedureId) {
		this.procedureId = procedureId;
	}

	public Integer getProcessflowId() {
		return processflowId;
	}

	public void setProcessflowId(Integer processflowId) {
		this.processflowId = processflowId;
	}

	public String getProcessflowElementNumber() {
		return processflowElementNumber;
	}

	public void setProcessflowElementNumber(String processflowElementNumber) {
		this.processflowElementNumber = processflowElementNumber;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getStrDetail() {
		return strDetail;
	}

	public void setStrDetail(String strDetail) {
		this.strDetail = strDetail;
	}

	@Override
	public String toString() {
		return "ProcessflowEntry [id=" + id + ", procedureId=" + procedureId + ", processflowId=" + processflowId
				+ ", processflowElementNumber=" + processflowElementNumber + ", imgUrl=" + imgUrl + ", procedure="
				+ procedure + ", processflow=" + processflow + "]";
	}


}
