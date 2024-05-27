package secret_talk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 서버입장에서 관리할 유저 클래스 - 클라이언트와 스트림이 연결되어 있음
public class User extends Thread {

	// 서버 참조
	Server mContext;

	// 소켓 - 클라이언트와 연결된 소켓
	private Socket socket;

	// 스트림
	private PrintWriter writer;
	private BufferedReader reader;

	// 변수
	private String userId;
	private String currentRoom;

	/**
	 * protocol/from(id)/message : 개인 메세지<br>
	 * protocol/from(room)/message : 방 메세지<br>
	 */
	private String protocol;
	private String from; // userId or roomName
	private String message;

	public User(Server mContext, Socket socket) {
		try {
			this.mContext = mContext;
			this.socket = socket;
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			registUser();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registUser() {
		try {
			// 1. id 입력 받기
			userId = reader.readLine();
			while(mContext.isNew(userId) == false) {
				mContext.logMessage("[알림] id 중복 발생.\n");
				writer.println("reject/");
				userId = reader.readLine();
			}
			// 2. 메세지 알림
			mContext.logMessage("[접속] " + userId + "님이 접속 하셨습니다.\n");
			writer.println("permit/");
			// 3. 유저 등록
			mContext.addUser(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO 유저 리스트 전송
	private void sendUserList() {
		mContext.getUserIds();
		for (int i = 0; i < mContext.getUserIds().size(); i++) {
			String str = mContext.getUserIds().elementAt(i);
			writer.println("UserList/" + str);
		}
	}
	// TODO 방 리스트 전송
	private void sendRoomList() {
		
	}

	@Override
	public void run() {
		String msg;
		try {
			while ((msg = reader.readLine()) != null) {
				checkProtocol(msg);
			}
		} catch (IOException e) {
			// TODO 메세지용 패널 만들 예정
			JOptionPane.showInternalMessageDialog(null, "접속 종료 !", "알림", JOptionPane.ERROR_MESSAGE);
		} finally {
			mContext.logMessage("[접속] " + userId + "님의 접속이 종료됨.\n");
			// TODO 방에서 나가는 코드

			// TODO 서버에서 유저가 나가는 코드
			mContext.removeUser(this);
		}
	}

	private void checkProtocol(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, "/");
		protocol = tokenizer.nextToken();
		from = tokenizer.nextToken();
	}
}
