第一章 项目概述 

基于Bmob后端云爆肝三年的校园app，支持手机号注册登录、发帖、评论、分享、点赞、以及Bmob提供的数据监听服务实现的聊天、即时通讯、消息通知、接入支付宝sdk实现支付、openAI、内置充值、消费、提现、关注、粉丝、新生迎新、校园外卖、店铺入驻、店铺管理等功能

如今，随着互联网和智能手机的普及，移动应用成为人们日常生活的必需品。尤其是在校园生活中，学生们需要处理各种琐碎的事务，例如寻物启示、失物招领、出售二手闲置、健康打卡、校园 VPN、新生迎新等等。而传统的寻物启示和失物招领往往需要在校内贴海报，效率较低；出售二手闲置也需要在校内张贴信息，受众范围有限。此外，随着新冠疫情的持续影响，学校也需要加强健康打卡和校园 VPN 服务，以保障学生的健康和网络安全。 

因此，开发一款校园助手 app，集成上述多种功能，可以极大地方便学生的日常生活。综上所述，旅助具有很高的实用价值和广阔的市场前景，有望成为校园生活中必不可少的工具之一。 

第二章 概要设计 

(一) 启动图 

在应⽤程序启动时显⽰，通常是品牌标识或应⽤程序的 logo。
![IMG_2384(20220509-073338)](https://github.com/ZHENGSHAOMIN/LVZHU/assets/112945467/af69d360-248b-4810-ac6a-8ccc11cc8ff9)
（感谢刘琳设计的图标）

(二) 主页 

包含应⽤程序的主要导航元素，如底部导航栏。
![_-1675094227__ed4dffd654be57acd0b68c836d9d7089_1281087827_Screenshot_2023-08-26-00-59-07-917_Min app plus_0_wifi_0](https://github.com/ZHENGSHAOMIN/LVZHU/assets/112945467/ae65d6b0-ec64-40e1-974e-d378c5c812c0)


(三) 首页 

⽤于展⽰⽤户发布的动态信息。 包含评论通知，当有新评论时会提⽰⽤户。 ⽤户可以发布动态，浏览其他⽤户发布的动态，并进⾏点赞、评论等互动。 

(四) 订单页

⽤于发布和展⽰订单信息。 ⽤户可以发布订单，查看⾃⼰发布的订单和接受订单的情况。其他⽤户可以浏览所有的订单，并且接受并完成订单。 
(五) 消息页 ![F055459F4675785C602D3CF814AEA6AB](https://github.com/ZHENGSHAOMIN/LVZHU/assets/112945467/2c32050f-18ef-401a-90d5-e777a7d5beb1)

⽤户可以查看系统通知、私信等消息，包括好友请求等信息。 
在新消息到来时会有提⽰。

![Uploading F055459F4675785C602D3CF814AEA6AB.jpg…]()


(六) 个人页 

⽤于展⽰⽤户个⼈信息、我的派单、我的接单、我的评论、我的账户等相关信息。 
⽤户可以编辑个⼈资料、添加好友等操作。

![1E8B10A5C183F2BA611812571AB07D27](https://github.com/ZHENGSHAOMIN/LVZHU/assets/112945467/d82c96de-af36-492e-a60a-3634145d778c)


1. 健康打卡
   
⽤户可以每⽇进⾏健康打卡，记录⾃⼰的健康状况。 
管理员可以查看⽤户打卡情况，对于不符合要求的⽤户进⾏处理。

3. 校园VPN
   
⽤户可以使⽤该功能连接校园 VPN ⽹络，保证使⽤安全。 
⽤户可以在该应⽤中直接使⽤，⽆需额外的 VPN 客户端。 
旅助⼤体设计流程出如下图所⽰：

  第三章 详细设计

旅助的布局主要以线性布局（LinearLayout）和相对布局（RelativeLayout）为主，其他主要控件分别是是输⼊框（EditText）、⽂本框（TextView）、按钮（Button）、图像视图
（ImageView）以及列表视图（ListView）等。
 
(一) 启动图布局

旅助的启动图，我的设计是在布局中间位置添加旅助图标，并且加以下坠的动画，使界
⾯看起来更加简约⼤⽅。启动图不仅是给⽤户留下⼀个使⽤印象，更多的是处理软件预加载的主要线程事件，避免在主页⾯加载数据过多，导致运⾏卡顿，极⼤的增强了⽤户体验。如下图所⽰。 
  
(二) 主页布局 

旅助的主布局我使⽤的是 FrameLayout+BottomNavigation 这样能够实现底部菜单栏事件，以及更好的加载每⼀个⼦视图。如下图所⽰。 
  
(三) 首页布局 

⾸页通常是展⽰ app 的主要数据，如⽤户发布的实时动态（例如学习⼼得分享、寻物启
⽰、情绪发泄等），因为该类可能会加载⼤量的图⽚使内存泄漏以及其他异常导致应⽤闪退，因此该页⾯没有使⽤ ListView，⽽是采⽤效率更加⾼效的 RecyclerView，使数据加载更加流畅，⽤户体验效果更佳。 
  
(四) 订单（任务）页布局 

订单页是展⽰⽤户发布的⽇常求助，当其他⽤户完成任务时即可获得相应“菠萝”数量，
其中“菠萝”可以兑换⼀些⽇常⽤品，如笔、笔记本、U 盘、⽿机等，以此⿎励更多⽤户参与到互帮互助。 
 
(五) 消息页布局 

消息页主要是展⽰⽤户接收到的聊天消息，⽬前⽀持⽂字信息，后续可更新图⽚，语⾳
等多类型信息。 
  
(六) 个人信息页布局 

个⼈信息页⾯主要是展⽰个⼈资料以及常⽤功能，⽅便⽤户查看管理⾃⼰发布的内容，可进⾏⼆次操作（如修改、删除等）。 
  
第四章 项目总结 

旅助，最开始我的想法是做⼀个 app 能够为同学们提供更多的便利服务，前提是现有的服务较为繁琐或者不能完全满⾜同学们的需求。旅助从 1.0.0 版本到现在的 2.0.0 版本经历了整整 18 个⽉，其中迭代版本有 7 个版本公开发布，2 个测试版本未发布，其中每⼀个版本的更新都需要去改变去创新，在这期间，我和我的团队付出了很多的时间与精⼒，我们也不知到未来会怎么样，只知道⼀直往下做，总会带来满意的结果。 

我们的期望是完善⾃⾝的功能，为同学们提供更多更优质的服务，其次是为更多的同学提供服务。（包括但不限于提供特⾊化、个性化服务） 

第五章 致谢

在这⾥感谢所有⽀持过旅助的朋友、同学、⽼师以及屏幕前读到此处的你！

最最最感谢的是坚持三年的自己！从2015年自学JAVA至今2023已有8年，未曾有过大的成就，一直忙于琐事，也曾写过一些大大小小项目，到最后留给自己的只有回忆。

最后把我最喜欢的一句话送给你：一万年太久，只争朝夕！ 


