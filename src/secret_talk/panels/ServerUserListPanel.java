package secret_talk.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import lombok.Getter;
import lombok.Setter;
import secret_talk.Server;

@Getter
@Setter
public class ServerUserListPanel extends JPanel implements ActionListener{
	
	private Server mContext;
	
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
	
	// 강퇴 컴포넌트
	private JLabel textLabelKick;
	private JButton kickBtn;

	public ServerUserListPanel(Server mContext) {
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
		
		// 강퇴하기 컴포넌트
		textLabelKick = new JLabel("강퇴 하기");
		kickBtn = new JButton("강퇴");
	}

	private void setInitLayout() {
		setSize(350, 566);
		setLayout(null); // 좌표값으로 배치

		add(backgroundLabel);
		backgroundLabel.setSize(350, 200);
		backgroundLabel.setLocation(0, 0);
		
		// 유저 리스트 텍스트 라벨
		add(textLabelList);
		textLabelList.setFont(new Font("Noto Sans KR", Font.BOLD, 15));
		textLabelList.setSize(90, 15);
		textLabelList.setLocation(47, 0);
		
		// 유저 리스트 (스크롤)
		add(scrollPane);
		userList.setFont(new Font("Noto Sans KR", Font.BOLD, 12));
		scrollPane.setBorder(new LineBorder(Color.BLACK, 2));
		scrollPane.setSize(150, 150);
		scrollPane.setLocation(15, 30);
		
		// 메세지 보내기 텍스트 라벨
		add(textLabelMsg);
		textLabelMsg.setFont(new Font("Noto Sans KR", Font.BOLD, 15));
		textLabelMsg.setSize(100, 15);
		textLabelMsg.setLocation(200, 0);
		
		// 메세지 텍스트 필드
		add(messageField);
		messageField.setSize(136, 25);
		messageField.setLocation(180, 30);
		
		// 개인 메세지 버튼
		add(personalMsgBtn);
		personalMsgBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 12));
		personalMsgBtn.setSize(60, 25);
		personalMsgBtn.setLocation(180, 65);
		
		// 단체 메세지 버튼
		add(groupMsgBtn);
		groupMsgBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 12));
		groupMsgBtn.setSize(60, 25);
		groupMsgBtn.setLocation(255, 65);
		
		// 강퇴 텍스트 필드
		add(textLabelKick);
		textLabelKick.setFont(new Font("Noto Sans KR", Font.BOLD, 15));
		textLabelKick.setSize(100, 25);
		textLabelKick.setLocation(215, 120);
		
		// 강퇴 버튼
		add(kickBtn);
		kickBtn.setFont(new Font("Noto Sans KR", Font.BOLD, 12));
		kickBtn.setSize(136, 25);
		kickBtn.setLocation(180, 155);
	}

	private void addEventListener() {
		kickBtn.addActionListener(this);
		personalMsgBtn.addActionListener(this);
		groupMsgBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == kickBtn) {
			String id = userList.getSelectedValue();
			if ( id.equals("")) {
				return;
			}
			mContext.clickKickBtn(id);
		} else if (e.getSource() == personalMsgBtn) {
			String id = userList.getSelectedValue();
			String msg = messageField.getText();
			if ( id.equals("") || msg.equals("")) {
				(new MessageFrame()).errorMsg("null");
				return;
			}
			mContext.clickPersonalMsgBtn(id, msg);
			userList.setSelectedValue(null, false);
			messageField.setText("");
		} else if (e.getSource() == groupMsgBtn) {
			String msg = messageField.getText();
			if (msg.equals("")) {
				(new MessageFrame()).errorMsg("null");
				return;
			}
			mContext.clickGroupMsgBtn(msg);
			messageField.setText("");
		}
	}
}
