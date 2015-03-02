package resolver.excption;

import com.wk.lang.SystemException;

/**
 * @description
 * @author raoliang
 * @version 2015年3月2日 下午2:39:44
 */
@SuppressWarnings("serial")
public class UnpackRequestException extends SystemException{

	private static String ERROR_CODE = "SYS_RESOLVER_UNPACK_REQUEST_EXCEPTION";
	
	public UnpackRequestException() {
		super(ERROR_CODE);
	}
	
	public UnpackRequestException(Throwable e) {
		super(ERROR_CODE, e);
	}
	
	public UnpackRequestException(String msgid) {
		super(msgid);
	}

	public UnpackRequestException(String msgid, Throwable e) {
		super(msgid, e);
	}

}
