package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * @Description:计价工资表
 *
 * @author qxp 2018年11月23日 下午2:12:29
 */
public class ValuationPayroll implements Serializable {

	private int id;
	/* 班次id */
	private int schedulTeamId;
	/* 员工id */
	private int staffId;
	/* 部门id */
	private int deptId;
	/* 物料id */
	private int fMaterialId;
	/* 工序id */
	private int procedureId;
	/* 总数量 */
	private int totalNumber;
	/* 计件类型id */
	private int valuationTypeId;
	/* 已分配数量 */
	private int assign;
	/* 未分配数量 */
	private int notAssign;
	/* 创建时间 */
	private String createDate;
	/* 创建人id */
	private int createrId;
	/* 创建人名称 */
	private String createrName;
	/* 物料产品类 */
	private Material material;
	/* 班次类 */
	private SchedulTeam shcedulTeam;
	/* 员工类 */
	private Staff staff;
	/* 部门类 */
	private Department department;
	/* 工序类 */
	private Procedure procedure;
	/* 计件类型 */
	private ValuationType valuationType;
	/* 单据状态，0为作废。默认为1正常 */
	private int valState;
	/* 单据来源。0WMS手动创建，1扫码来源创建 */
	private int createWay;

	public ValuationPayroll() {
		super();
	}

	public int getId() {
		return id;
	}

	public ValuationPayroll setId(int id) {
		this.id = id;
		return this;
	}

	public int getSchedulTeamId() {
		return schedulTeamId;
	}

	public ValuationPayroll setSchedulTeamId(int schedulTeamId) {
		this.schedulTeamId = schedulTeamId;
		return this;
	}

	public int getStaffId() {
		return staffId;
	}

	public ValuationPayroll setStaffId(int staffId) {
		this.staffId = staffId;
		return this;
	}

	public int getDeptId() {
		return deptId;
	}

	public ValuationPayroll setDeptId(int deptId) {
		this.deptId = deptId;
		return this;
	}

	public int getfMaterialId() {
		return fMaterialId;
	}

	public ValuationPayroll setfMaterialId(int fMaterialId) {
		this.fMaterialId = fMaterialId;
		return this;
	}

	public int getProcedureId() {
		return procedureId;
	}

	public ValuationPayroll setProcedureId(int procedureId) {
		this.procedureId = procedureId;
		return this;
	}

	public int getTotalNumber() {
		return totalNumber;
	}

	public ValuationPayroll setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
		return this;
	}

	public int getValuationTypeId() {
		return valuationTypeId;
	}

	public ValuationPayroll setValuationTypeId(int valuationTypeId) {
		this.valuationTypeId = valuationTypeId;
		return this;
	}

	public int getAssign() {
		return assign;
	}

	public ValuationPayroll setAssign(int assign) {
		this.assign = assign;
		return this;
	}

	public int getNotAssign() {
		return notAssign;
	}

	public ValuationPayroll setNotAssign(int notAssign) {
		this.notAssign = notAssign;
		return this;
	}

	public String getCreateDate() {
		return createDate;
	}

	public ValuationPayroll setCreateDate(String createDate) {
		this.createDate = createDate;
		return this;
	}

	public int getCreaterId() {
		return createrId;
	}

	public ValuationPayroll setCreaterId(int createrId) {
		this.createrId = createrId;
		return this;
	}

	public String getCreaterName() {
		return createrName;
	}

	public ValuationPayroll setCreaterName(String createrName) {
		this.createrName = createrName;
		return this;
	}

	public Material getMaterial() {
		return material;
	}

	public ValuationPayroll setMaterial(Material material) {
		this.material = material;
		return this;
	}

	public SchedulTeam getShcedulTeam() {
		return shcedulTeam;
	}

	public ValuationPayroll setShcedulTeam(SchedulTeam shcedulTeam) {
		this.shcedulTeam = shcedulTeam;
		return this;
	}

	public Staff getStaff() {
		return staff;
	}

	public ValuationPayroll setStaff(Staff staff) {
		this.staff = staff;
		return this;
	}

	public Department getDepartment() {
		return department;
	}

	public ValuationPayroll setDepartment(Department department) {
		this.department = department;
		return this;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public ValuationPayroll setProcedure(Procedure procedure) {
		this.procedure = procedure;
		return this;
	}

	public ValuationType getValuationType() {
		return valuationType;
	}

	public ValuationPayroll setValuationType(ValuationType valuationType) {
		this.valuationType = valuationType;
		return this;
	}

	public int getValState() {
		return valState;
	}

	public ValuationPayroll setValState(int valState) {
		this.valState = valState;
		return this;
	}

	public int getCreateWay() {
		return createWay;
	}

	public ValuationPayroll setCreateWay(int createWay) {
		this.createWay = createWay;
		return this;
	}
}