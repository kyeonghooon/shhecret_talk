package secret_talk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

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
	private Vector<String> rooms = new Vector<>();

	/**
	 * 예시)<br>
	 * protocol(personalMsg)/from(id)/data(msg) : 개인 메세지<br>
	 * protocol(chat)/from(room)/data(msg) : 방 메세지<br>
	 * protocol(newRoom)/from(room)/data(pw) : 방 생성<br>
	 */
	private String protocol;
	private String from; // userId or roomName
	private String data;
	private String msg; // roomMsg에서는 토큰이 4개까지도 가능

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
			// id 입력을 받기전에 연결이 끊긴거라 따로 처리하지 않음
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
		for (int i = 0; i < rooms.size(); i++) {
			String roomName = rooms.elementAt(i);
			mContext.outUserFromRoom(roomName, this);
		}
		mContext.removeUser(this);
		mContext.broadCast("logOutUser/" + userId);
	}
	
	@Override
	public void removeRoom() {
		for (int i = 0; i < mContext.getRoomList().size(); i++) {
			Room room = mContext.getRoomList().elementAt(i);
			for (int j = 0; j < rooms.size(); j++) {
				String roomName = rooms.elementAt(i);
				if (room.getRoomName().equals(roomName)) {
					mContext.getRoomList().remove(room);
					mContext.getRoomNames().remove(roomName);
					mContext.broadCast("removeRoom/" + roomName);
				}
			}
		}
	}

	/**
	 * 읽기용 쓰레드<br>
	 */
	@Override
	public void run() {
		String msg;
		try {
			while ((msg = reader.readLine()) != null) {
				// 확인용 코드
				System.out.println(msg);
				checkProtocol(msg);
			}
		} catch (IOException e) {
			// 접속이 종료 되었을때 창을 띄우기에는 나갈때 마다 떠서 너무 불편함
			// 따라서 로그에만 기록하고 띄우지 않음
		} finally {
			mContext.logMessage("[접속] " + userId + "님의 접속이 종료됨.\n");
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
		case "enterRoom":
			data = tokenizer.nextToken();
			enterRoom();
			break;

		// 방 메세지 프로토콜
		case "roomMsg":
			data = tokenizer.nextToken(); // 보내준 id
			if (tokenizer.hasMoreTokens()) {
				msg = tokenizer.nextToken();
			}
			roomMsg();
			break;

		// 방 나가기 프로토콜
		case "outRoom":
			outRoom();
			break;

		// 메세지 프로토콜
		case "personalMsg":
			data = tokenizer.nextToken(); // 보내줄 id
			msg = tokenizer.nextToken(); // msg
			personalMsg();
			break;
		case "groupMsg":
			data = tokenizer.nextToken(); // msg
			groupMsg();
			break;

		}
		// 프로토콜 체크 끝나면 변수를 비움
		protocol = null;
		from = null;
		data = null;
		msg = null;
	}

	// 새로운 방 생성시 호출
	@Override
	public void newRoom() {
		for (int i = 0; i < mContext.getRoomList().size(); i++) {
			Room room = mContext.getRoomList().elementAt(i);
			if (room.getRoomName().equals(from)) {
				mContext.logMessage("[알림] 방 생성 실패 " + userId + "_" + from + "\n");
				writer.println("FailNewRoom/" + from);
				return;
			}
		}
		rooms.add(from);
		mContext.getRoomList().add(new Room(from, Integer.parseInt(data), this));
		mContext.getRoomNames().add(from);
		mContext.logMessage("[알림] 방 생성 " + userId + "_" + from + "\n");
		mContext.broadCast("newRoom/" + from + "/" + data);
		writer.println("successNewRoom/" + from);
	}

	@Override
	public void enterRoom() {
		for (int i = 0; i < mContext.getRoomList().size(); i++) {
			Room room = mContext.getRoomList().elementAt(i);
			// 방이름과 비밀번호가 매치된다면
			if (room.getRoomName().equals(from) && room.getPassWord() == Integer.parseInt(data)) {
				rooms.add(from);
				room.getUserList().add(this);
				mContext.logMessage("[알림] 방 입장 " + userId + "_" + from + "\n");
				writer.println("enterRoom/" + from);
				room.roomBroadCast("roomMsg/" + from + "/" + userId + "/입장");
				return;
			}
		}
		mContext.logMessage("[알림] 방 입장 실패" + userId + "_" + from + "\n");
		writer.println("FailEnterRoom/" + from);
	}

	@Override
	public void roomMsg() {
		mContext.logMessage("[메세지] " + from + "에서 " + data + " : " + msg + "\n");
		for (int i = 0; i < mContext.getRoomList().size(); i++) {
			Room room = mContext.getRoomList().elementAt(i);
			// 방이름이 매치된다면
			if (room.getRoomName().equals(from)) {
				room.roomBroadCast("roomMsg/" + from + "/" + userId + "/" + msg);
				return;
			}
		}
	}

	@Override
	public void personalMsg() {
		// data : 보내줄 id
		for (int i = 0; i < mContext.getUserList().size(); i++) {
			User user = mContext.getUserList().elementAt(i);
			if (data.equals(user.getUserId())) {
				user.getWriter().println("personalMsg/" + from + "/" + msg);
				mContext.logMessage("[메세지] 개인 메세지 " + from + " -> " + data + " : " + msg + "\n");
				return;
			}
		}
	}

	@Override
	public void groupMsg() {
		for (int i = 0; i < mContext.getUserList().size(); i++) {
			User user = mContext.getUserList().elementAt(i);
			user.getWriter().println("groupMsg/" + from + "/" + msg);
			mContext.logMessage("[메세지] 단체 메세지 " + from + " : " + msg + "\n");
		}
	}

	@Override
	public void outRoom() {
		for (int i = 0; i < mContext.getRoomList().size(); i++) {
			Room room = mContext.getRoomList().elementAt(i);
			if (room.getRoomName().equals(from)) {
				room.getUserList().remove(this);
				mContext.logMessage("[알림] 방 퇴장 " + userId + "_" + from + "\n");
				writer.println("outRoom/" + from);
				room.roomBroadCast("roomMsg/" + from + "/" + userId + "/퇴장");
				if (room.getUserList().isEmpty()) {
					removeRoom();
				}
				rooms.remove(from);
				return;
			}
		}
	}

}
