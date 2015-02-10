package resolver.conf;

import com.wk.sdo.ServiceData;

/**
 * @description
 * @author raoliang
 * @version 2015年2月9日 上午11:10:04
 */
public class ServerInfo {

	private ServiceData data;
	
	public ServerInfo(ServiceData data) {
		this.data = data;
	}
	
	public String getServerCode() {
		return data.getString("SERVER_CODE");
	}
	
	public String getSuccessExp() {
		return data.getString("SUCCESS_EXP");
	}
	
	public String getPut_Svc_Exp() {
		return data.getString("PUT_SVC_EXP");
	}
	
	public String getMSG_CLASS() {
		return data.getString("MSG_CLASS");
	}
	
	public String getSERVER_ACTOR_CLASS() {
		return data.getString("SERVER_ACTOR_CLASS");
	}
	
	public String getReq_Package_Conf() {
		return getConfigString(data.getServiceData("REQ_PACKAGE_CONFIG"));
	}
	
	public String getResp_Package_Conf() {
		return getConfigString(data.getServiceData("RESP_PACKAGE_CONFIG"));
	}
	
	public String getErr_Package_Conf() {
		return getConfigString(data.getServiceData("ERR_PACKAGE_CONFIG"));
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
		return data.getServiceData("STRUCTURE_CONTENT").getString("SDATAS");
	}
	
	private String getMappingScript(ServiceData data) {
		return data.getString("MAPPING_SCRIPT");
	}
}
