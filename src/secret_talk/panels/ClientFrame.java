package secret_talk.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import lombok.Getter;
import lombok.Setter;
import secret_talk.Client;

@Getter
@Setter
public class ClientFrame extends JFrame {

	// 참조 변수
	private Client mContext;
	private ClientFrame clientFrame;
	private ClientUserListPanel userListPanel;
	private ClientRoomListPanel roomListPanel;
	private Vector<ClientRoomPanel> roomPanels;

	private JLabel backgroundLabel;
	private JTabbedPane tabPane;

	// 연결 확인 -> id입력으로 넘어감
	private boolean isConnected;

	public ClientFrame(Client mContext) {
		this.mContext = mContext;
		clientFrame = this;
		new connectServerPanel();
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

		tabPane = new JTabbedPane(JTabbedPane.TOP);
	}

	private void setInitLayout() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Frame -> root Panel
		setTitle(" Secret Talk ");
		setSize(400, (int)(400*1.618));
		setContentPane(backgroundLabel); // add 처리
		setLayout(null); // 좌표값으로 배치
		setResizable(false); // 프레임 조절 불가
		setLocationRelativeTo(null); // JFrame을 모니터 가운데 자동 배치
		
		add(tabPane);
		tabPane.setSize(getWidth(), getHeight());
		tabPane.setLocation(0, 0);
		tabPane.setFont(new Font("Noto Sans KR", Font.BOLD, 14));
		
		tabPane.addTab("유저 리스트", userListPanel);
		tabPane.addTab("방 리스트", roomListPanel);
	}

	private void addEventListener() {

	}

	// 서버 연결을 위한 패널 - 최초 호출
	private class connectServerPanel extends JFrame {
		private JLabel backgroundLabel;

		// 입력값과 관련된 컴포넌트
		private JLabel ipAddress;
		private JTextField inputIp;
		private JLabel portNumber;
		private JTextField inputPort;
		private JButton connectBtn;

		public connectServerPanel() {
			initData();
			setInitLayout();
			addEventListener();
		}

		private void initData() {
			// TODO 이미지 교체
			backgroundLabel = new JLabel();
			ipAddress = new JLabel("IP");
			inputIp = new JTextField();
			portNumber = new JLabel("PORT NUMBER");
			inputPort = new JTextField();
			connectBtn = new JButton("CONNECT");
		}

		private void setInitLayout() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// Frame -> root Panel
			setTitle(" Secret Talk Connect Server ");
			setSize(285, 190);
			setContentPane(backgroundLabel); // add 처리
			setLayout(null); // 좌표값으로 배치
			setResizable(false); // 프레임 조절 불가
			setLocationRelativeTo(null); // JFrame을 모니터 가운데 자동 배치

			add(ipAddress);
			ipAddress.setSize(30, 30);
			ipAddress.setLocation(80, 20);
			ipAddress.setFont(new Font("Noto Sans KR", Font.BOLD, 14));

			add(inputIp);
			inputIp.setSize(90, 30);
			inputIp.setLocation(150, 20);
			inputIp.setFont(new Font("Noto Sans KR", Font.BOLD, 14));
			inputIp.setText("127.0.0.1"); // 초기 값 변경 가능

			add(portNumber);
			portNumber.setSize(110, 30);
			portNumber.setLocation(30, 60);
			portNumber.setFont(new Font("Noto Sans KR", Font.BOLD, 14));

			add(inputPort);
			inputPort.setSize(90, 30);
			inputPort.setLocation(150, 60);
			inputPort.setFont(new Font("Noto Sans KR", Font.BOLD, 14));
			inputPort.setText("5000"); // 초기 값 변경 가능

			add(connectBtn);
			connectBtn.setSize(210, 30);
			connectBtn.setLocation(30, 100);
			connectBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 14));

			setVisible(true);
		}

		// 버튼 클릭 이벤트 발생시
		private void addEventListener() {
			connectBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String ip = inputIp.getText();
					int port = Integer.parseInt(inputPort.getText());
					mContext.connectServer(ip, port);
					if (isConnected) {
						setVisible(false);
						new CreateIdPanel();
					}
				}
			});
		}
	}

	// id 생성을 위한 패널
	private class CreateIdPanel extends JFrame {

		private JLabel backgroundLabel;

		// PORT NUMBER와 관련된 컴포넌트
		private JLabel userId;
		private JTextField inputId;
		private JButton createBtn;

		public CreateIdPanel() {
			initData();
			setInitLayout();
			addEventListener();
		}

		private void initData() {
			// TODO 이미지 교체
			backgroundLabel = new JLabel();
			userId = new JLabel("I D");
			inputId = new JTextField();
			createBtn = new JButton("CREATE");
		}

		private void setInitLayout() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// Frame -> root Panel
			setTitle(" Secret Talk Server Creater ");
			setSize(280, 100);
			setContentPane(backgroundLabel); // add 처리
			setLayout(null); // 좌표값으로 배치
			setResizable(false); // 프레임 조절 불가
			setLocationRelativeTo(null); // JFrame을 모니터 가운데 자동 배치

			add(userId);
			userId.setSize(30, 20);
			userId.setLocation(20, 20);
			userId.setFont(new Font("Noto Sans KR", Font.BOLD, 12));

			add(inputId);
			inputId.setSize(90, 20);
			inputId.setLocation(60, 20);
			inputId.setFont(new Font("Noto Sans KR", Font.BOLD, 12));

			add(createBtn);
			createBtn.setSize(90, 20);
			createBtn.setLocation(160, 20);
			createBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 12));

			setVisible(true);
		}

		private void addEventListener() {
			// CREATE 버튼 클릭시
			createBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					mContext.createId(inputId.getText());
					if (mContext.checkId()) {
						setVisible(false);
						clientFrame.setVisible(true);
						clientFrame.setTitle(" Secret Talk " + mContext.getMyId());
					}
				}
			});
		}

	}

}
