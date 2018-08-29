# Netty学习

鸣谢：《Netty权威指南》

## 一. 回顾JAVA IO知识点

##### 1. BIO 、NIO、 AIO 特点分析

* BIO

	同步阻塞IO

* NIO

	同步非阻塞IO

* AIO

	异步非阻塞IO

##### 2. 不同 I/O 模型对比
|      | 同步阻塞I/O(BIO) | 伪异步I/O | 非阻塞I/O(NIO)| 异步I/O(AIO)
|--------|--------|--------|--------|--------|
| 客户端个数：I/O线程|1：1 |M:N(其中M可以大于N)|M:1(1个I/O线程处理多个客户端连接)|M:0(不需要启动额外的I/O线程，被动回调)        |
|I/O类型（阻塞）|阻塞I/O|阻塞I/O|非阻塞I/O|非阻塞I/O|
|I/O类型（同步）|同步I/O|同步I/O|同步I/O（多路复用）|异步I/O|
|API使用难度   |简单   |简单   |非常复杂          |复杂|
|调试难度      |简单   |简单   |复杂             |复杂|
|可靠性        |非常差 |差     |高               |高|
|吞吐量        |低     |中    |高               |高|


## 二. 初始Netty


## 三. TCP 粘包/拆包

![TCP粘包/拆包问题](https://raw.github.com/eastFu/docs/master/netty-docs/3-1.png)

##### 1. 产生原因
* 应用程序write写入的字节大小大于套接口发送缓冲区大小
* 进行MSS大小的TCP字段
* 以太网帧的payload大于MTU进行IP分片

![TCP粘包/拆包问题原因](https://raw.github.com/eastFu/docs/master/netty-docs/3-2.png)

##### 2. 如何解决
* 消息定长，例如每个报文的大小为固定长度200字节，如果不够，空位补空格
* 在包尾增加回车换行符进行分割，例如FTP协议
* 将消息分为消息头和消息体，消息头中包含表示消息总长度（或者消息体长度）的字段，通常设计思想为消息头的第一个字段使用int32来表示消息的总长度
* 更复杂的应用层协议

##### 3. Netty解决TCP粘包/拆包

为了TCP粘包/拆包导致的半包读写的问题，Netty默认提供了多种编解码器用于处理半包。在练习代码中这里使用了LineBasedFrameDecoder+StringDecoder来解决半包读写问题。

原理：LineBasedFrameDecoder+StringDecoder组合是按行切换的文本解码器，通过"\n"或者"\r\n"来区分一个完整的数据包，从而解决TCP的粘包/拆包问题。

Netty还提供了多种编解码器来应对各种场景。


* netty 提供的解码器
　　 DelimiterBasedFrameDecoder 解决TCP的粘包解码器
　　 StringDecoder              消息转成String解码器
　　 LineBasedFrameDecoder      自动完成标识符分隔解码器
　　 FixedLengthFrameDecoder    固定长度解码器，二进制
　　 Base64Decoder base64       解码器

* netty 提供的编码器
　　 Base64Encoder  base64编码器
　　 StringEncoder  消息转成String编码器
　　 LineBasedFrameDecoder  自动完成标识符分隔编码器
　　 MessageToMessageEncoder 根据 消息对象 编码为消息对象

## 四. 分隔符和定长解码器的应用


##### DelimiterBasedFrameDecoder

按照分隔符实现的解码器

	@Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
    	ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new EchoServerHandler());
    }

##### FixedLengthFrameDecoder

按照固定长度实现的解码器

	@Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(10));
        socketChannel.pipeline().addLast(new StringDecoder());
        socketChannel.pipeline().addLast(new EchoServerHandler());
    }


## 五. 编解码技术


## 六. Google Protobuf解码器


## 七. JBoss Marshalling解码器

