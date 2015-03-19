package resolver.msg;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description
 * @author raoliang
 * @version 2015年3月2日 下午3:22:06
 */
public class ResponseInfo {
	private String recv_time;
	private int msg_id;
	private String ret_code;
	private String ret_msg;
	
	public ResponseInfo() {
		setRecv_time(getTime());
	}
	
	public String getRecv_time() {
		return recv_time;
	}
	public void setRecv_time(String recv_time) {
		this.recv_time = recv_time;
	}
	public int getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(int msg_id) {
		this.msg_id = msg_id;
	}
	public String getRet_code() {
		return ret_code;
	}
	public void setRet_code(String ret_code) {
		this.ret_code = ret_code;
	}
	public String getRet_msg() {
		return ret_msg;
	}
	public void setRet_msg(String ret_msg) {
		this.ret_msg = ret_msg;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("recv_time:").append(getRecv_time()).append("\n");
		sb.append("msg_id:").append(getMsg_id()).append("\n");
		sb.append("ret_code:").append(getRet_code()).append("\n");
		sb.append("ret_msg:").append(getRet_msg()).append("\n");
		return sb.toString();
	}
	
	private static String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
	
}
