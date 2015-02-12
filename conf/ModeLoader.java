package resolver.conf;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import resolver.util.JSONFileUtil;

import com.wk.conv.mode.DefaultPackageMode;
import com.wk.conv.mode.FieldEndProcessMode;
import com.wk.conv.mode.FieldMode;
import com.wk.conv.mode.FieldProcessMode;
import com.wk.conv.mode.Modes;
import com.wk.conv.mode.PackageMode;
import com.wk.lang.SystemException;
import com.wk.sdo.FieldType;
import com.wk.sdo.ServiceData;
import com.wk.util.BeanUtil;
import com.wk.util.ClassUtil;

/**
 * @description ����ģʽ��������ģʽ����ģʽ������ģʽ�����������ģʽ
 * @author raoliang
 * @version 2015��2��3�� ����1:51:47
 */
public class ModeLoader extends Loader{
	
	private static final String modeBasePath = basePath + "Mode/";
	
	private static final String modeFilePath = config.getProperty("resolver.modeFilePath", modeBasePath);
	
	public static void main(String[] args) {
		loadMode();
		DefaultPackageMode mode = (DefaultPackageMode) Modes.getPackageMode("outsys_mode");
		System.out.println(mode.getName());
	}
	
	public static void loadMode() {
		List<File> fileList = getFileList(modeFilePath);
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			loadMode(data, file.getAbsolutePath());
		}
		logger.info("Load Mode End");
	}
	
	private static void loadMode(ServiceData data, String modeFilePath) {
		if(data == null || data.size() == 0) {
			logger.warn("����ģʽʱ�ļ��쳣,�ļ�����Ϊ��.�ļ���:{}", modeFilePath);
			return;
		}
		//�����ϵͳ����ģʽ,�򲻼���
		if("1".equals(data.getString("IS_SYS_MODE"))){
			return;
		}
		String mode_code = data.getString("MODE_CODE");
		String mode_name = data.getString("MODE_NAME");
		String mode_class = data.getString("MODE_CLASS");
		Object mode = null;
		try {
			//ģʽʵ����
			final Class<?> cls = ClassUtil.classFromName(mode_class);
			ServiceData mode_param = data.getServiceData("MODE_PARAM");
			//����ģʽ����
			if(mode_param != null) {
				String[] param_keys = mode_param.getKeys();
				//�����Ĭ�ϰ�ģʽ
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
						logger.info("load MODE_PARAM:{} = {}", param_data.getString("PARAM_CODE"), param_data.getString("PARAM_VALUE"));
						BeanUtil.setProperty(mode, param_data.getString("PARAM_CODE"),param_data.getString("PARAM_VALUE"));
					}
				}
			} else {
				mode = ClassUtil.newInstance(cls, cls.getConstructor(String.class), false, mode_code);
			}
		} catch(Throwable t) {
			t.printStackTrace();
			throw new SystemException("SYS_RESOLVER_LOAD_MODE_CLASS_NOT_FOUND_OR_HAS_NO_STRING_CONSTRUCTOR")
							.addScene("ModeName", mode_code)
							.addScene("ModeClass", mode_class);
		}
		String type = data.getString("MODE_TYPE");
		if(type == null)
			throw new SystemException("SYS_RESOLVER_LOAD_MODE_NOT_HAVE_MODE_TYPE_PARAMETER");
		if (type.equals("0")) {
			Modes.putFieldMode((FieldMode)mode);
			logger.info("Load Field Mode:MODE_CODE -> [{}], MODE_NAME -> [{}], MODE_CLASS -> [{}]", mode_code, mode_name, mode_class);
		} else if (type.equals("1")) {
			Modes.putPackageMode((PackageMode)mode);
			logger.info("Load Package Mode:MODE_CODE -> [{}], MODE_NAME -> [{}], MODE_CLASS -> [{}]", mode_code, mode_name, mode_class);
		} else if (type.equals("2")) {
			Modes.putFieldProcessMode((FieldProcessMode)mode);
			logger.info("Load Field Process Mode:MODE_CODE -> [{}], MODE_NAME -> [{}], MODE_CLASS -> [{}]", mode_code, mode_name, mode_class);
		} else if (type.equals("3")) {
			Modes.putFieldEndProcessMode((FieldEndProcessMode)mode);
			logger.info("Load Field End Process Mode:MODE_CODE -> [{}], MODE_NAME -> [{}], MODE_CLASS -> [{}]", mode_code, mode_name, mode_class);
		} else {
			logger.warn("Not Exist Mode Type:MODE_CODE -> [{}], MODE_NAME -> [{}]", mode_code, mode_name);
			return;
		}
	}
	
}
