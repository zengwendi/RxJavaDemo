## RxJava 是什么
  	a library for composing asynchronous and event-based programs using observable sequences for the Java VM

RxJava是Reactive Extensions的Java VM实现，用于通过使用observable序列来构建异步和基于事件的库。  
GitHub地址[https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)

## API 介绍和原理简析
### 1. 概念：扩展的观察者模式  
RxJava 的异步实现，是通过一种扩展的观察者模式来实现的。  
栗子：  
![](http://ww4.sinaimg.cn/mw1024/52eb2279jw1f2rx42h1wgj20fz03rglt.jpg)  
如图所示，通过 setOnClickListener() 方法，Button 持有 OnClickListener 的引用（这一过程没有在图上画出）；当用户点击时，Button 自动调用 OnClickListener 的 onClick() 方法。另外，如果把这张图中的概念抽象出来（Button -> 被观察者、OnClickListener -> 观察者、setOnClickListener() -> 订阅，onClick() -> 事件），就由专用的观察者模式（例如只用于监听控件点击）转变成了通用的观察者模式。如下图：  
![](http://ww3.sinaimg.cn/mw1024/52eb2279jw1f2rx4446ldj20ga03p74h.jpg)
### 2. 基于以上的概念， RxJava 的基本实现主要有三点：  
  1） 创建 Observer（观察者，它决定事件触发的时候将有怎样的行为  
  2） 创建 Observable （被观察者），它决定什么时候触发事件以及触发怎样的事件。   
  3） Subscribe (订阅)  再用 subscribe() 方法将Observer与Observable它们联结起来，  
observable.subscribe(observer);大概做了2件事  
  - onSubscribe开始订阅，Disposable参数可以用去取消订阅  
  - subscribeActual开始订阅， 在这里，事件发送的逻辑开始运行。从这也可以看出，在 RxJava 中， Observable 并不是在创建的时候就立即开始发送事件，而是在它被订阅的时候，即当 subscribe() 方法执行的时候。  
 
**示例图**  
![](http://upload-images.jianshu.io/upload_images/1008453-c6712bd320b8caf1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
### 3.线程控制 
在不指定线程的情况下， RxJava 遵循的是线程不变的原则，即：在哪个线程调用 subscribe()，就在哪个线程生产事件；在哪个线程生产事件，就在哪个线程消费事件。如果需要切换线程，就需要用到 Scheduler （调度器）。  
1) Scheduler 的 API (一)  
	- Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。  
	- Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。  
	- I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。  
	- Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。

### 4. 变换
1. RxJava 提供了对事件序列进行变换的支持，这是它的核心功能之一，也是大多数人说『RxJava 真是太好用了』的最大原因。**所谓变换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列**。
2. 变换的原理：lift()  
**针对事件序列的处理和再发送**。而在 RxJava 的内部，它们是基于同一个基础的变换方法： 
lift(ObservableOperator)  
当含有 lift() 时：  
	1. lift() 创建了一个 Observable 后，加上之前的原始 Observable，已经有两个 Observable 了；  
	2. 而同样地，新 Observable 里的新 Observer 加上之前的原始 Observable 中的原始 Observer，也就有了两个 Observer； 
	3. 当用户调用经过 lift() 后的 Observable 的 subscribe() 的时候，使用的是 lift() 所返回的新的 Observable ，于是它所触发的 subscribeActual（），也是用的新 Observable 中的新 subscribeActual（），即在 lift() 中生成的那个 Observer；        
       
这样就实现了 lift() 过程，有点**像一种代理机制，通过事件拦截和处理实现事件序列的变换。**  
![](http://i.imgur.com/uBIMPFA.png)   

## Rx拓展
ReactiveX 不且有Rxjava，还有RxJs、RxSwift、RxKotlin、RxPHP等，基本原理与Rxjava基本一致。  
[https://github.com/ReactiveX](https://github.com/ReactiveX)





