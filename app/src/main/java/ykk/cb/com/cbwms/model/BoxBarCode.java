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


	@Override
	public String toString() {
		return "BoxBarCode [id=" + id + ", boxId=" + boxId + ", barCode=" + barCode + ", status=" + status
				+ ", roughWeight=" + roughWeight + ", box=" + box + ", mtlBinningRecord=" + mtlBinningRecord + "]";
	}
	
}
