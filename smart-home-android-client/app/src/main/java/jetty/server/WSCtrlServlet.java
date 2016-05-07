
package jetty.server;

import com.myhome.frame.ServiceMain;
import com.myhome.websocket.ClientWebSocket;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.http.HttpServletRequest;

public class WSCtrlServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest req, String protocol) {
        return new ClientWebSocket(ServiceMain.websocketRoom);
    }
}
