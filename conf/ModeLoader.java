package unpacker.conf;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unpacker.util.JSONFileUtil;

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
	
	private static final String modeBasePath = basePath + "mode/";
	
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
		List<File> fileList = getFileList(fieldModePath);
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			FieldMode mode = (FieldMode)getMode(data, file.getAbsolutePath());
			Modes.putFieldMode(mode);
		}
		logger.info("������ģʽ:{}��", fileList.size());
	}
	
	private static void loadPackageMode() {
		List<File> fileList = getFileList(packageModePath);
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			PackageMode mode = (PackageMode)getMode(data, file.getAbsolutePath());
			Modes.putPackageMode(mode);
		}
		logger.info("���ذ�ģʽ:{}��", fileList.size());
	}
	
	private static void loadFieldProcessMode() {
		List<File> fileList = getFileList(fieldProcessModePath);
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			FieldProcessMode mode = (FieldProcessMode)getMode(data, file.getAbsolutePath());
			Modes.putFieldProcessMode(mode);
		}
		logger.info("��������ģʽ:{}��", fileList.size());
	}
	
	private static void loadFieldEndProcessMode() {
		List<File> fileList = getFileList(fieldEndProcessModePath);
		for(File file : fileList) {
			ServiceData data = JSONFileUtil.loadJsonFileToServiceData(file);
			FieldEndProcessMode mode = (FieldEndProcessMode)getMode(data, file.getAbsolutePath());
			Modes.putFieldEndProcessMode(mode);
		}
		logger.info("�������������ģʽ:{}��", fileList.size());
	}
	
	private static Object getMode(ServiceData data, String modeFilePath) {
		if(data == null || data.size() == 0) {
			logger.warn("����ģʽʱ�ļ��쳣,�ļ�����Ϊ��.�ļ���:{}", modeFilePath);
			return null;
		}
		String mode_code = data.getString("MODE_CODE");
		String mode_class = data.getString("MODE_CLASS");
		logger.info("load MODE_CODE - {}; MODE_CALSS - {}", mode_code, mode_class);
		Object mode = null;
		try {
			//ģʽʵ����
			final Class<?> cls = ClassUtil.classFromName(mode_class);
			ServiceData mode_param = data.getServiceData("MODE_PARAM");
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
	
}
