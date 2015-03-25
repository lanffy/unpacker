###功能说明
- - - 

1. 旁路解析接收的报文需要符合文档规范:旁路报文接口规范1.0.doc;
2. 旁路解析时,根据报文中的**接收系统ip（dst\_ip）**和**接收系统端口（dst\_port）**来确定接收报文的服务系统;
3. 根据**采集端消息ID（msg\_id）**来识别交易.
> 例如
>>核心的ip和端口是:10.0.128.62:9000,关联交易有8813
>> 则报文中的dst\_ip和dst\_port分别是:10.0.128.62、9000,msg_id是:8813

###相关配置文件
- - -

####配置文件路径:config/resolverConf
1. config/resolverConf/Mode
>拆包模式,使用VrtUpdate导出工具导出到文件夹 DBData/7Expand/1Mode 中的文件;

2. config/resolverConf/Server
>服务系统接口配置,使用导出工具导出到文件夹 DBData/3Server 中的文件;

3. config/resolverConf/ServerTranService
>服务系统关联交易接口配置,使用导出工具导出到文件夹 DBData/4ServerTranService 中的文件;

4. config/resolverConf/server.properties
>接收报文服务系统识别配置文件
>>Ip地址和端口与对应的服务系统映射保存在server.properties 文件中,每行表示一条映射,格式为:ip+port=serverCode,
>>其中ip+port和serverCode分别为config/resolverConf/Server文件中的SERVER\_CODE和REMOTE\_ADDRESS,
>>如果REMOTE\_ADDRESS有多个值被逗号分割,则分多行配置.

5. config/resolverConf/tranDist.properties
>交易码识别配置文件
>>每行表示一个服务系统配置,保存格式为:服务系统编码=报文头中表示关联交易的字段名称;
如果字段处于一个结构中则格式为:
<strong>服务系统编码=结构名称>关联交易的字段名称</strong>;
如果字段处于多级嵌套的结构中,格式为:
<strong>服务系统编码=结构名称1>结构名称2>关联交易的字段名称</strong>;
如果某服务系统的报文中，无法通过报文头识别交易码，则要通过实现<code>resolver.msg.impl.TranCodeImpl</code>接口来定义其识别交易码的方式。
然后将实现类按照下面的方式配置在上述配置文件中:<strong>服务系统编码=实现类全名</strong>

6. config/resolverConf/channels.properties
>发送报文渠道识别配置文件
>>每行表示一个渠道映射配置.保存格式为：<code>src\_ip+dst\_ip+dst\_port=渠道名称</code>.<code>src\_ip</code>:发送报文系统ip;<ode>dst\_ip</code>:接收报文系统ip;<code>dst\_port</code>:接收报文系统端口

7. config/resolverConf/decrypt.properties
>请求解密配置文件
>>若渠道发给服务系统的报文经过加密，则 旁路解析器需要先将其解密才能处理。
>>配置规则：<strong>服务系统编码=实现<code>resolver.msg.impl.TranDecryptImpl</code>接口的解密处理类</strong>

8. config/system.properties
>旁路解析相关系统配置
>resolver.isSendMsg:是否发送旁路解析结果
>resolver.timeOut:交易超市时间,若旁路解析收到一笔请求报文，但在该时间后没有收到响应的响应报文，则判定该请求已经超时。单位毫秒
>resolver.timeOutDetectInterval:检测超时时间间隔，每隔多少时间检测是否有请求超时。单位毫秒


###异常信息处理
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
		<td>拆包成功</td>
	</td>
</table>