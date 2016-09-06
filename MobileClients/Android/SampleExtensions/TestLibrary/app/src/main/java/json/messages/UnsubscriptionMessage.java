package json.messages;

import json.enums.MessageTypes;

public class UnsubscriptionMessage extends BaseMessage {
	
	public String[] aggregationFunctions;
	
	public UnsubscriptionMessage() {
		super();
		this.type = MessageTypes.UNSUBSCRIPTION_MSG;
	}

	public UnsubscriptionMessage(String srcIP, String dstIP, String srcID, String dstID) {
		super(srcIP, dstIP, srcID, dstID);
		this.type = MessageTypes.UNSUBSCRIPTION_MSG;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[\n");
		sb.append(super.toString());
		
		sb.append("\taggregationFunctions = [ ");
		if(aggregationFunctions != null) {
			for(int i = 0; i < aggregationFunctions.length; i++) {
				sb.append(aggregationFunctions[i]);
				if(i < aggregationFunctions.length - 1) {
					sb.append(", ");
				}
			}
		}
		sb.append(" ]\n");		
		sb.append("]");
		
		return sb.toString();
	}

}
