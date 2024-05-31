package secret_talk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JList;

import lombok.Getter;
import lombok.Setter;
import secret_talk.interfaces.CallBackClientService;
import secret_talk.interfaces.ProtocolImpl;
import secret_talk.panels.ClientFrame;
import secret_talk.panels.ClientRoomPanel;
import secret_talk.panels.MessageFrame;

@Getter
@Setter
public class Client implements ProtocolImpl, CallBackClientService {

	// 소켓
	private Socket socket;

	// 스트림
	private BufferedReader reader;
	private PrintWriter writer;

	// 프레임 관련 참조 변수
	private ClientFrame clientFrame;
	private Vector<ClientRoomPanel> roomPanels = new Vector<>();
	// 상호작용 가능한 컴포넌트
	private JList<String> userList;
	private JList<String> roomList;

	// 명단 업데이트 용 벡터
	private Vector<String> userIdList = new Vector<>();
	private Vector<String> roomNameList = new Vector<>();

	// 유저 정보
	private String myId;
	private String myRoom;
	private Vector<String> myRooms = new Vector<>();
	
	// 강퇴 확인
	private boolean kick;
	
	// 리터럴
	public final String PW_NULL = "0";

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

	// 프레임 띄우기
	public void openFrame() {
		clientFrame = new ClientFrame(this);
	}

	// 서버에 연결 - ip주소 port번호 입력
	public void connectServer(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			clientFrame.setConnected(true);
			setupStream();
		} catch (IOException e) {
			(new MessageFrame()).errorMsg("connectServer");
		}
	}

	// 스트림 셋업
	public void setupStream() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
		}
	}

	// id 전송 - 최초 메세지는 id로 확정이므로 프로토콜 안씀
	public void createId(String id) {
		myId = id;
		writer.println(id);
	}

	// id 중복 확인
	public boolean checkId() {
		try {
			String check = reader.readLine();
			switch (check) {
			case "permit/":
				setupComponet();
				readThread();
				return true;
			case "reject/":
				(new MessageFrame()).errorMsg("reject");
				return false;
			default:
				break;
			}
		} catch (IOException e) {
		}
		return false;
	}

	// 상호작용 가능한 컴포넌트의 주소를 주입받음
	public void setupComponet() {
		userList = clientFrame.getUserListPanel().getUserList();
		roomList = clientFrame.getRoomListPanel().getRoomList();
	}

	// 서버 측으로부터 요청을 받음
	private void readThread() {
		new Thread(() -> {
			try {
				String msg;
				while ((msg = reader.readLine()) != null) {
					// 확인용 코드
					System.out.println(msg);
					checkProtocol(msg);
				}
			} catch (IOException e) {
				if (kick) {
					(new MessageFrame()).errorMsg("kick");
				} else {
					(new MessageFrame()).errorMsg("serverNull");
				}
			}
		}).start();
	}

	/**
	 * 받기용 프로토콜 메서드
	 */
	private void checkProtocol(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, "/");
		protocol = tokenizer.nextToken();
		from = tokenizer.nextToken();

		switch (protocol) {
		case "userList":
			userList();
			break;
		case "newUser":
			newUser();
			break;
		case "logOutUser":
			logOutUser();
			break;
		case "roomList":
			data = tokenizer.nextToken(); // password
			roomList();
			break;

		// 방 생성과 관련된 프로토콜
		case "newRoom":
			data = tokenizer.nextToken(); // password
			newRoom();
			break;
		case "FailNewRoom":
			(new MessageFrame()).errorMsg("roomNameUsed");
			break;
		case "successNewRoom":
			successNewRoom();
			break;

		// 방 입장과 관련된 프로토콜
		case "enterRoom":
			enterRoom();
			break;
		case "FailEnterRoom":
			(new MessageFrame()).errorMsg("failEnterRoom");
			break;

		// 방 메세지 프로토콜
		case "roomMsg":
			data = tokenizer.nextToken();
			if (tokenizer.hasMoreTokens()) {
				msg = tokenizer.nextToken();
			}
			roomMsg();
			break;

		// 메세지 프로토콜
		case "personalMsg":
			data = tokenizer.nextToken(); // msg
			personalMsg();
			break;
		case "groupMsg":
			data = tokenizer.nextToken(); // msg
			groupMsg();
			break;

		// 방 나가기 프로토콜
		case "outRoom":
			outRoom();
			break;
		case "removeRoom":
			removeRoom();
			break;
		case "kick":
			kick = true;
			clientFrame.setVisible(false);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		// 프로토콜 체크 끝나면 변수를 비움
		protocol = null;
		from = null;
		data = null;
		msg = null;
	}

	// 본인을 제외한 리스트를 최초로 받아옴
	public void userList() {
		userIdList.add(from);
		userList.setListData(userIdList);
	}

	// 최초에는 본인도 등록함
	public void newUser() {
		userIdList.add(from);
		userList.setListData(userIdList);
	}

	// 접속 해제
	public void logOutUser() {
		userIdList.remove(from);
		userList.setListData(userIdList);
	}

	// 방 리스트 받아옴
	public void roomList() {
		if (data.equals(PW_NULL)) {
			roomNameList.add("[공개]" + from);
		} else {
			roomNameList.add("[비밀]" + from);
		}
		roomList.setListData(roomNameList);
	}

	// 새로운 방 생성시 호출
	@Override
	public void newRoom() {
		if (data.equals(PW_NULL)) {
			roomNameList.add("[공개]" + from);
		} else {
			roomNameList.add("[비밀]" + from);
		}
		roomList.setListData(roomNameList);
	}

	public void successNewRoom() {
		myRooms.add(from);
		ClientRoomPanel roomPanel = new ClientRoomPanel(this, from);
		roomPanels.add(roomPanel);
		clientFrame.getTabPane().addTab(from, roomPanel);
		clientFrame.getTabPane().setSelectedComponent(roomPanel);
	}

	// 방 입장 성공시 호출
	@Override
	public void enterRoom() {
		myRooms.add(from);
		ClientRoomPanel roomPanel = new ClientRoomPanel(this, from);
		roomPanels.add(roomPanel);
		clientFrame.getTabPane().addTab(from, roomPanel);
		clientFrame.getTabPane().setSelectedComponent(roomPanel);
	}

	// 방에서 보내는 모든 메세지 -> 채팅방에 띄움
	@Override
	public void roomMsg() {
		// data : userId, msg : 메세지
		if (msg.equals("입장")) {
			ClientRoomPanel roomPanel;
			if ((roomPanel = findRoomPanel(from)) != null) {
				roomPanel.getChatArea().append(data + "님이 입장 하셨습니다\n");
				return;
			}
		} else if (msg.equals("퇴장")) {
			ClientRoomPanel roomPanel;
			if ((roomPanel = findRoomPanel(from)) != null) {
				roomPanel.getChatArea().append(data + "님이 퇴장 하셨습니다\n");
				return;
			}
		}
		if (data.equals(myId)) {
			data = "나";
		}
		for (int i = 0; i < roomPanels.size(); i++) {
			ClientRoomPanel roomPanel;
			if ((roomPanel = findRoomPanel(from)) != null) {
				roomPanel.getChatArea().append(data + " : " + msg + "\n");
				break;
			}
		}
	}
	
	@Override
	public void personalMsg() {
		(new MessageFrame()).personalMsg(from, data);
	}
	
	@Override
	public void groupMsg() {
		// 내가 보낸건 무시
		if (!from.equals(myId)) {
			(new MessageFrame()).groupMsg(from, data);
		}
	}
	
	// 방 나가기 시 호출
	@Override
	public void outRoom() {
		myRooms.remove(from);
		getClientFrame().getTabPane().setSelectedIndex(2);
		ClientRoomPanel roomPanel;
		if ((roomPanel = findRoomPanel(from)) != null) {
			getClientFrame().getTabPane().remove(roomPanel);
		}
	}
	
	// 방 제거
	@Override
	public void removeRoom() {
		roomNameList.remove("[공개]" + from);
		roomNameList.remove("[비밀]" + from);
		roomList.setListData(roomNameList);
	}
	
	// 벡터 찾기
	private ClientRoomPanel findRoomPanel(String roomName) {
		for (ClientRoomPanel roomPanel : roomPanels) {
			if (roomName.equals(roomPanel.getRoomName())) {
				return roomPanel;
			}
		}
		return null;
	}

	// 버튼 상호작용 콜백 메서드
	/**
	 * 보내기용 프로토콜 메서드
	 */
	@Override
	public void clickNewRoomBtn(String roomName, String password) {
		writer.println("newRoom/" + roomName + "/" + password);
	}

	@Override
	public void clickEnterRoomBtn(String roomName, String password) {
		for (int i = 0; i < myRooms.size(); i++) {
			if (roomName.equals(myRooms.elementAt(i))) {
				(new MessageFrame()).errorMsg("enteredRoom");
				return;
			}
		}
		writer.println("enterRoom/" + roomName + "/" + password);
	}

	@Override
	public void clickOutRoomBtn(String roomName) {
		writer.println("outRoom/" + roomName);
	}

	@Override
	public void clickRoomMsgBtn(String roomName, String msg) {
		writer.println("roomMsg/" + roomName + "/" + myId + "/" + msg);
	}

	@Override
	public void clickPersonalMsgBtn(String id, String msg) {
		if (id.equals(myId)) {
			(new MessageFrame()).errorMsg("self");
			return;
		}
		writer.println("personalMsg/" + myId + "/" + id + "/" + msg);
	}

	@Override
	public void clickGroupMsgBtn(String msg) {
		writer.println("groupMsg/" + myId + "/" + msg);
	}

	// 실행 코드
	public static void main(String[] args) {
		Client client = new Client();
		client.openFrame();
	}

}
