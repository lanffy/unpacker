package resolver.excption;

import com.wk.lang.SystemException;

/**
* @description
* @author raoliang
* @version 2015年3月2日 下午2:42:24
*/
@SuppressWarnings("serial")
public class UnpackResponseException extends SystemException{

	private static String ERROR_CODE = "SYS_RESOLVER_UNPACK_RESPONSE_EXCEPTION";
	
	public UnpackResponseException() {
		super(ERROR_CODE);
	}
	
	public UnpackResponseException(Throwable e) {
		super(ERROR_CODE, e);
	}
	
	public UnpackResponseException(String msgid) {
		super(msgid);
	}

	public UnpackResponseException(String msgid, Throwable e) {
		super(msgid, e);
	}

}
