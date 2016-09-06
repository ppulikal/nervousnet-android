package json.messages;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;

import json.enums.MessageTypes;
import state.PossibleStatePoint;

/**
 * Represents a message for setting new selected state
 * 
 * @author nikolijo, ales_omerzel
 *
 */
public class SelectedStateMessage extends BaseMessage{
	
	public String message;
	
	public SelectedStateMessage(String srcIP, String dstIP, String srcID, String dstID,
			PossibleStatePoint selectedState) {
		super(srcIP, dstIP, srcID, dstID);

		JSONObject obj = new JSONObject();


//TODO: SELECTED STATE MUST NOT BE STATIC
		obj.put("type", "SELECTED_STATE_MESSAGE");
		obj.put("srcIP", srcIP);
		obj.put("dstIP", dstIP);
		obj.put("srcID", srcID);
		obj.put("dstID", dstID);

		JSONArray init = new JSONArray();
		if (selectedState != null && selectedState.values != null)
			for (double d : selectedState.values)
				init.add(d);

		obj.put("selectedState", init);


		this.message = obj.toJSONString();
	}
}
