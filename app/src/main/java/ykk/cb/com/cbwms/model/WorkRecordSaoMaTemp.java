package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

/**
 * 扫码报工主表，临时表
 * @author Administrator
 *
 */
public class WorkRecordSaoMaTemp implements Serializable {
	private WorkRecordSaoMa workRecordSaoMa;
	private WorkRecordSaoMaEntry1 workRecordSaoMaEntry1;
	private WorkRecordSaoMaEntry2 workRecordSaoMaEntry2;
	private List<WorkRecordSaoMaEntry2> listEntry2;
	private int topProcedureId; // 上个工序id
	private int topProcessflowElementNumber; // 上个工序，工艺路线对应的顺序号
	private String topReportType; // 上个工序，汇报类型
	private List<MaterialProcessflowSon> listAutoReportProcess; // 上个工序，自动汇报的工序列表

	private String strLocaltionQty; // 拼接的位置和数量
	private boolean checkRow; // 是否选中行


	public WorkRecordSaoMaTemp() {
		super();
	}


	public WorkRecordSaoMa getWorkRecordSaoMa() {
		return workRecordSaoMa;
	}


	public void setWorkRecordSaoMa(WorkRecordSaoMa workRecordSaoMa) {
		this.workRecordSaoMa = workRecordSaoMa;
	}


	public WorkRecordSaoMaEntry1 getWorkRecordSaoMaEntry1() {
		return workRecordSaoMaEntry1;
	}


	public void setWorkRecordSaoMaEntry1(WorkRecordSaoMaEntry1 workRecordSaoMaEntry1) {
		this.workRecordSaoMaEntry1 = workRecordSaoMaEntry1;
	}


	public WorkRecordSaoMaEntry2 getWorkRecordSaoMaEntry2() {
		return workRecordSaoMaEntry2;
	}


	public void setWorkRecordSaoMaEntry2(WorkRecordSaoMaEntry2 workRecordSaoMaEntry2) {
		this.workRecordSaoMaEntry2 = workRecordSaoMaEntry2;
	}


	public List<WorkRecordSaoMaEntry2> getListEntry2() {
		return listEntry2;
	}


	public void setListEntry2(List<WorkRecordSaoMaEntry2> listEntry2) {
		this.listEntry2 = listEntry2;
	}


	public int getTopProcedureId() {
		return topProcedureId;
	}


	public void setTopProcedureId(int topProcedureId) {
		this.topProcedureId = topProcedureId;
	}


	public int getTopProcessflowElementNumber() {
		return topProcessflowElementNumber;
	}


	public void setTopProcessflowElementNumber(int topProcessflowElementNumber) {
		this.topProcessflowElementNumber = topProcessflowElementNumber;
	}


	public String getTopReportType() {
		return topReportType;
	}


	public void setTopReportType(String topReportType) {
		this.topReportType = topReportType;
	}


	public List<MaterialProcessflowSon> getListAutoReportProcess() {
		return listAutoReportProcess;
	}


	public void setListAutoReportProcess(List<MaterialProcessflowSon> listAutoReportProcess) {
		this.listAutoReportProcess = listAutoReportProcess;
	}


	public String getStrLocaltionQty() {
		return strLocaltionQty;
	}


	public void setStrLocaltionQty(String strLocaltionQty) {
		this.strLocaltionQty = strLocaltionQty;
	}


	public boolean isCheckRow() {
		return checkRow;
	}


	public void setCheckRow(boolean checkRow) {
		this.checkRow = checkRow;
	}


}