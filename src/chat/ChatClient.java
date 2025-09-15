package chat;

import chat.client.ReadHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static util.MyLogger.log;

public class ChatClient {

	public static void main(String[] args) {
		log("클라이언트 시작");

		int PORT = 12345;
		try (
			Socket socket = new Socket("localhost", PORT);
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		){
			log("소켓 연결: " + socket);

			ReadHandler readHandler = new ReadHandler(socket);

			Thread thread = new Thread(readHandler);
			thread.start();

			Scanner scan = new Scanner(System.in);
			while(true) {
				String toSend = scan.nextLine();

				// 서버에 메시지 보내기
				dos.writeUTF(toSend);
				log("client -> server: " + toSend);

				if(toSend.equals("exit")) {
					break;
				}
			}
		} catch (IOException e) {
			log(e);
		}
	}
}
