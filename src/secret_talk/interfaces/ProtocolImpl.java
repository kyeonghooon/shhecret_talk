package secret_talk.interfaces;

public interface ProtocolImpl {
	
	/** 
	 * 새로운 유저 접속시 사용<br>
	 */
	void newUser(); 	// 기존 유저에게 알리기 위함
	void userList(); 	// 유저 리스트를 주고 받을때 사용
	void roomList(); 	// 리스트를 주고 받을때 사용
	
	// 유저 로그아웃시
	void logOutUser();
	
	/**
	 * 방 생성, 입장, 나가기<br>
	 */
	void newRoom();
	void enterRoom();
	void outRoom();
	// 방에 아무도 없다면 방 제거
	void removeRoom();
	
	/**
	 * 방에서 주고 받는 모든 메세지, 알림 포함<br>
	 */
	void roomMsg();
	
	/**
	 * 방 외부에서 주고 받는 메세지<br>
	 */
	void personalMsg();
	void groupMsg();
}
