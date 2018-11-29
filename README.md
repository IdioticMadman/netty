# 目录划分

### Chapter02
用BIO，NIO，AIO分别实现了TimerServer以及TimeClient

### chapter03
简单用netty实现了TimerServer和TimeClient     

---
以上两章节，都是未考虑TCP层的拆包和粘包的问题
TCP是面向流协议，所以为了对消息区分进行以下四种方式

1. 消息固定长度，累计读到len长度的报文后，就认为读取到了一个完整的消息，将计数器置位，重新读取下一个数据报
2. 将回车换行符作为消息结束符，列如FTP协议，这种在文本协议应用比较广泛。
3. 将特殊的分隔符作为消息的结束标志，回车换行符就是一种特殊的结束分隔符
4. 通过在消息头中定义长度字段来表示消息的总长度

### chapter04
介绍了LineBasedFrameDecoder解码器的用法，它是以换行符当做包读取完毕。

### chapter05
* DelimiterBasedFrameDecoder解码器，它是以指定的字节当做包结束。
* FixedLengthFrameDecoder解码器，定长解码。

### chapter06
jdk提供的对象序列化成byte数组，又大，需要的时间又长。所以基本都不用这套序列化以及反序列化
主流的编解码框架有:

* Google的Protobuf
* Facebook的thrift
* JBoss的Marshalling

### chapter07
使用MessagePack编解码demo
