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
import secret_talk.interfaces.ProtocolImpl;

@Getter
@Setter
// 서버입장에서 관리할 유저 클래스 - 클라이언트와 스트림이 연결되어 있음
public class User extends Thread implements ProtocolImpl {

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
	 * 예시)<br>
	 * protocol(personalMsg)/from(id)/data(msg) : 개인 메세지<br>
	 * protocol(chat)/from(room)/data(msg) : 방 메세지<br>
	 * protocol(newRoom)/from(room)/data(pw) : 방 생성<br>
	 */
	private String protocol;
	private String from; // userId or roomName
	private String data;

	public User(Server mContext, Socket socket) {
		try {
			this.mContext = mContext;
			this.socket = socket;
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);

			registUser();
			this.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registUser() {
		try {
			// 1. id 입력 받기
			userId = reader.readLine();
			while (mContext.isNew(userId) == false) {
				mContext.logMessage("[알림] id 중복 발생.\n");
				writer.println("reject/");
				userId = reader.readLine();
			}
			// 2. 메세지 알림
			mContext.logMessage("[접속] " + userId + "님이 접속 하셨습니다.\n");
			writer.println("permit/");
			// 3. 유저 등록
			userList(); // 본인이 제외된 유저리스트를 먼저 받음
			newUser(); // 신규 유저 등록
			roomList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 보내기용 프로토콜 메서드
	 */
	// 기존의 유저 리스트를 보냄
	public void userList() {
		mContext.getUserIds();
		for (int i = 0; i < mContext.getUserIds().size(); i++) {
			String str = mContext.getUserIds().elementAt(i);
			writer.println("userList/" + str);
		}
	}

	// 새로운 유저임을 알림
	public void newUser() {
		mContext.addUser(this);
		mContext.broadCast("newUser/" + userId);
	}

	// 방 리스트 전송
	public void roomList() {
		mContext.getRoomNames();
		for (int i = 0; i < mContext.getRoomNames().size(); i++) {
			String str = mContext.getRoomNames().elementAt(i);
			writer.println("roomList/" + str);
		}
	}

	public void logOutUser() {
		mContext.removeUser(this);
		mContext.broadCast("logOutUser/" + userId);
	}

	@Override
	public void run() {
		// ReadThread
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
			logOutUser();
		}
	}

	/**
	 * 받기용 프로토콜 메서드
	 */
	private void checkProtocol(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, "/");
		protocol = tokenizer.nextToken();
		from = tokenizer.nextToken();
		
		switch (protocol) {
		case "newRoom":
			data = tokenizer.nextToken();
			newRoom();
			break;
		}
	}

	@Override
	public void newRoom() {
		for (int i = 0; i < mContext.getRoomList().size(); i++) {
			Room room = mContext.getRoomList().elementAt(i);
			if (room.getRoomName().equals(from)) {
				writer.println("FailNewRoom/" + from);
				mContext.logMessage("[알림] 방 생성 실패 " + userId + "_" + from + "\n");
				return;
			}
		}
		currentRoom = from;
		mContext.getRoomList().add(new Room(from, Integer.parseInt(data), this));
		mContext.logMessage("[알림] 방 생성 " + userId + "_" + from + "\n");
		mContext.broadCast("newRoom/" + from + "/" + data);
		writer.println("SuccessNewRoom/" + from);
	}

}
