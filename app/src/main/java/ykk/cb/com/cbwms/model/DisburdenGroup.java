package ykk.cb.com.cbwms.model;

import java.util.List;

/**
 * 装卸任务单组合实体
 * @author Administrator
 *
 */
public class DisburdenGroup {

	private DisburdenMission dis; // 装卸单主表
	private List<DisburdenMissionEntry> disEntryList; // 装卸单分录列表
	private List<DisburdenPerson> disPersonList; // 装卸工列表

	public DisburdenGroup() {
		super();
	}

	public DisburdenMission getDis() {
		return dis;
	}

	public void setDis(DisburdenMission dis) {
		this.dis = dis;
	}

	public List<DisburdenMissionEntry> getDisEntryList() {
		return disEntryList;
	}

	public void setDisEntryList(List<DisburdenMissionEntry> disEntryList) {
		this.disEntryList = disEntryList;
	}

	public List<DisburdenPerson> getDisPersonList() {
		return disPersonList;
	}

	public void setDisPersonList(List<DisburdenPerson> disPersonList) {
		this.disPersonList = disPersonList;
	}

}
