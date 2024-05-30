package secret_talk.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import lombok.Getter;
import lombok.Setter;
import secret_talk.Client;

@Getter
@Setter
public class ClientRoomPanel extends JPanel implements ActionListener {

	private Client mContext;

	private JLabel backgroundLabel;

	// 메세지 보드 컴포넌트
	private JLabel textLabelchat;
	private JScrollPane scrollPane;
	private JTextArea chatArea;

	// 메세지 컴포넌트
	private JLabel textLabelMsg;
	private JTextField messageField;
	private JButton messageBtn;
	
	// 방에서 나가기 버튼
	private JButton outRoomBtn;

	private String roomName;

	public ClientRoomPanel(Client mContext, String roomName) {
		this.mContext = mContext;
		this.roomName = roomName;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		// TODO 이미지 교체
		backgroundLabel = new JLabel();

		// 유저 리스트 컴포넌트
		textLabelchat = new JLabel("채팅창");
		chatArea = new JTextArea();
		scrollPane = new JScrollPane(chatArea);

		// 메세지 보내기 컴포넌트
		textLabelMsg = new JLabel("메세지 보내기");
		messageField = new JTextField();
		messageBtn = new JButton("메세지 보내기");
		
		// 나가기 버튼
		outRoomBtn = new JButton("나가기");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null); // 좌표값으로 배치

		add(backgroundLabel);
		backgroundLabel.setSize(getWidth(), getHeight());
		backgroundLabel.setLocation(0, 0);

		// 채팅창 텍스트 라벨
		add(textLabelchat);
		textLabelchat.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		textLabelchat.setSize(150, 23);
		textLabelchat.setLocation(15, 15);

		// 채팅창 (스크롤)
		add(scrollPane);
		scrollPane.setBorder(new LineBorder(Color.BLACK, 2));
		scrollPane.setSize(350, 380);
		scrollPane.setLocation(15, 50);

		// 메세지 보내기 텍스트 라벨
		add(textLabelMsg);
		textLabelMsg.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		textLabelMsg.setSize(160, 23);
		textLabelMsg.setLocation(15, 445);

		// 메세지 텍스트 필드
		add(messageField);
		messageField.setSize(350, 30);
		messageField.setLocation(15, 480);
		messageField.setFont(new Font("Noto Sans KR", Font.PLAIN, 17));

		// 메세지 보내기 버튼
		add(messageBtn);
		messageBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		messageBtn.setSize(165, 30);
		messageBtn.setLocation(15, 525);
		
		add(outRoomBtn);
		outRoomBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		outRoomBtn.setSize(165, 30);
		outRoomBtn.setLocation(200, 525);

	}

	private void addEventListener() {
		outRoomBtn.addActionListener(this);
		messageBtn.addActionListener(this);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String msg = messageField.getText();
					mContext.clickRoomMsgBtn(roomName, msg);
					messageField.setText("");
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == outRoomBtn) {
			mContext.clickOutRoomBtn(roomName);
		} else if (e.getSource() == messageBtn) {
			String msg = messageField.getText();
			if (msg.equals("")) {
				(new MessageFrame()).errorMsg("null");
				return;
			}
			mContext.clickRoomMsgBtn(roomName, msg);
			messageField.setText("");
		}
	}

}
