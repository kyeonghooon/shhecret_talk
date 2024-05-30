package secret_talk.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import lombok.Getter;
import lombok.Setter;
import secret_talk.Client;

@Getter
@Setter
public class ClientRoomListPanel extends JPanel implements ActionListener {

	private Client mContext;

	private JLabel backgroundLabel;

	// 방 리스트 컴포넌트
	private JLabel textLabelList;
	private JScrollPane scrollPane;
	private JList<String> roomList;

	// 방이름 컴포넌트
	private JLabel textLabelRoom;
	private JTextField roomNameField;

	// 비밀번호 컴포넌트
	private JLabel textLabelPw;
	private JTextField passwordField;

	// 방 생성, 들어가기 버튼
	private JButton newRoomBtn;
	private JButton enterRoomBtn;

	// 안내 문구
	private JLabel infoLabel;

	public ClientRoomListPanel(Client mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		// TODO 이미지 교체
		backgroundLabel = new JLabel();

		// 방 리스트 컴포넌트
		textLabelList = new JLabel("방 리스트");
		roomList = new JList<String>();
		scrollPane = new JScrollPane(roomList);

		// 방이름 컴포넌트
		textLabelRoom = new JLabel("방 이름 입력");
		roomNameField = new JTextField();

		// 비밀번호 관련 컴포넌트
		textLabelPw = new JLabel("비밀번호 입력");
		passwordField = new JTextField();

		// 상호작용 버튼
		newRoomBtn = new JButton("방 만들기");
		enterRoomBtn = new JButton("입장");

		// 안내 문구
		infoLabel = new JLabel("※ 비밀번호는 4자리의 숫자입니다.");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null); // 좌표값으로 배치

		add(backgroundLabel);
		backgroundLabel.setSize(getWidth(), getHeight());
		backgroundLabel.setLocation(0, 0);

		// 방 리스트 텍스트 라벨
		add(textLabelList);
		textLabelList.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		textLabelList.setSize(150, 23);
		textLabelList.setLocation(15, 15);

		// 방 리스트 (스크롤)
		roomList.setFont(new Font("Noto Sans KR", Font.BOLD, 15));
		add(scrollPane);
		scrollPane.setBorder(new LineBorder(Color.BLACK, 2));
		scrollPane.setSize(350, 380);
		scrollPane.setLocation(15, 50);
		
		// 방 이름 입력 텍스트 라벨
		add(textLabelRoom);
		textLabelRoom.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		textLabelRoom.setSize(120, 30);
		textLabelRoom.setLocation(15, 445);

		// 방 이름 입력 필드
		add(roomNameField);
		roomNameField.setSize(165, 30);
		roomNameField.setLocation(15, 480);
		roomNameField.setFont(new Font("Noto Sans KR", Font.PLAIN, 17));

		// 비밀번호 입력 텍스트 라벨
		add(textLabelPw);
		textLabelPw.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		textLabelPw.setSize(120, 30);
		textLabelPw.setLocation(200, 445);

		// 비밀번호 입력 필드
		add(passwordField);
		passwordField.setSize(165, 30);
		passwordField.setLocation(200, 480);
		passwordField.setFont(new Font("Noto Sans KR", Font.PLAIN, 17));

		// 개인 메세지 버튼
		add(newRoomBtn);
		newRoomBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		newRoomBtn.setSize(165, 30);
		newRoomBtn.setLocation(15, 515);

		// 단체 메세지 버튼
		add(enterRoomBtn);
		enterRoomBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		enterRoomBtn.setSize(165, 30);
		enterRoomBtn.setLocation(200, 515);

		add(infoLabel);
		infoLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 11));
		infoLabel.setSize(350, 30);
		infoLabel.setLocation(15, 550);
	}

	private void addEventListener() {
		newRoomBtn.addActionListener(this);
		enterRoomBtn.addActionListener(this);
		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (roomList.getSelectedValue() != null) {
					String[] roomName = roomList.getSelectedValue().split("]");
					roomNameField.setText(roomName[1]);
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == newRoomBtn) {
			String roomName = roomNameField.getText();
			String password = passwordField.getText();
			if (roomName.equals("")) {
				(new MessageFrame()).errorMsg("null");
				return;
			}
			if (roomName.length() > 10) {
				(new MessageFrame()).errorMsg("roomNameOver");
				return;
			}
			
			// 비밀번호가 없을때
			if (password.equals("")) {
				password = mContext.PW_NULL;
				mContext.clickNewRoomBtn(roomName, password);
				roomNameField.setText("");
				passwordField.setText("");
			} else {
				int passwordInt = Integer.parseInt(password);
				if ((999 < passwordInt && passwordInt < 10000)) {
					mContext.clickNewRoomBtn(roomName, password);
					roomNameField.setText("");
					passwordField.setText("");
				}
			}
		} else if (e.getSource() == enterRoomBtn) {
			String roomName = roomNameField.getText();
			if (roomName.equals("")) {
				return;
			}
			String password = passwordField.getText();
			if (password.equals("")) {
				password = mContext.PW_NULL;
			}
			mContext.clickEnterRoomBtn(roomName, password);
			roomList.setSelectedValue(null, false);
			roomNameField.setText("");
			passwordField.setText("");
		}
	}

}
