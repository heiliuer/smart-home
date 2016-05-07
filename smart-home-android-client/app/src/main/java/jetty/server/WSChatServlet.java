
package jetty.server;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.http.HttpServletRequest;

public class WSChatServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;
    public static final ChatRoom mChatRoom = new ChatRoom();

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest req, String protocol) {
        return new ChatWebSocket(mChatRoom);
    }
}
