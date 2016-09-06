package json.messages;

import json.enums.MessageTypes;
import json.enums.SubscriptionPolicy;

public class SubscriptionMessage extends BaseMessage {
	
	public SubscriptionPolicy subscriptionPolicy;			// policy - ON_UPDATE or PERIODICALLY
	public int[] millisTime;					// time in millis for periodically policy for every aggregation function
	public String[] aggregationFunctions;		// aggregation functions client subscribes for
	
	public SubscriptionMessage() {
		super();
		this.type = MessageTypes.SUBSCRIPTION_MSG;
	}
	
	public SubscriptionMessage(String srcIP, String dstIP, String srcID, String dstID) {
		super(srcIP, dstIP, srcID, dstID);
		this.type = MessageTypes.SUBSCRIPTION_MSG;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[\n");
		sb.append(super.toString());
		
		sb.append("\tsubscription Policy = " + subscriptionPolicy + "\n");
		sb.append("\taggregationFunctions = [ ");
		if(aggregationFunctions != null) {
			for(int i = 0; i < aggregationFunctions.length; i++) {
				sb.append(aggregationFunctions[i]);
				if(millisTime != null) {
					sb.append(" @ " + millisTime[i] + "ms");
				}
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
