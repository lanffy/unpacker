package unpacker.msg;

import unpacker.conf.LoadMode;

import com.wk.conv.PacketChannelBuffer;
import com.wk.conv.mode.PackageMode;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年2月3日 下午5:40:31
 */
public abstract class Msg {
	static {
		LoadMode.loadMode();
	}
	protected final Log logger = LogFactory.getLog("unpacker");
	public String reqModeName;
	public String respModeName;
	public String errModeName;
	/**
	 * 请求包模式
	 */
	public PackageMode requestMode = getReqPackageMode(reqModeName);
	/**
	 * 响应包模式
	 */
	public PackageMode responseMode = getRespPackageMode(respModeName);
	/**
	 * 错误包模式
	 */
	public PackageMode errorMode = getErrPackageMode(errModeName);
	
	/**
	* @description 获取请求包模式
	* @return
	*/
	public abstract PackageMode getReqPackageMode(String reqModeName);
	/**
	* @description 获取响应包模式
	* @return
	*/
	public abstract PackageMode getRespPackageMode(String respModeName);
	/**
	* @description 获取错误包模式
	* @return
	*/
	public abstract PackageMode getErrPackageMode(String errModeName);
	
	/**
	* @description 拆请求报文
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackRequest(PacketChannelBuffer buffer);
	
	/**
	* @description 拆响应报文
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackResponse(PacketChannelBuffer buffer);
	
	/**
	* @description 拆错误报文
	* @param buffer
	* @return
	*/
	public abstract ServiceData unpackError(PacketChannelBuffer buffer);
}
