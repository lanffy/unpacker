package unpacker.conf;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.wk.eai.config.PackageConfig;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.util.FileUtil;

/**
 * @description 加载报文配置
 * @author raoliang
 * @version 2015年2月4日 下午4:06:26
 */
public class Configs extends Loader{
	private static final HashMap<String, PackageConfig> serverConfigs = new HashMap<String, PackageConfig>();
	private static final HashMap<String, PackageConfig> tranConfigs= new HashMap<String, PackageConfig>();
	
	private static final String serverConfPath = config.getProperty("unpacker.serverConfPath", "/unpackerConf/server");
	private static final String tranConfPath = config.getProperty("unpacker.tranConfPath", "/unpackerConf/tranServer");
	
	public static void loadServer() {
		
	}
	
	public static void loadTranConf() {
		
	}
	
}
