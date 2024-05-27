package secret_talk.panels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import lombok.*;

@Getter
@Setter
public class ServerLogPanel extends JPanel{
	
	private JLabel backgroundLabel;
	private JLabel textLabel;
	private JScrollPane scrollPane;
	private JTextArea logBoard;
	
	public ServerLogPanel() {
		initData();
		setInitLayout();
		addEventListener();
	}
	
	private void initData() {
		// TODO 이미지 교체
		backgroundLabel = new JLabel();
		textLabel = new JLabel("서버 로그");
		logBoard = new JTextArea();
		scrollPane = new JScrollPane(logBoard);
	}
	private void setInitLayout() {
		setSize(350, 320);
		setLayout(null); // 좌표값으로 배치
		
		add(backgroundLabel);
		backgroundLabel.setSize(350, 320);
		backgroundLabel.setLocation(0, 0);
		
		add(textLabel);
		textLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 15));
		textLabel.setSize(70, 15);
		textLabel.setLocation(132, 18);
		
		add(scrollPane);
		logBoard.setEnabled(false);
		scrollPane.setBorder(new LineBorder(Color.BLACK, 3));
		scrollPane.setSize(300, 260);
		scrollPane.setLocation(15, 50);
		
	}
	private void addEventListener() {
		
	}
}
