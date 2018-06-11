package com.golaxy.entity;

import java.sql.Date;

public class WebVisitsCount {
	private int wzid;
	private int ptid;
	private String ym;
	private String wzmc;
	private Date date;
	private int pv;
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
	public int getPv() {
		return pv;
	}
	public void setPv(int pv) {
		this.pv = pv;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
