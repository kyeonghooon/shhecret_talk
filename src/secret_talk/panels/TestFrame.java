package secret_talk.panels;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import secret_talk.Client;

public class TestFrame extends JFrame {

	// 참조 변수
	private Client mContext;
	private TestFrame testFrame;
	private ClientUserListPanel userListPanel;
	private ClientRoomListPanel roomListPanel;
	private Vector<ClientRoomPanel> roomPanels;

	private JLabel backgroundLabel;
	private JTabbedPane tabPane;

	// 연결 확인 -> id입력으로 넘어감
	private boolean isConnected;

	public TestFrame(Client mContext) {
		this.mContext = mContext;
		testFrame = this;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		// TODO 이미지 교체
		backgroundLabel = new JLabel();

		userListPanel = new ClientUserListPanel(mContext);
		roomListPanel = new ClientRoomListPanel(mContext);
		roomPanels = new Vector<ClientRoomPanel>();
		roomPanels.add(new ClientRoomPanel(mContext, "test"));

		tabPane = new JTabbedPane(JTabbedPane.TOP);
	}

	private void setInitLayout() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Frame -> root Panel
		setTitle(" Secret Talk ");
		setSize(400, (int) (400 * 1.618));
		setContentPane(backgroundLabel); // add 처리
		setLayout(null); // 좌표값으로 배치
		setResizable(false); // 프레임 조절 불가
		setLocationRelativeTo(null); // JFrame을 모니터 가운데 자동 배치

		add(tabPane);
		tabPane.setSize(getWidth(), getHeight());
		tabPane.setLocation(0, 0);

		tabPane.addTab("유저 리스트", userListPanel);
		tabPane.addTab("방 리스트", roomListPanel);
		tabPane.addTab("test", roomPanels.elementAt(0));
		
		setVisible(true);
	}

	private void addEventListener() {

	}

	public static void main(String[] args) {
		new TestFrame(new Client());
	}

}
