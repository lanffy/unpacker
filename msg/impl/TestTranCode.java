package resolver.msg.impl;

import resolver.conf.ModeLoader;
import resolver.util.BufferReader;

import com.wk.nio.ChannelBuffer;

/**
 * @description
 * @author raoliang
 * @version 2015年3月4日 下午4:28:11
 */
public class TestTranCode {
	public static void main(String[] args) {
		ModeLoader.loadMode();
		String classNameStr = "resolver.msg.impl.InbankSrvTranCode";
		try {
			TranCodeImpl c = (TranCodeImpl) Class.forName(classNameStr).newInstance();
			ChannelBuffer buffer = BufferReader.createRequestMsg("8813req");
			System.out.println(c.getTranCode(buffer));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
