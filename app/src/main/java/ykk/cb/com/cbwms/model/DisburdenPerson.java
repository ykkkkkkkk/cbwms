package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 装卸任务参与人员实体类
 * @author Administrator
 *
 */
public class DisburdenPerson implements Serializable {

	/*id*/
	private Integer id;
	/*单据id*/
	private Integer dmBillId;
	/*装卸员工id*/
	private Integer dpStaffId;
	/*装卸员工*/
	private Staff dpStaff;

	public DisburdenPerson() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDpStaffId() {
		return dpStaffId;
	}

	public void setDpStaffId(Integer dpStaffId) {
		this.dpStaffId = dpStaffId;
	}

	public Staff getDpStaff() {
		return dpStaff;
	}

	public void setDpStaff(Staff dpStaff) {
		this.dpStaff = dpStaff;
	}

	public Integer getDmBillId() {
		return dmBillId;
	}

	public void setDmBillId(Integer dmBillId) {
		this.dmBillId = dmBillId;
	}

	@Override
	public String toString() {
		return "DisburdenPerson [id=" + id + ", dmBillId=" + dmBillId + ", dpStaffId=" + dpStaffId + ", dpStaff="
				+ dpStaff + "]";
	}


}
