package secret_talk.panels;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MessageFrame extends JFrame{
	
	private JLabel backgroundLabel;
	
	private JScrollPane scrollPane;
	private JTextArea msgBoard;
	private JLabel textLabel;
	private JLabel imgLabel;
	
	public MessageFrame() {
		initData();
		setInitLayout();
		addEventListener();
	}
	private void initData() {
		backgroundLabel = new JLabel(new ImageIcon("images/bg.png"));
		
		msgBoard = new JTextArea();
		textLabel = new JLabel();
		scrollPane = new JScrollPane(msgBoard);
		imgLabel = new JLabel();
	}
	
	private void setInitLayout() {
		// Frame -> root Panel
		setTitle("메세지");
		setSize(500, 240);
		setContentPane(backgroundLabel); // add 처리
		setLayout(null); // 좌표값으로 배치
		setResizable(false); // 프레임 조절 불가
		setLocationRelativeTo(null); // JFrame을 모니터 가운데 자동 배치
		
		add(textLabel);
		textLabel.setFont(new Font("Noto Sans KR", Font.BOLD, 15));
		textLabel.setSize(450, 30);
		textLabel.setLocation(65, 15);
		
		add(scrollPane);
		msgBoard.setFont(new Font("Noto Sans KR", Font.BOLD, 20));
		msgBoard.setLineWrap(true);
		msgBoard.setEnabled(false);
		scrollPane.setSize(450, 120);
		scrollPane.setLocation(20, 60);
		
		add(imgLabel);
		imgLabel.setSize(36, 36);
		imgLabel.setLocation(20, 10);
		
	}
	
	private void addEventListener() {
		
	}
	
	public void personalMsg(String fromId, String msg) {
		textLabel.setText(fromId + "으로 부터온 개인 메세지");
		msgBoard.append(msg);
		imgLabel.setIcon(new ImageIcon("images/msg.png"));
		setVisible(true);
	}
	public void groupMsg(String fromId, String msg) {
		textLabel.setText(fromId + "으로 부터온 단체 메세지");
		msgBoard.append(msg);
		imgLabel.setIcon(new ImageIcon("images/msg.png"));
		setVisible(true);
	}
	public void errorMsg(String error) {
		switch (error) {
//		case "test":
//			textLabel.setText("테스트용 에러 메세지입니다 몇글자 까지 가능할까요");
//			msgBoard.append("테스트용 에러 메세지입니다");
//			imgLabel.setIcon(new ImageIcon("images/error.png"));
//			break;
		case "connectServer" :
			textLabel.setText("서버 연결 불가");
			msgBoard.append("ip주소, port 번호를 다시 확인해주세요\n");
			msgBoard.append("서버에 연결이 불가능 한 상태 일수도 있어요\n");
			imgLabel.setIcon(new ImageIcon("images/error.png"));
			break;
		case "serverNull":
			textLabel.setText("서버를 찾을 수 없음");
			msgBoard.append("서버로부터 접속이 끊겼습니다.\n");
			imgLabel.setIcon(new ImageIcon("images/error.png"));
			break;
		case "reject":
			textLabel.setText("id 사용 불가");
			msgBoard.append("이미 사용중인 id입니다.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "blank":
			textLabel.setText("id 사용 불가");
			msgBoard.append("id에 공백은 사용할 수 없습니다.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "letterOver":
			textLabel.setText("id 사용 불가");
			msgBoard.append("id는 6글자 이하로만 만들어 주세요.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "roomNameOver":
			textLabel.setText("방이름 사용 불가");
			msgBoard.append("방이름은 10글자 이하로만 만들어 주세요.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "roomNameUsed":
			textLabel.setText("방이름 사용 불가");
			msgBoard.append("동일한 방이름이 존재 합니다.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "failEnterRoom":
			textLabel.setText("방 입장 불가");
			msgBoard.append("방이름과 비밀번호를 다시 확인해주세요");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "enteredRoom":
			textLabel.setText("방 입장 불가");
			msgBoard.append("이미 입장한 방입니다.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "null":
			textLabel.setText("빈칸 입력 불가");
			msgBoard.append("텍스트 필드에 아무것도 입력되지 않았습니다.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "self":
			textLabel.setText("메세지 전송 불가");
			msgBoard.append("자기 자신에게 메세지를 보낼 수 없습니다.");
			imgLabel.setIcon(new ImageIcon("images/reject.png"));
			break;
		case "kick":
			textLabel.setText("추 방");
			msgBoard.append("서버관리자로 부터 추방 되었습니다.");
			imgLabel.setIcon(new ImageIcon("images/kick.png"));
			break;
		}
		setVisible(true);
	}

}
