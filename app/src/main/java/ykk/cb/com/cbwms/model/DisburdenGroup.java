package ykk.cb.com.cbwms.model;

import java.util.List;

/**
 * 装卸任务单组合实体
 * @author Administrator
 *
 */
public class DisburdenGroup {

	private DisburdenMission disMission; // 装卸单主表
	private List<DisburdenMissionEntry> disMEntryList; // 装卸单分录列表
	private List<DisburdenPerson> disPersonList; // 装卸工列表

	public DisburdenMission getDisMission() {
		return disMission;
	}

	public void setDisMission(DisburdenMission disMission) {
		this.disMission = disMission;
	}

	public List<DisburdenMissionEntry> getDisMEntryList() {
		return disMEntryList;
	}

	public void setDisMEntryList(List<DisburdenMissionEntry> disMEntryList) {
		this.disMEntryList = disMEntryList;
	}

	public List<DisburdenPerson> getDisPersonList() {
		return disPersonList;
	}

	public void setDisPersonList(List<DisburdenPerson> disPersonList) {
		this.disPersonList = disPersonList;
	}

}
