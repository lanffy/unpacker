package resolver.conf;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.wk.SystemConfig;
import com.wk.logging.Log;
import com.wk.logging.LogFactory;
import com.wk.util.FileUtil;

/**
 * @description ���ع�������
 * @author raoliang
 * @version 2015��2��4�� ����4:28:18
 */
public class Loader {
	
	public static final Log logger = LogFactory.getLog();
	
	public static final SystemConfig config = SystemConfig.getInstance();
	
	protected static final String basePath = "/resolverConf/";
	
	protected static List<File> getFileList(String filePath) {
		URL url = ModeLoader.class.getResource(filePath);
		if(url == null) {
			logger.warn("�ļ��в����ڣ�{}", filePath);
			return null;
		}
		List<File> fileList = FileUtil.listAllFiles(new File(url.getFile()));
		return fileList;
	}
}
