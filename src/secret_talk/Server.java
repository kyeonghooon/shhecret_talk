package secret_talk;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Vector;

import javax.swing.JTextArea;

import lombok.Getter;
import lombok.Setter;
import secret_talk.interfaces.CallBackServerService;
import secret_talk.panels.ServerFrame;

@Getter
@Setter
public class Server implements CallBackServerService {

	// 유저 리스트
	private Vector<User> userList = new Vector<>();
	// JList에 띄우기 위해 String 자료구조 선언
	private Vector<String> userIds = new Vector<>();
	// 방 리스트
	private Vector<Room> roomList = new Vector<>();
	// JList에 띄우기 위해 String 자료구조 선언
	private Vector<String> roomNames = new Vector<>();
	// 서버 소켓
	private ServerSocket serverSocket;

	// 프레임 관련 참조 변수
	ServerFrame serverFrame;
	JTextArea logBoard;

	// 프레임 띄우기
	public void openFrame() {
		serverFrame = new ServerFrame(this);
		logBoard = serverFrame.getLogPanel().getLogBoard();
	}

	// 서버 세팅 - 포트 번호 할당
	public void setupServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
			logMessage("[알림] 서버 시작\n");

			connectClient();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 클라이언트 연결 대기
	private void connectClient() {
		new Thread(() -> {
			while (true) {
				try {
					logMessage("[알림] 사용자 접속 대기\n");
					new User(this, serverSocket.accept());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 로그 메세지 화면에 출력 + 로그 저장
	public void logMessage(String str) {
		try (PrintWriter printWriter = new PrintWriter(new FileWriter("secret_Talk_log.txt", true), true)) {
			logBoard.append(str);
			printWriter.print(str);
			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 유저 관리를 위한 유저 리스트 추가 제거
	public void addUser(User user) {
		userList.add(user);
		userIds.add(user.getUserId());
		serverFrame.getUserListPanel().getUserList().setListData(userIds);
	}

	public void removeUser(User user) {
		userList.remove(user);
		userIds.remove(user.getUserId());
		serverFrame.getUserListPanel().getUserList().setListData(userIds);
	}
	
	// id가 이미 사용중임을 확인
	public boolean isNew(String userId) {
		for (String string : userIds) {
			if (string.equals(userId)) {
				return false;
			}
		}
		return true;
	}

	// 접속중인 모든 유저에게 방송
	public void broadCast(String msg) {
		for (int i = 0; i < userList.size(); i++) {
			// 유저의 writer는 각각의 클라이언트로 보내는 아웃풋 스트림
			(userList.elementAt(i)).getWriter().println(msg);
		}
	}

	// 버튼 상호작용 콜백 메서드
	@Override
	public void clickKickBtn(String userId) {
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.elementAt(i);
			if (userId.equals(user.getUserId())) {
				user.logOutUser();
				user.getWriter().println("kick/" + user.getId());
			}
		}
	}

	@Override
	public void clickPersonalMsgBtn(String id, String msg) {
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.elementAt(i);
			if (id.equals(user.getUserId())) {
				user.getWriter().println("personalMsg/" + "서버관리자/" + msg);
				logMessage("[메세지] 개인 메세지 : 서버관리자 -> " + id + " : " + msg + "\n");
				return;
			}
		}
	}

	@Override
	public void clickGroupMsgBtn(String msg) {
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.elementAt(i);
			user.getWriter().println("groupMsg/" + "서버관리자/" + msg);
			logMessage("[메세지] 단체 메세지 : 서버관리자  : " + msg + "\n");
		}
	}

	// 실행 코드
	public static void main(String[] args) {
		Server server = new Server();
		server.openFrame();
	}
}
