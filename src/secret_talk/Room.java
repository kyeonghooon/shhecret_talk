package secret_talk;

import java.util.Vector;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {
	private Vector<User> userList;

	private String roomName;
	private int passWord;

	public Room(String roomName, int passWord, User user) {
		this.roomName = roomName;
		this.passWord = passWord;
		userList.add(user);
	}

	// 방에 있는 사람에게 출력
	public void roomBroadCast(String msg) {
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.elementAt(i);
			user.getWriter().println(msg);
		}
	}
}
