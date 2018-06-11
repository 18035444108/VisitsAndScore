package com.golaxy.entity;

public class QrjrwzEntity {
	private int wzid;
	private int ptid;
	private String ym;
	private String wzmc;
	public int getWzid() {
		return wzid;
	}
	public void setWzid(int wzid) {
		this.wzid = wzid;
	}
	public int getPtid() {
		return ptid;
	}
	public void setPtid(int ptid) {
		this.ptid = ptid;
	}
	public String getYm() {
		return ym;
	}
	public void setYm(String ym) {
		this.ym = ym;
	}
	public String getWzmc() {
		return wzmc;
	}
	public void setWzmc(String wzmc) {
		this.wzmc = wzmc;
	}
	
	@Override
	public String toString() {
		return "QrjrwzEntity [wzid=" + wzid + ", ptid=" + ptid + ", ym=" + ym + ", wzmc=" + wzmc + "]";
	}
	
}
