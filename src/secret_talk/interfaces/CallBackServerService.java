package secret_talk.interfaces;

public interface CallBackServerService {
	void clickKickBtn(String userId);
	void clickPersonalMsgBtn(String id, String msg);
	void clickGroupMsgBtn(String msg);
}
