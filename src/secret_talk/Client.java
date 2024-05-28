package secret_talk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;

import lombok.Getter;
import lombok.Setter;
import secret_talk.interfaces.CallBackClientService;
import secret_talk.interfaces.ProtocolImpl;
import secret_talk.panels.ClientFrame;
import secret_talk.panels.ClientRoomPanel;

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
	// 상호작용 가능한 컴포넌트
	private JList<String> userList;
	private JList<String> roomList;
	private JButton newRoomBtn;
	private JButton enterRoomBtn;

	// 명단 업데이트 용 벡터
	private Vector<String> userIdList = new Vector<>();
	private Vector<String> roomNameList = new Vector<>();

	// 유저 정보
	private String myId;
	private String myRoom;

	/**
	 * 예시)<br>
	 * protocol(personalMsg)/from(id)/data(msg) : 개인 메세지<br>
	 * protocol(chat)/from(room)/data(msg) : 방 메세지<br>
	 * protocol(newRoom)/from(room)/data(pw) : 방 생성<br>
	 */
	private String protocol;
	private String from; // userId or roomName
	private String data;

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
			// TODO 오류 패널 제작
			System.out.println("해당 주소, 포트에 연결할 수 없습니다.");
		}
	}

	// 스트림 셋업
	public void setupStream() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			// TODO 오류 패널 제작
			e.printStackTrace();
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
				// TODO 거절 메세지 출력
				return false;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 상호작용 가능한 컴포넌트의 주소를 주입받음
	public void setupComponet() {
		userList = clientFrame.getUserListPanel().getUserList();
		roomList = clientFrame.getRoomListPanel().getRoomList();
		newRoomBtn = clientFrame.getRoomListPanel().getNewRoomBtn();
		enterRoomBtn = clientFrame.getRoomListPanel().getEnterRoomBtn();
	}

	// 서버 측으로부터 요청을 받음
	private void readThread() {
		new Thread(() -> {
			try {
				String msg;
				while ((msg = reader.readLine()) != null) {
					System.out.println(msg);
					checkProtocol(msg);
				}
			} catch (IOException e) {
				// TODO 에러 처리 - 서버가 사라졌을때 뜸 혹은 강퇴당했을지도
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * 보내기용 프로토콜 메서드
	 */

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
			roomList();
			break;
		case "newRoom":
			newRoom();
			break;
		case "FailNewRoom":
			// TODO 방 이름 중복 메세지 띄움
			break;
		case "SuccessNewRoom":
			successNewRoom();
			break;
		case "kick":
			data = tokenizer.nextToken();
			// TODO 강퇴 메세지 띄움
			clientFrame.setVisible(false);
		}
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
		roomNameList.add(from);
		roomList.setListData(roomNameList);
	}

	// 새로운 방 생성시 호출
	@Override
	public void newRoom() {
		roomNameList.add(from);
		roomList.setListData(roomNameList);
	}
	
	public void successNewRoom() {
		myRoom = from;
		newRoomBtn.setEnabled(false);
		enterRoomBtn.setEnabled(false);
		ClientRoomPanel roomPanel = new ClientRoomPanel(this, from);
		clientFrame.getTabPane().addTab(from, roomPanel);
		clientFrame.getTabPane().setSelectedComponent(roomPanel);
	}

	// 버튼 상호작용 콜백 메서드
	@Override
	public void clickNewRoomBtn(String roomName, String password) {
		writer.println("newRoom/" + roomName + "/" + password);
	}

	// 테스트 코드
	public static void main(String[] args) {
		Client client = new Client();
		client.openFrame();
	}

}
