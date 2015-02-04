package unpacker.conf;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unpacker.util.JSONFileUtil;

import com.wk.SystemConfig;
import com.wk.conv.mode.DefaultPackageMode;
import com.wk.conv.mode.FieldEndProcessMode;
import com.wk.conv.mode.FieldMode;
import com.wk.conv.mode.FieldProcessMode;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;
import com.wk.util.BeanUtil;
import com.wk.util.ClassUtil;
import com.wk.util.FileUtil;

/**
 * @description 加载模式，包括域模式、包模式、域处理模式、域结束处理模式
 * @author raoliang
 * @version 2015年2月3日 下午1:51:47
 */
public class LoadMode {
	private static final Log logger = LogFactory.getLog("unpacker");
	
	private static final String modeBasePath = "/unpackerConf/mode/";
	
	private static final SystemConfig config = SystemConfig.getInstance();
	
	private static final String fieldModePath = config.getProperty("unpacker.fieldModePath", modeBasePath + "fieldMode");
	private static final String packageModePath = config.getProperty("unpacker.packageModePath", modeBasePath + "packageMode");
	private static final String fieldProcessModePath = config.getProperty("unpacker.fieldProcessModePath", modeBasePath + "fieldProcessMode");
	private static final String fieldEndProcessModePath = config.getProperty("unpacker.fieldEndProcessModePath", modeBasePath + "fieldEndProcessMode");
	
	public static void main(String[] args) {
		loadMode();
		DefaultPackageMode mode = (DefaultPackageMode) Modes.getPackageMode("outsys_mode");
		System.out.println(mode.getName());
	}
	
	public static void loadMode() {
		loadFieldMode();
		loadPackageMode();
		loadFieldProcessMode();
		loadFieldEndProcessMode();
	}
	
	private static void loadFieldMode() {
		URL url = getFileUrl(fieldModePath);
		if(url == null)
			return;
		List<File> fileList = FileUtil.listAllFiles(new File(url.getFile()));
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			FieldMode mode = (FieldMode)getMode(data, file.getAbsolutePath());
			Modes.putFieldMode(mode);
		}
		logger.info("加载域模式:{}个", fileList.size());
	}
	
	private static void loadPackageMode() {
		URL url = getFileUrl(packageModePath);
		if(url == null)
			return;
		List<File> fileList = FileUtil.listAllFiles(new File(url.getFile()));
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			PackageMode mode = (PackageMode)getMode(data, file.getAbsolutePath());
			Modes.putPackageMode(mode);
		}
		logger.info("加载包模式:{}个", fileList.size());
	}
	
	private static void loadFieldProcessMode() {
		URL url = getFileUrl(fieldProcessModePath);
		if(url == null)
			return;
		List<File> fileList = FileUtil.listAllFiles(new File(url.getFile()));
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			FieldProcessMode mode = (FieldProcessMode)getMode(data, file.getAbsolutePath());
			Modes.putFieldProcessMode(mode);
		}
		logger.info("加载域处理模式:{}个", fileList.size());
	}
	
	private static void loadFieldEndProcessMode() {
		URL url = getFileUrl(fieldEndProcessModePath);
		if(url == null)
			return;
		List<File> fileList = FileUtil.listAllFiles(new File(url.getFile()));
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			FieldEndProcessMode mode = (FieldEndProcessMode)getMode(data, file.getAbsolutePath());
			Modes.putFieldEndProcessMode(mode);
		}
		logger.info("加载域结束处理模式:{}个", fileList.size());
	}
	
	private static Object getMode(ServiceData data, String modeFilePath) {
		if(data == null || data.size() == 0) {
			logger.warn("加载模式时文件异常,文件内容为空.文件名:{}", modeFilePath);
			return null;
		}
		String mode_code = data.getString("MODE_CODE");
		String mode_class = data.getString("MODE_CLASS");
		logger.info("load MODE_CODE - {}; MODE_CALSS - {}", mode_code, mode_class);
		Object mode = null;
		try {
			//模式实现类
			final Class<?> cls = ClassUtil.classFromName(mode_class);
			ServiceData mode_param = data.getServiceData("MODE_PARAM");
			String[] param_keys = mode_param.getKeys();
			//如果是默认包模式
			if (DefaultPackageMode.class == cls) {
				final Map<FieldType, FieldMode> map = new HashMap<FieldType, FieldMode>();
				for(String key : param_keys) {
					ServiceData param_data = mode_param.getServiceData(key);
					final String mode_type = param_data.getString("PARAM_CODE");
					final FieldType ftype;
					if ("byte[]".equals(mode_type)) {
						ftype = FieldType.FIELD_IMAGE;
					} else {
						ftype = FieldType.getFieldType(mode_type);
					}
					logger.info("load MODE_PARAM:{}, FIELD_MODE: {} = {}",mode_code, ftype, param_data.getString("PARAM_VALUE"));
					map.put(ftype, Modes.getFieldMode(param_data.getString("PARAM_VALUE")));
				}
				mode = ClassUtil.newInstance(cls, cls.getConstructor(String.class, Map.class), 
											 false, mode_code, map);
			} else {
				mode = ClassUtil.newInstance(cls, cls.getConstructor(String.class), false, mode_code);
				for(String key : param_keys) {
					ServiceData param_data = mode_param.getServiceData(key);
					logger.debug("PARAM : {} = {}", param_data.getString("PARAM_CODE"), param_data.getString("PARAM_VALUE"));
					BeanUtil.setProperty(mode, param_data.getString("PARAM_CODE"),param_data.getString("PARAM_VALUE"));
				}
			}
		} catch(Throwable t) {
			t.printStackTrace();
			throw new SystemException("SYS_UNPAKER_LOAD_MODE_CLASS_NOT_FOUND_OR_HAS_NO_STRING_CONSTRUCTOR")
							.addScene("ModeName", mode_code)
							.addScene("ModeClass", mode_class);
		}
		
		return mode;
	}
	
	private static URL getFileUrl(String filePath) {
		URL url = LoadMode.class.getResource(filePath);
		if(url == null) {
			logger.warn("文件夹不存在：{}", filePath);
			return null;
		}
		return url;
	}
}
