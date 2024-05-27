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
import secret_talk.Client;

@Getter
@Setter
public class ClientFrame extends JFrame {

	private Client mContext;
	private ClientFrame clientFrame;

	public ClientFrame(Client mContext) {
		this.mContext = mContext;
		clientFrame = this;
		new connectServerPanel();
	}

	// 서버 연결을 위한 패널
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

		private void addEventListener() {
			connectBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String ip = inputIp.getText();
					int port = Integer.parseInt(inputPort.getText());
					mContext.connectServer(ip, port);
					setVisible(false);
				}
			});
		}
	}

}
