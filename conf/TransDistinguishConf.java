package resolver.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.wk.lang.SystemException;

/**
 * @description 加载各个服务系统报文头中表示交易码的字段<br/>
 * <pre>保存文件：tranDist.properties.每行表示一个服务系统配置。
 * 保存格式为：<strong>服务系统编码=报文头中表示关联交易的字段名称</strong>
 * 如果字段处于一个结构中则格式为：
 * <strong>服务系统编码=结构名称>关联交易的字段名称</strong>
 * 如果字段处于多接嵌套的结构中，格式为：
 * <strong>服务系统编码=结构名称1>结构名称2>关联交易的字段名称</strong>
 * 如果某服务系统的报文中，无法通过报文头识别交易码，则要通过实现<code>resolver.msg.impl.TranCodeImpl</code>
 * 接口来定义其识别交易码的方式。然后将实现类按照下面的方式配置在上述配置文件中：
 * <strong>服务系统编码=实现类全名</strog>
 * </pre>
 * @author raoliang
 * @version 2015年2月27日 下午2:01:08
 */
public class TransDistinguishConf extends Loader{
	private static HashMap<String, String> trans = new HashMap<String, String>();
	private static final String transConfFileName = "tranDist.properties";
	
	public static void main(String[] args) {
		load();
		System.out.println(getTranDistField("outsys"));
		System.out.println(getTranDistField("hxzh-nb"));
		System.out.println(getTranDistField("hxzh-tb"));
		System.out.println(getTranDistField("inbankSRV"));
		System.out.println(getTranDistField("unitepaySystem"));
	}
	
	public static void load() {
		File confFile = getFile(transConfFileName);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(confFile));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			while((line=in.readLine()) != null) {
				if(line.trim().length() == 0 || line.startsWith("#") || !line.contains("="))
					continue;
				putConf(line);
			}
			in.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Load Transaction Distinguish Config End ==========================");
	}
	
	public static String getTranDistField(String serviceName) {
		return trans.get(serviceName);
	}
	
	private static void putConf(String line) {
		String[] confs = line.split("=");
		String service = confs[0].trim();
		String tran_field = confs[1].trim();
		if(service.length() == 0 || tran_field.length() == 0) {
			throw new SystemException(
					"SYS_RESOLVER_TRANDIST_MAPPING_CONFIG_CONTENT_ERROR")
					.addScene("filePath", transConfFileName)
					.addScene("line", line);
		}
		trans.put(service, tran_field);
		logger.info("Load Transaction Distinguish Config：{} -> [{}]", service, tran_field);
	}
}
