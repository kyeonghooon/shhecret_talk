package secret_talk.interfaces;

public interface CallBackClientService {
	void clickNewRoomBtn(String roomName, String password);
	void clickEnterRoomBtn(String roomName, String password);
	void clickOutRoomBtn(String roomName);
	void clickRoomMsgBtn(String roomName, String msg);
	void clickPersonalMsgBtn(String id, String msg);
	void clickGroupMsgBtn(String msg);
}
