package ykk.cb.com.cbwms.model;

import java.io.Serializable;
import java.util.List;

/**
 * 物料单价类型与工序的组合类，只是传到前台使用
 *
 * @author qxp 2018-11-09
 *
 */
public class MtlPriceTypeProcedureTemp implements Serializable {

	private String mtlPriceTypeId; // 物料单价类型id
	private String mtlPriceTypeName; // 物料单价类型Name
	private List<Procedure> listProcedure; // 工序列表


	public MtlPriceTypeProcedureTemp() {
		super();
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

	public List<Procedure> getListProcedure() {
		return listProcedure;
	}

	public void setListProcedure(List<Procedure> listProcedure) {
		this.listProcedure = listProcedure;
	}

}
