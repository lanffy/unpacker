package resolver.conf;
/**
 * @description
 * @author raoliang
 * @version 2015年3月4日 下午3:42:40
 */
public class TranCodeInfo {
	private String fieldName;
	private String fieldType;
	private int offset;
	private int len;
	
	public TranCodeInfo(String fieldName,String fieldType, int offset, int len) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.offset = offset;
		this.len = len;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	
}
