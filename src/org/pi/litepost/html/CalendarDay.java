package org.pi.litepost.html;

public class CalendarDay {
	boolean hasEvent;
	boolean isOffset;
	int num;
	String link; 
	
	public CalendarDay(boolean hasEvent, boolean isOffset, int num, String link) {
		this.hasEvent = hasEvent;
		this.isOffset = isOffset;
		this.num = num;
		this.link = link;
	}
	
	public boolean hasEvent(){
		return hasEvent; 
	}
	
	public boolean isOffset() {
		return isOffset;
	}
	
	public int getNum() {
		return num;
	}
	
	public String getLink() {
		return link;
	}
}
