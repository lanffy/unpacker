package resolver.conf;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.wk.SystemConfig;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.util.FileUtil;

/**
 * @description 加载公共方法
 * @author raoliang
 * @version 2015年2月4日 下午4:28:18
 */
public class Loader {
	
	public static final Log logger = LogFactory.getLog();
	
	public static final SystemConfig config = SystemConfig.getInstance();
	
	protected static final String basePath = "/resolverConf/";
	
	protected static List<File> getFileList(String filePath) {
		URL url = ModeLoader.class.getResource(filePath);
		if(url == null) {
			logger.warn("文件夹不存在：{}", filePath);
			return null;
		}
		List<File> fileList = FileUtil.listAllFiles(new File(url.getFile()));
		return fileList;
	}
}
