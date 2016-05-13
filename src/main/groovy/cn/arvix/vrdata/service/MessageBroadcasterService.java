package cn.arvix.vrdata.service;

/**
 * 发送消息给客户端浏览器
 * @author abel
 *
 */
public interface MessageBroadcasterService {
	public void send(String message);
}
