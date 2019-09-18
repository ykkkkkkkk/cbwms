package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 物料类别与工艺流程组合( 只显示部分字段 )
 */
public class MaterialProcessflowSon implements Serializable {
	private int processflowId; // 工艺路线
	private String reportType; // 工序汇报类型 A：按位置汇报 B：按套汇报*/
	private int processId; // 工序id

	public MaterialProcessflowSon() {
		super();
	}

	public int getProcessflowId() {
		return processflowId;
	}

	public void setProcessflowId(int processflowId) {
		this.processflowId = processflowId;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}


}
