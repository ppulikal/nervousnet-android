package json.enums;

public class Utility {
	
	public final static String POSSIBLE_STATES_MSG = "POSSIBLE_STATES_MSG";
	public final static String SELECTED_STATE_MSG = "SELECTED_STATE_MSG";
	public final static String AGGREGATE_MSG = "AGGREGATE_MSG";	
	public final static String SUBSCRIPTION_MSG = "SUBSCRIPTION_MSG";
	public final static String REQUEST_MSG = "REQUEST_MSG";
	
	public static final String ON_UPDATE = "ON_UPDATE";
	public static final String PERIODICALLY = "PERIODICALLY";
	
	/**
	 * Converts JsonTags tag into a String
	 * @param tag - JsonTag object
	 * @return string that corresponds to tag. return null if match not found
	 */
	public static String aggTagToString(JsonTags tag) {
		switch(tag) {
		case AVG:
			return "AVG";
		case COUNT:
			return "COUNT";
		case MAX:
			return "MAX";
		case MIN:
			return "MIN";
		case STDEV:
			return "STDEV";
		case SUM:
			return "SUM";
		case SUMSQR:
			return "SUMSQR";
		default: 
			return null;
		}
	}
	
	/**
	 * Converts String tag names of aggregation functions into JsonTag objects
	 * @param tag - String name of aggregation function
	 * @return JsonTags object that corresponds tag if match was found. Otherwise, returns null.
	 */
	public static JsonTags stringToAggTag(String tag) {
		switch(tag) {
		case "AVG":
			return JsonTags.AVG;
		case "COUNT":
			return JsonTags.COUNT;
		case "MAX":
			return JsonTags.MAX;
		case "MIN":
			return JsonTags.MIN;
		case "STDEV":
			return JsonTags.STDEV;
		case "SUM":
			return JsonTags.SUM;
		case "SUMSQR":
			return JsonTags.SUMSQR;
		default:
				return null;			
		}
	}
	
	public static MessageTypes string2MessageTypes(String tag) {
		switch (tag) {
		case POSSIBLE_STATES_MSG:
			return MessageTypes.POSSIBLE_STATES_MSG;
		case SELECTED_STATE_MSG:
			return MessageTypes.SELECTED_STATE_MSG;
		case REQUEST_MSG:
			return MessageTypes.REQUEST_MSG;
		case SUBSCRIPTION_MSG:
			return MessageTypes.SUBSCRIPTION_MSG;
		case AGGREGATE_MSG:
			return MessageTypes.AGGREGATE_MSG;
		default:
			return null;
		}
	}
	
	public static SubscriptionPolicy string2policy(String tag) {
		switch (tag) {
		case ON_UPDATE:
			return SubscriptionPolicy.ON_UPDATE;
		case PERIODICALLY:
			return SubscriptionPolicy.PERIODICALLY;
		default:
			return null;
		}
	}
	
	

}
