package resolver.conf;

import com.wk.sdo.ServiceData;

/**
 * @description 关联交易报文配置
 * @author raoliang
 * @version 2015年2月9日 下午2:12:32
 */
public class ServerTranInfo {

	private ServiceData data;
	public ServerTranInfo(ServiceData data) {
		this.data = data;
	}
	
	public String getServer_Code() {
		return data.getString("SERVER_CODE");
	}
	
	public String getTran_Code() {
		return data.getString("TRAN_CODE");
	}
	
	public String getReq_Conf() {
		return getConfigString(data.getServiceData("REQ_PACKAGE_CONFIG"));
	}
	
	public String getResp_Conf() {
		return getConfigString(data.getServiceData("RESP_PACKAGE_CONFIG"));
	}
	
	public String getIn_Mapping_Script() {
		return getMappingScript(data.getServiceData("IN_MAPPING"));
	}
	
	public String getOut_Mapping_Script() {
		return getMappingScript(data.getServiceData("OUT_MAPPING"));
	}
	
	public String getErr_Mapping_Script() {
		return getMappingScript(data.getServiceData("ERROR_MAPPING"));
	}
	
	private String getConfigString(ServiceData data) {
		ServiceData contentData = data.getServiceData("STRUCTURE_CONTENT");
		return contentData == null ? null : contentData.getString("SDATAS");
	}
	
	private String getMappingScript(ServiceData data) {
		return data == null ? null : data.getString("MAPPING_SCRIPT");
	}
}
