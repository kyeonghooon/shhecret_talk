package secret_talk;

import java.io.IOException;
import java.net.Socket;

import secret_talk.panels.ClientFrame;

public class Client {
	
	// 소켓
	private Socket socket;
	
	// 프레임 관련 참조 변수
	ClientFrame clientFrame;
	
	// 프레임 띄우기
	public void openFrame() {
		clientFrame = new ClientFrame(this);
	}
	
	// 서버에 연결 - ip주소 port번호 입력
	public void connectServer(String ip, int port) {
		try {
			socket = new Socket(ip, port);
		} catch (IOException e) {
			// TODO 오류 패널 제작
			new ClientFrame(this);
			e.printStackTrace();
		}
	}
	
	// 테스트 코드
	public static void main(String[] args) {
		Client client = new Client();
		client.openFrame();
	}
	
}
