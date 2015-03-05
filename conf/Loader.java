package resolver.conf;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.wk.SystemConfig;
import com.wk.lang.SystemException;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.util.FileUtil;

/**
 * @description 加载公共方类
 * @author raoliang
 * @version 2015年2月4日 下午4:28:18
 */
public class Loader {
	
	public static final Log logger = LogFactory.getLog();
	
	public static final SystemConfig config = SystemConfig.getInstance();
	
	protected static final String basePath = "/resolverConf/";
	
	protected static List<File> getFileList(String folderPath) {
		URL url = ModeLoader.class.getResource(basePath + folderPath);
		if(url == null) {
			logger.warn("文件夹不存在：{}", folderPath);
			return null;
		}
		List<File> fileList = FileUtil.listAllFiles(new File(url.getFile()));
		return fileList;
	}
	
	protected static File getFile(String filePaht) {
		URL url = Servers.class.getResource(basePath + filePaht);
		if(url == null) {
			throw new SystemException("Config File Is Not Exist").addScene("FilePath", filePaht);
		}
		return new File(url.getFile());
	}
}
