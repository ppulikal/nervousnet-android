package json.messages;

import json.enums.MessageTypes;

public abstract class BaseMessage {
	
	protected MessageTypes type;
	protected String srcIP;		// this is IP:port string
	protected String dstIP;
	protected String srcID;
	protected String dstID;
	
	public BaseMessage() {
		
	}
	
	public BaseMessage(String srcIP, String dstIP, String srcID, String dstID) {
		this.srcID = srcID;
		this.dstID = dstID;
		this.srcIP = srcIP;
		this.dstIP = dstIP;
	}
	
	public MessageTypes getType() {
		return this.type;
	}
	
	public String getSrcIP() {
		return this.srcIP;
	}
	
	public void setSrcIP(String srcIP) {
		this.srcIP = srcIP;
	}
	
	public String getDstIP() {
		return this.dstIP;
	}
	
	public void setDstIP(String dstIP) {
		this.dstIP = dstIP;
	}
	
	public String getSrcID() {
		return this.srcID;
	}
	
	public void setSrcID(String srcID) {
		this.srcID = srcID;
	}
	
	public String getDstID() {
		return this.dstID;
	}
	
	public void setDstID(String dstID) {
		this.dstID = dstID;
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\ttype = " + type + "\n");
		sb.append("\tIP = " + srcIP + " to " + dstIP + "\n");
		sb.append("\tID = " + srcID + " to " + dstID + "\n");
		return sb.toString();
	}

}
