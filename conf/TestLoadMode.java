package resolver.conf;

import com.wk.conv.mode.DefaultPackageMode;
import com.wk.conv.mode.Modes;
import com.wk.test.TestCase;

/**
 * @description
 * @author raoliang
 * @version 2015年2月3日 下午5:08:01
 */
public class TestLoadMode extends TestCase{
	public void test_load_mode() {
		ModeLoader.loadMode();
		DefaultPackageMode mode = (DefaultPackageMode) Modes.getPackageMode("outsys_mode");
		assertEquals("outsys_mode", mode.getName());
	}
}
