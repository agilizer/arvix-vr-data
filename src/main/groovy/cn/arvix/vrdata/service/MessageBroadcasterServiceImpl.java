package cn.arvix.vrdata.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import cn.arvix.vrdata.WebSocketConfig;

@Service
public class MessageBroadcasterServiceImpl  implements MessageBroadcasterService{ 
    @Autowired
    WebSocketConfig.WebSocketTransactionsHandler handler;

	@Override
	public void send(String message) {
		 try {
			handler.broadcastMessage(new TextMessage(message));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}