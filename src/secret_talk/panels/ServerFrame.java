package secret_talk.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import lombok.Getter;
import lombok.Setter;
import secret_talk.Server;

@Getter
@Setter
public class ServerFrame extends JFrame {

	private Server mContext;
	private ServerFrame serverFrame;
	private ServerLogPanel logPanel;
	private ServerUserListPanel userListPanel;

	private JLabel backgroundLabel;

	private boolean serverCreated;
	private int port;

	public ServerFrame(Server mContext) {
		this.mContext = mContext;
		serverFrame = this;
		new SetupServerPanel();
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		// TODO 이미지 교체
		backgroundLabel = new JLabel();

		logPanel = new ServerLogPanel();
		userListPanel = new ServerUserListPanel();
	}

	private void setInitLayout() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Frame -> root Panel
		setTitle(" Secret Talk Server Manager ");
		setSize(350, 566);
		setContentPane(backgroundLabel); // add 처리
		setLayout(null); // 좌표값으로 배치
		setResizable(false); // 프레임 조절 불가
		setLocationRelativeTo(null); // JFrame을 모니터 가운데 자동 배치

		backgroundLabel.add(logPanel);
		backgroundLabel.add(userListPanel);
		logPanel.setLocation(0, 205);
		userListPanel.setLocation(0, 15);
	}

	private void addEventListener() {

	}

	// 서버 소켓 생성을 위한 패널
	private class SetupServerPanel extends JFrame {

		private JLabel backgroundLabel;

		// PORT NUMBER와 관련된 컴포넌트
		private JLabel portNumber;
		private JTextField inputPort;
		private JButton createBtn;

		public SetupServerPanel() {
			initData();
			setInitLayout();
			addEventListener();
		}

		private void initData() {
			// TODO 이미지 교체
			backgroundLabel = new JLabel();
			portNumber = new JLabel("PORT NUMBER");
			inputPort = new JTextField();
			createBtn = new JButton("CREATE");
		}

		private void setInitLayout() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// Frame -> root Panel
			setTitle(" Secret Talk Server Creater ");
			setSize(380, 100);
			setContentPane(backgroundLabel); // add 처리
			setLayout(null); // 좌표값으로 배치
			setResizable(false); // 프레임 조절 불가
			setLocationRelativeTo(null); // JFrame을 모니터 가운데 자동 배치

			add(portNumber);
			portNumber.setSize(90, 20);
			portNumber.setLocation(30, 20);
			portNumber.setFont(new Font("Noto Sans KR", Font.BOLD, 12));

			add(inputPort);
			inputPort.setSize(90, 20);
			inputPort.setLocation(130, 20);
			inputPort.setFont(new Font("Noto Sans KR", Font.BOLD, 12));
			inputPort.setText("5000"); // 초기 값 변경 가능

			add(createBtn);
			createBtn.setSize(90, 20);
			createBtn.setLocation(240, 20);
			createBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 12));

			setVisible(true);
		}

		private void addEventListener() {
			// CREATE 버튼 클릭시
			createBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					port = Integer.parseInt(inputPort.getText());
					mContext.setupServer(port);
					setVisible(false);
					serverFrame.setVisible(true);
				}
			});
		}
	}

}
