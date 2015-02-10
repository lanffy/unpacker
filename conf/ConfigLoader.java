package resolver.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import resolver.util.JSONFileUtil;

import com.wk.conv.config.ArrayConfig;
import com.wk.conv.config.ConvConfig;
import com.wk.conv.config.FieldConfig;
import com.wk.conv.config.StructConfig;
import com.wk.conv.mode.FieldEndProcessMode;
import com.wk.conv.mode.FieldMode;
import com.wk.conv.mode.FieldProcessMode;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.eai.config.PackageConfig;
import com.wk.eai.startup.FieldInfo;
import com.wk.eai.startup.IOFieldInfo;
import com.wk.lang.SystemException;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;
import com.wk.util.ConverterUtil;
import com.wk.util.JSON;
import com.wk.util.StringUtil;

/**
 * @description 加载报文配置
 * @author raoliang
 * @version 2015年2月4日 下午4:06:26
 */
public class ConfigLoader extends Loader{
	
	private static final String baseServerPath = basePath + "server/";
	private static final String baseTranConfPath = basePath + "tranConf/";
	
	private static final String serverPath = config.getProperty("resolver.serverPath", baseServerPath);
	private static final String tranConfPath = config.getProperty("resolver.tranConfPath", baseTranConfPath);
	
	public static void loadConf() {
		loadServer();
		loadTranConf();
	}
	
	private static void loadServer() {
		List<File> fileList = getFileList(serverPath);
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			ServerInfo serverInfo = new ServerInfo(data);
			String serverCode = serverInfo.getServerCode();
			StructConfig req_conf = json2IOConfig(serverInfo.getReq_Package_Conf());
			StructConfig resp_conf = json2IOConfig(serverInfo.getResp_Package_Conf());
			StructConfig err_conf = json2IOConfig(serverInfo.getErr_Package_Conf());
			final PackageConfig head_config = new PackageConfig(req_conf, resp_conf, err_conf); 
			Configs.putHeadConfig(serverCode, head_config);
			logger.info("加载服务系统报文头配置：[ {} ].", serverCode);
		}
	}
	
	private static void loadTranConf() {
		List<File> fileList = getFileList(tranConfPath);
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			ServerTranInfo tranInfo = new ServerTranInfo(data);
			String serverCode = tranInfo.getServer_Code();
			String tranCode = tranInfo.getTran_Code();
			StructConfig req_conf = json2IOConfig(tranInfo.getReq_Conf());
			StructConfig resp_conf = json2IOConfig(tranInfo.getResp_Conf());
			final PackageConfig body_config = new PackageConfig(req_conf, resp_conf);
			Configs.putBodyConfig(serverCode, tranCode, body_config);
			logger.info("加载交易报文体配置，服务系统：[ {} ],交易码：[ {} ].", serverCode, tranCode);
		}
	}
	
	private static StructConfig json2IOConfig(String json) {
		if (StringUtil.isEmpty(json)) {
			return null;
		}

		ServiceData data = JSON.toServiceData(json);
		String str = data.getString("is_strict");
		boolean is_strict = false;
		if (!StringUtil.isEmpty(str)) {
			//0-否， 1-是
			is_strict = (str.charAt(0) == '1');
		}
		String mode_name = data.getString("package_mode").trim();
		PackageMode mode = StringUtil.isEmpty(mode_name) ? null : Modes.getPackageMode(mode_name);
		StructConfig config = new StructConfig(mode, is_strict);
		ServiceData[] array = data.getServiceDataArray("fdatas");
		if (array != null) {
			IOFieldInfo[] infos = new IOFieldInfo[array.length];
			for (int i = 0; i < infos.length; i++) {
				infos[i] = ConverterUtil.serviceData2Bean(array[i], IOFieldInfo.class);
			}
			for (IOFieldInfo info : infos) {
				if (StringUtil.isEmpty(info.getField_parent())) {
					config.putChild(parseIOConfig(is_strict, info, infos));
				}
			}
		}
		return config;
	}
	
	/**
	 * 报文接口
	 */
	private static ConvConfig parseIOConfig(boolean is_strict, IOFieldInfo field, IOFieldInfo[] infos) {
		if (field == null) {
			return null;
		}

		final String name = field.getField_code();
		final String id = field.getField_id();
		final String type = field.getField_category();
		//0-否，1-是
		final String req_string = field.getFreq();
		boolean is_required;
		if (StringUtil.isEmpty(req_string)) {
			is_required = false;
		} else {
			is_required = req_string.charAt(0) == '1';
		}
		switch (type.charAt(0)) {
		case '0':
			PackageMode mode = null;
			if (!StringUtil.isEmpty(field.getPmode())) {
				mode = Modes.getPackageMode(field.getPmode());
			}
			StructConfig struct = new StructConfig(name, mode, is_strict, field.getFexp(), is_required);
			for(IOFieldInfo fld : findFieldByParent(infos, id)) {
				struct.putChild(parseIOConfig(is_strict, fld, infos));
			}
			return struct;
		case '1':
			List<IOFieldInfo> lst_fld = findFieldByParent(infos, id);
			IOFieldInfo element = lst_fld.size()>0 ? lst_fld.get(0) : null;
			return new ArrayConfig(name, field.getFasize(), field.getFaexp()
								   , parseIOConfig(is_strict, element, infos)
								   , field.getFexp(), is_required);
		case '2':
			FieldMode field_mode = null;
			if (!StringUtil.isEmpty(field.getFmode())) {
				field_mode = Modes.getFieldMode(field.getFmode());
			}
			FieldProcessMode process_mode = null;
			if (!StringUtil.isEmpty(field.getFpmode())) {
				process_mode = Modes.getFieldProcessMode(field.getFpmode());
			}
			FieldEndProcessMode end_process_mode = null;
			if (!StringUtil.isEmpty(field.getFemode())) {
				end_process_mode = Modes.getFieldEndProcessMode(field.getFemode());
			}
			final FieldConfig field_config = new FieldConfig(name, 
															 FieldType.getFieldType(field.getField_type()),
															 field.getField_length(), 
															 field.getField_scale(), 
															 0, 
															 field_mode, 
															 process_mode, 
															 end_process_mode, 
															 field.getFexp(), 
															 is_required);
			field_config.setDefaultValue(field.getDefval());
			return field_config;
		default:
			throw new SystemException("SYS_LOADER_PACKAGE_INVALID_TYPE").addScene("type", type);
		}
	}
	
	private static <T extends FieldInfo> List<T> findFieldByParent(T[] infos, String parent) {
		List<T> lst_info = new ArrayList<T>();
		for (T info : infos) {
			if (parent.equals(info.getField_parent())) {
				lst_info.add(info);
			}
		}
		return lst_info;
	}
	
}
