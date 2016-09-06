package json.enums;


/**
 * ON_UPDATE subscription policy means that server will send values of 
 * aggregation functions that the client subscribed for only when at least
 * one of them change. PERIODICALLY subscription policy means that server
 * will send values of aggregation functions that the client subscribed for only
 * when timer expires.
 * 
 * @author nikolijo
 *
 */
public enum SubscriptionPolicy {
	ON_UPDATE,
	PERIODICALLY
}
