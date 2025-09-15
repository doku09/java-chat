package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static util.MyLogger.log;

public class ChatServer {

	private static final int PORT = 12345;
	
	public static void main(String[] args) throws IOException {
		log("서버 시작");
		SessionManager sessionManager = new SessionManager();
		ServerSocket serverSocket = new ServerSocket(PORT);
		log("서버 소켓 시작 - 리스닝 포트: " + PORT);

		// ShutdownHook 등록
		ShutdownHook shutdownHook = new ShutdownHook(serverSocket, sessionManager);
		Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook, "shutdown"));

		try {
			while (true) {
				Socket socket = serverSocket.accept(); // 블로킹
				log("소켓 연결: " + socket);

				Session session = new Session(socket, sessionManager);
				Thread thread = new Thread(session);
				thread.start();
			}
		} catch (IOException e) {
			log("서버 소켓 종료: " + e);
		}
	}

	static class ShutdownHook implements Runnable {
		private final ServerSocket serverSocket;
		private final SessionManager sessionManager;

		public ShutdownHook(ServerSocket serverSocket, SessionManager sessionManager) {
			this.serverSocket = serverSocket;
			this.sessionManager = sessionManager;
		}

		@Override
		public void run() {
			log("shutdownHook 실행");
			try {
				sessionManager.closeAll();
				serverSocket.close();

				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("e = " + e);
			}
		}
	}
}
