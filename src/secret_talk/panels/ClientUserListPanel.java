package secret_talk.panels;

import java.awt.Color;
import java.awt.Font;

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
public class ClientUserListPanel extends JPanel {
	
	private Client mContext;

	private JLabel backgroundLabel;

	// 유저 리스트 컴포넌트
	private JLabel textLabelList;
	private JScrollPane scrollPane;
	private JList<String> userList;

	// 메세지 컴포넌트
	private JLabel textLabelMsg;
	private JTextField messageField;
	private JButton personalMsgBtn;
	private JButton groupMsgBtn;

	public ClientUserListPanel(Client mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		// TODO 이미지 교체
		backgroundLabel = new JLabel();

		// 유저 리스트 컴포넌트
		textLabelList = new JLabel("유저 리스트");
		userList = new JList<String>();
		scrollPane = new JScrollPane(userList);

		// 메세지 보내기 컴포넌트
		textLabelMsg = new JLabel("메세지 보내기");
		messageField = new JTextField();
		personalMsgBtn = new JButton("개인");
		groupMsgBtn = new JButton("단체");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null); // 좌표값으로 배치

		add(backgroundLabel);
		backgroundLabel.setSize(getWidth(), getHeight());
		backgroundLabel.setLocation(0, 0);

		// 유저 리스트 텍스트 라벨
		add(textLabelList);
		textLabelList.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		textLabelList.setSize(150, 23);
		textLabelList.setLocation(15, 15);

		// 유저 리스트 (스크롤)
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

		// 개인 메세지 버튼
		add(personalMsgBtn);
		personalMsgBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		personalMsgBtn.setSize(165, 30);
		personalMsgBtn.setLocation(15, 525);

		// 단체 메세지 버튼
		add(groupMsgBtn);
		groupMsgBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		groupMsgBtn.setSize(165, 30);
		groupMsgBtn.setLocation(200, 525);
	}

	private void addEventListener() {

	}

}
