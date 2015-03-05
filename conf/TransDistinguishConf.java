package resolver.conf;

import java.util.concurrent.ConcurrentHashMap;

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
	private static final ConcurrentHashMap<String, String> trans = new ConcurrentHashMap<String, String>();
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
		_load(transConfFileName, trans, "Transaction Distinguish");
	}
	
	public static String getTranDistField(String serviceName) {
		return trans.get(serviceName);
	}
	
}
