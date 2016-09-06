package ethz.ch.client;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import json.messages.PossibleStatesMessage;
import json.messages.SelectedStateMessage;
import state.State;

/**
 * Created by ales on 13/07/16.
 */
public class SendStatesButtonHandler extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    State state;
    String response;
    TextView textResponse;

    SendStatesButtonHandler(TextView textResponse, String addr, int port, State state){
        dstAddress = addr;
        dstPort = port;
        this.state = state;
        this.textResponse = textResponse;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        PossibleStatesMessage posStMsg = new PossibleStatesMessage(
                state.getClientIP(), state.getServerIP(), state.getClientID(),
                state.getServerID(), state.getPossibleStates(), state.getInitState());

        // Send selected state
        SelectedStateMessage selStMsg = new SelectedStateMessage(
                state.getClientIP(), state.getServerIP(), state.getClientID(),
                state.getServerID(), state.getSelectedState());

        Log.d("MESSAGE", posStMsg.message);
        Log.d("MESSAGE", selStMsg.message);

        Socket socket = null;

        try {
            // Create new socket
            socket = new Socket(dstAddress, dstPort);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(posStMsg.message);
            out.println(selStMsg.message);
            response = "Sent";
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "Unknown host";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException";
        }finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        textResponse.setText(response);
        super.onPostExecute(result);
    }
}
