package edu.rosehulman.weightlosstracker;

import java.util.Date;



public class SleepTime {
	private Date fallAsleepDate;
	private int hoursSlept;
	private int minutesSlept;
	private int secondsSlept;
	
	public SleepTime() {
		this.fallAsleepDate = new Date();
		this.hoursSlept = 0;
		this.minutesSlept = 0;
		this.secondsSlept = 0;
	}
	
	public SleepTime(Date date, int hours, int minutes, int seconds) {
		this.fallAsleepDate = date;
		this.hoursSlept = hours;
		this.minutesSlept = minutes;
		this.secondsSlept = seconds;
	}
	
	public void setHours(int hours) {
		this.hoursSlept = hours;
	}
	
	public int getHours() {
		return this.hoursSlept;
	}
	
	public void setMinutes(int minutes) {
		this.minutesSlept = minutes;
	}
	
	public int getMinutes() {
		return this.minutesSlept;
	}
	
	public void setSeconds(int seconds) {
		this.secondsSlept = seconds;
	}
	
	public int getSeconds() {
		return this.secondsSlept;
	}
	
	public void setDate(Date date) {
		this.fallAsleepDate = date;
	}
	
	public Date getDate() {
		return this.fallAsleepDate;
	}

}
