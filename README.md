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

* JAVA序列化的目的主要有两个：
	1.网络传输
	2.对象持久化

* 什么是Java对象的编解码技术？

	对于网络传输而言，当进行远程跨进程服务调用时，需要把被传输的java对象编码为字节数组或者ByteBuffer对象。而当远程服务读取到ByteBuffer对象或者字节数组时，需要将其解码为发送时的java对象。这就是java对象的编解码技术。

* java序列化

    用来实现序列化的类都在java.io包中，我们常用的类或接口有：

	ObjectOutputStream:提供序列化对象并把其写入流的方法

	ObjectInputStream：读取流并反序列化对象

	Serializable：一个对象想要被序列化，那么它的类就要实现 此接口，这个对象的所有属性（包括private属性、包括其引用的对象）都可以被序列化和反序列化来保存、传递。

	Externalizable：他是Serializable接口的子类，有时我们不希望序列化那么多，可以使用这个接口，这个接口的writeExternal()和readExternal()方法可以指定序列化哪些属性;

	但是如果你只想隐藏一个属性，比如用户对象user的密码pwd，如果使用Externalizable，并除了pwd之外的每个属性都写在writeExternal()方法里，这样显得麻烦，可以使用Serializable接口，并在要隐藏的属性pwd前面加上transient就可以实现了

* java序列化的缺点

	1.无法跨语言：java的序列化技术是java语言内部的私有协议，其他语言并不支持，对于用户来说它完全是黑盒。对于java序列化后的字节数组，别的语言无法进行反序列化，这就严重阻碍它的使用。在远程服务调用（RPC）时，目前流行的RPC通讯框架，都没有使用或直接使用JAVA序列化作为编解码框架，因为它无法跨语言。
	2.序列化后的码流太大
	3.序列化性能太低

## 六. MessagePack 编解码
	<!-- https://mvnrepository.com/artifact/org.msgpack/msgpack -->
    <dependency>
        <groupId>org.msgpack</groupId>
        <artifactId>msgpack</artifactId>
        <version>0.6.12</version>
    </dependency>


## 七. Google Protobuf解码器



## 八. JBoss Marshalling解码器

