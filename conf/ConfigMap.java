package unpacker.conf;

import java.util.HashMap;

import com.wk.eai.config.PackageConfig;

/**
 * @description
 * @author raoliang
 * @version 2015年2月4日 下午4:09:18
 */
public class ConfigMap extends HashMap<String, PackageConfig>{

	private static final long serialVersionUID = 7855091479245400958L;
	
	@Override
	public PackageConfig put(String key, PackageConfig value) {
		return super.put(key, value);
	}
}
