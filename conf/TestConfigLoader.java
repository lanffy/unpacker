package resolver.conf;

import com.wk.test.TestCase;

/**
 * @description
 * @author raoliang
 * @version 2015年2月9日 下午3:12:33
 */
public class TestConfigLoader extends TestCase {

	@Override
	protected void setUpOnce() throws java.lang.Exception {
		ModeLoader.load();
		TranConfigLoader.load();
		System.out.println("loader done");
	}

	public void test_headConfigLoad() {
		assertTrue(TransConfigs.getHeadConfigMap().containsKey("inbankSRV"));
	}

	public void test_bodyConfigLoad() {
		assertTrue(TransConfigs.getBodyConfigMap().containsKey("inbankSRV_8813"));
	}
}
