package unpacker.util;

import com.wk.sdo.ServiceData;
import com.wk.test.TestCase;

/**
 * @description
 * @author raoliang
 * @version 2015��2��3�� ����3:55:14
 */
public class TestJSONFileUtil extends TestCase {
	public void test_load_json() {
		ServiceData data = JSONFileUtil.loadJsonFileToServiceData("/unpackerConf/mode/packageMode/outsys_mode");
		System.out.println(data);
	}
}
