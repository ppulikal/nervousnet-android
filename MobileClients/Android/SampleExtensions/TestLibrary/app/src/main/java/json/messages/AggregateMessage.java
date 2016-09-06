package json.messages;

import json.enums.MessageTypes;

/**
 * Represents a message for returning value of one aggregation function
 * back to the client.
 * 
 * @author nikolijo
 *
 */
public class AggregateMessage extends BaseMessage {

	public String[] aggregationFunction;
	public double[] value;
	
	public AggregateMessage(String srcIP, String dstIP, String srcID, String dstID) {
		super(srcIP, dstIP, srcID, dstID);
		this.type = MessageTypes.AGGREGATE_MSG;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[\n");
		sb.append(super.toString());		
		sb.append("\taggregates = [ ");
		if(aggregationFunction != null && value != null) {
			for(int i = 0; i < aggregationFunction.length; i++) {
				sb.append(aggregationFunction[i] + "=" + value[i]);
				if(i < aggregationFunction.length - 1) {
					sb.append(", ");
				}
			}
		}		
		sb.append(" ]\n");		
		sb.append("]");
		return sb.toString();
	}

}
