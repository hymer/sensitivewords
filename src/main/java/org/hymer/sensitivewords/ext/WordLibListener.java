package org.hymer.sensitivewords.ext;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.hymer.sensitivewords.Finder;


/**
 * 敏感词库变化监听类
 * 
 * @author hymer
 * 
 */
public class WordLibListener implements MessageListener, Runnable {

	private String url = null;
	private String topicName = null;
	private Connection connection = null;
	private Session session = null;
	private Topic topic = null;
	private MessageConsumer consumer = null;

	public WordLibListener(String url, String topic) {
		this.url = url;
		this.topicName = topic;
	}

	@Override
	public void run() {
		try {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
					url);
			connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			topic = session.createTopic(topicName);
			consumer = session.createConsumer(topic);
			consumer.setMessageListener(this);
			connection.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * 协议： 
	 * 		首位字符： +(新增)、-(移除) 
	 * 		后面为词组，以,间隔：word1,word2
	 * 
	 * example:+word1,word2,word3
	 * </pre>
	 */
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				String msg = ((TextMessage) message).getText();
				if (msg != null && msg.trim().length() > 0) {
					msg = msg.trim();
					char flag = msg.charAt(0);
					switch (flag) {
					case FinderUtil.ADD_FLAG: // 新增
						Finder.addSensitiveWords(msg.substring(1));
						break;

					case FinderUtil.REMOVE_FLAG: // 删除
						Finder.removeSensitiveWords(msg.substring(1));
						break;

					default:
						break;
					}
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
