package json.messages;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import json.enums.MessageTypes;
import state.PossibleStatePoint;

/**
 * Represents a message that includes list of possible states.
 * This should be the first message the client sends. 
 * 
 * @author nikolijo, ales_omerzel
 *
 */
public class PossibleStatesMessage extends BaseMessage {

	public String message = "";
	
	public PossibleStatesMessage(String srcIP, String dstIP, String srcID, String dstID,
								 ArrayList<PossibleStatePoint> possibleStates, PossibleStatePoint initState) {
		super(srcIP, dstIP, srcID, dstID);

		JSONObject obj = new JSONObject();


// TODO, possible state message
		obj.put("type", "POSSIBLE_STATES_MSG");
		obj.put("srcIP", srcIP);
		obj.put("dstIP", dstIP);
		obj.put("srcID", srcID);
		obj.put("dstID", dstID);

		JSONArray init = new JSONArray();
		if (initState != null && initState.values != null)
			for (double d : initState.values)
				init.add(d);

		obj.put("initState", init);

		JSONArray states = new JSONArray();

		for (PossibleStatePoint state : possibleStates) {
			JSONArray coordinates = new JSONArray();
			for (double d : state.values)
				coordinates.add(d);
			states.add(coordinates);
		}

		obj.put("possibleStates", states);


		this.message = obj.toJSONString();

	}
	

}
