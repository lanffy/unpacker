###����˵��
- - - 

1. ��·�������յı�����Ҫ�����ĵ��淶:��·���Ľӿڹ淶1.0.doc;
2. ��·����ʱ,���ݱ����е�**����ϵͳip��dst\_ip��**��**����ϵͳ�˿ڣ�dst\_port��**��ȷ�����ձ��ĵķ���ϵͳ;
3. ����**�ɼ�����ϢID��msg\_id��**��ʶ����.
> ����
>>���ĵ�ip�Ͷ˿���:10.0.128.62:9000,����������8813
>> �����е�dst\_ip��dst\_port�ֱ���:10.0.128.62��9000,msg_id��:8813

###��������ļ�
- - -

####�����ļ�·��:config/resolverConf
1. config/resolverConf/Mode
>���ģʽ,ʹ��VrtUpdate�������ߵ������ļ��� DBData/7Expand/1Mode �е��ļ�;

2. config/resolverConf/Server
>����ϵͳ�ӿ�����,ʹ�õ������ߵ������ļ��� DBData/3Server �е��ļ�;

3. config/resolverConf/ServerTranService
>����ϵͳ�������׽ӿ�����,ʹ�õ������ߵ������ļ��� DBData/4ServerTranService �е��ļ�;

4. config/resolverConf/server.properties
>���ձ��ķ���ϵͳʶ�������ļ�
>>Ip��ַ�Ͷ˿����Ӧ�ķ���ϵͳӳ�䱣����server.properties �ļ���,ÿ�б�ʾһ��ӳ��,��ʽΪ:ip+port=serverCode,
>>����ip+port��serverCode�ֱ�Ϊconfig/resolverConf/Server�ļ��е�SERVER\_CODE��REMOTE\_ADDRESS,
>>���REMOTE\_ADDRESS�ж��ֵ�����ŷָ�,��ֶ�������.

5. config/resolverConf/tranDist.properties
>������ʶ�������ļ�
>>ÿ�б�ʾһ������ϵͳ����,�����ʽΪ:����ϵͳ����=����ͷ�б�ʾ�������׵��ֶ�����;
����ֶδ���һ���ṹ�����ʽΪ:
<strong>����ϵͳ����=�ṹ����>�������׵��ֶ�����</strong>;
����ֶδ��ڶ༶Ƕ�׵Ľṹ��,��ʽΪ:
<strong>����ϵͳ����=�ṹ����1>�ṹ����2>�������׵��ֶ�����</strong>;
���ĳ����ϵͳ�ı����У��޷�ͨ������ͷʶ�����룬��Ҫͨ��ʵ��<code>resolver.msg.impl.TranCodeImpl</code>�ӿ���������ʶ������ķ�ʽ��
Ȼ��ʵ���ఴ������ķ�ʽ���������������ļ���:<strong>����ϵͳ����=ʵ����ȫ��</strong>

6. config/resolverConf/channels.properties
>���ͱ�������ʶ�������ļ�
>>ÿ�б�ʾһ������ӳ������.�����ʽΪ��<code>src\_ip+dst\_ip+dst\_port=��������</code>.<code>src\_ip</code>:���ͱ���ϵͳip;<ode>dst\_ip</code>:���ձ���ϵͳip;<code>dst\_port</code>:���ձ���ϵͳ�˿�

7. config/resolverConf/decrypt.properties
>������������ļ�
>>��������������ϵͳ�ı��ľ������ܣ��� ��·��������Ҫ�Ƚ�����ܲ��ܴ���
>>���ù���<strong>����ϵͳ����=ʵ��<code>resolver.msg.impl.TranDecryptImpl</code>�ӿڵĽ��ܴ�����</strong>

8. config/system.properties
>��·�������ϵͳ����
>resolver.isSendMsg:�Ƿ�����·�������
>resolver.timeOut:���׳���ʱ��,����·�����յ�һ�������ģ����ڸ�ʱ���û���յ���Ӧ����Ӧ���ģ����ж��������Ѿ���ʱ����λ����
>resolver.timeOutDetectInterval:��ⳬʱʱ������ÿ������ʱ�����Ƿ�������ʱ����λ����


###�쳣��Ϣ����
- - - 
<table border="1">
	<tr>
		<td>NO.</td>
		<td>ret_code</td>
		<td>ret_msg</td>
	</tr>
	<tr>
		<td>1</td>
		<td>0</td>
		<td>����ɹ�</td>
	</td>
</table>