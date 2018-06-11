package com.golaxy.entity;

import java.sql.Date;

public class WebScore {
	private int wzid;
	private String ym;
	private String wzmc;
	private Date date;
	private Double  score;
	private long scoreRanking;
	private String updateTime;
	public int getWzid() {
		return wzid;
	}
	public void setWzid(int wzid) {
		this.wzid = wzid;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public long getScoreRanking() {
		return scoreRanking;
	}
	public void setScoreRanking(long scoreRanking) {
		this.scoreRanking = scoreRanking;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
