package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 装卸任务参与人员实体类
 * @author Administrator
 *
 */
public class DisburdenPerson implements Serializable {

	/*id*/
	private int id;
	/*单据id*/
	private int dmBillId;
	/*装卸员工id*/
	private int dpStaffId;
	/*装卸员工*/
	private Staff dpStaff;

	public DisburdenPerson() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDpStaffId() {
		return dpStaffId;
	}

	public void setDpStaffId(int dpStaffId) {
		this.dpStaffId = dpStaffId;
	}

	public Staff getDpStaff() {
		return dpStaff;
	}

	public void setDpStaff(Staff dpStaff) {
		this.dpStaff = dpStaff;
	}

	public int getDmBillId() {
		return dmBillId;
	}

	public void setDmBillId(int dmBillId) {
		this.dmBillId = dmBillId;
	}

	@Override
	public String toString() {
		return "DisburdenPerson [id=" + id + ", dmBillId=" + dmBillId + ", dpStaffId=" + dpStaffId + ", dpStaff="
				+ dpStaff + "]";
	}


}
