package chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

	private List<Session> sessions = new ArrayList<>();

	// 전체 메세지 전송
	public void sendAllMessage(String message) throws IOException {
		for (Session session : sessions) {
			session.sendMessage(message);
		}
	}

	public synchronized void add(Session session) {
		sessions.add(session);
	}

	public synchronized void remove(Session session) {
		sessions.remove(session);
	}

	public synchronized void closeAll() {
		for (Session session : sessions) {
			session.close();
		}

		sessions.clear();
	}
}
