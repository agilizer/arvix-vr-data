package cn.arvix.vrdata.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import cn.arvix.vrdata.WebSocketConfig;

@Service
public class MessageBroadcasterServiceImpl  implements MessageBroadcasterService{ 
	private static final Logger log = LoggerFactory
            .getLogger(MessageBroadcasterServiceImpl.class);
    @Autowired
    WebSocketConfig.WebSocketTransactionsHandler handler;

	@Override
	public void send(String message) {
		 try {
			handler.broadcastMessage(new TextMessage(message));
		} catch (Exception e) {
			log.warn("send msg error",e);
		}		
	}
}