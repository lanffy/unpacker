package resolver.conf;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description ���ظ�������ϵͳ����ͷ�б�ʾ��������ֶ�<br/>
 * <pre>�����ļ���tranDist.properties.ÿ�б�ʾһ������ϵͳ���á�
 * �����ʽΪ��<strong>����ϵͳ����=����ͷ�б�ʾ�������׵��ֶ�����</strong>
 * ����ֶδ���һ���ṹ�����ʽΪ��
 * <strong>����ϵͳ����=�ṹ����>�������׵��ֶ�����</strong>
 * ����ֶδ��ڶ��Ƕ�׵Ľṹ�У���ʽΪ��
 * <strong>����ϵͳ����=�ṹ����1>�ṹ����2>�������׵��ֶ�����</strong>
 * ���ĳ����ϵͳ�ı����У��޷�ͨ������ͷʶ�����룬��Ҫͨ��ʵ��<code>resolver.msg.impl.TranCodeImpl</code>
 * �ӿ���������ʶ������ķ�ʽ��Ȼ��ʵ���ఴ������ķ�ʽ���������������ļ��У�
 * <strong>����ϵͳ����=ʵ����ȫ��</strog>
 * </pre>
 * @author raoliang
 * @version 2015��2��27�� ����2:01:08
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
