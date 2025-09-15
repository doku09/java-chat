package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static util.MyLogger.log;

public class Session implements Runnable{
	private static long userId = 1;
	private User user;
	private final Socket socket;
	private final DataInputStream input;
	private final DataOutputStream output;
	private final SessionManager sessionManager;
	private boolean closed = false;

	public Session(Socket socket, SessionManager sessionManager) throws IOException {
		this.socket = socket;
		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
		this.sessionManager = sessionManager;
		sessionManager.add(this);
	}

	@Override
	public void run() {

		try {
			while(true) {
				// 클라이언트로부터 문자 받기
				String received = input.readUTF();

				//클라이언트에게 문자 보내기
				if(this.user != null) {
					if(received.trim().startsWith("/message")) {
						String[] msgInfo = received.split(" ");
						if(msgInfo.length > 0) {
							String message = msgInfo[1];
							sessionManager.sendAllMessage("[전체메세지]" + this.user.getName() + ": " + message);
						}
					} else {
						sendMessage(received);
						log("[client <- server] " + received);
					}

				} else {
					if(received.trim().startsWith("/join")) {
						String[] joinInfo = received.split(" ");
						if(joinInfo.length > 0) {
							String username = joinInfo[1];
							this.user = new User(userId++,username);
							sessionManager.sendAllMessage(username+"님이 입장하였습니다.");
						} else {
							sendMessage("닉네임을 입력해주세요");
						}
					} else {
						sendMessage("닉네임을 입력하고 입장해주세요.");
						log("[client <- server] 닉네임을 입력하고 입장해주세요.");
					}

				}

				if(received.equals("/exit")) {
					break;
				}
			}

		} catch (IOException e) {
			log(e);
		} finally {
			sessionManager.remove(this);
			close();
		}
	}


	// 세션 종료시, 서버 종료시 동시에 호출 될 수 있다.
	public void sendMessage(String message) throws IOException {
		output.writeUTF(message);
	}

	// 세션 종료시, 서버 종료시 동시에 호출 될 수 있다.
	public synchronized void close() {
		if(closed) {
			return;
		}

		SocketCloseUtil.closeAll(socket,input,output);
		closed = true;
		log("연결종료: " + socket + " isClosed: " + socket.isClosed());
	}
}