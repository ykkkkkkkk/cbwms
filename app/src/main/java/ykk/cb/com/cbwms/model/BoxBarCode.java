package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

import ykk.cb.com.cbwms.model.MaterialBinningRecord;

/**
 * 包装物条码类，用于记录对每个包装物使用流水号生成唯一条码
 * @author Administrator
 *
 */
public class BoxBarCode implements Serializable {
	/*id*/
	private int id;
	/*包装物id*/
	private int boxId;
	/*包装物生成的唯一码*/
	private String barCode;
	/**箱子的状态
	 * 0代表创建
	 * 1代表开箱
	 * 2代表封箱
	 * */
	private int status;
	/*箱子净重*/
	private double roughWeight;
	/*包装物*/
	private Box box;
	/* pda扫描箱码查询箱子里的物料列表  */
	public List<MaterialBinningRecord> mtlBinningRecord;

	// 临时数据, 不存表
	private int combineSalOrderId; // 拼单主表id
	private int combineSalOrderRow; // 拼单子表行数
	private double combineSalOrderFqtys; // 拼单子表总数量
	private boolean outStockFlag; // true：箱码已出库，false：未出库

	public BoxBarCode() {
		super();
	}


	public int getId() {
		return id;
	}


	public int getBoxId() {
		return boxId;
	}


	public String getBarCode() {
		return barCode;
	}


	public int getStatus() {
		return status;
	}


	public double getRoughWeight() {
		return roughWeight;
	}


	public Box getBox() {
		return box;
	}


	public List<MaterialBinningRecord> getMtlBinningRecord() {
		return mtlBinningRecord;
	}


	public void setId(int id) {
		this.id = id;
	}


	public void setBoxId(int boxId) {
		this.boxId = boxId;
	}


	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public void setRoughWeight(double roughWeight) {
		this.roughWeight = roughWeight;
	}


	public void setBox(Box box) {
		this.box = box;
	}


	public void setMtlBinningRecord(List<MaterialBinningRecord> mtlBinningRecord) {
		this.mtlBinningRecord = mtlBinningRecord;
	}

	public int getCombineSalOrderId() {
		return combineSalOrderId;
	}

	public void setCombineSalOrderId(int combineSalOrderId) {
		this.combineSalOrderId = combineSalOrderId;
	}

	public int getCombineSalOrderRow() {
		return combineSalOrderRow;
	}

	public double getCombineSalOrderFqtys() {
		return combineSalOrderFqtys;
	}

	public void setCombineSalOrderRow(int combineSalOrderRow) {
		this.combineSalOrderRow = combineSalOrderRow;
	}

	public void setCombineSalOrderFqtys(double combineSalOrderFqtys) {
		this.combineSalOrderFqtys = combineSalOrderFqtys;
	}

	public boolean isOutStockFlag() {
		return outStockFlag;
	}


	public void setOutStockFlag(boolean outStockFlag) {
		this.outStockFlag = outStockFlag;
	}
}
