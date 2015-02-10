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
		ModeLoader.loadMode();
		ConfigLoader.loadConf();
		System.out.println("loader done");
	}

	public void test_headConfigLoad() {
		assertTrue(Configs.getHeadConfigMap().containsKey("inbankSRV"));
	}

	public void test_bodyConfigLoad() {
		assertTrue(Configs.getBodyConfigMap().containsKey("inbankSRV_8813"));
	}
}
