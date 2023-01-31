
## IOT 驱动运行原理

## 定义：



| 类                    | 定义                                                         | 内容 |
| --------------------- | ------------------------------------------------------------ | ---- |
| @Driver               | 作为驱动标识                                                 |      |
| DriverInfo            | 解析@Driver 注解内容放在里面                                 |      |
| @FieldConfig          | 作为驱动配置标识                                             |      |
| FieldConfigInfo       | 解析@FieldConfig 注解内容放里面                              |      |
| DriverConnection 接口 | 定义驱动的操作                                               |      |
|                       |                                                              |      |
| Connection接口        | 静态代理类， DriverConnection作为实现， 通过connection 大接口完成通用操作 |      |
| ConnectionImpl        | 连接实例, 用于保留上层的 连接层， 方便后续用来统一处理，若需要统一管理等，放入该层可处理 |      |
|                       |                                                              |      |
| Logger                | 自定义log接口                                                |      |
| LoggerImpl            | 自定义log接口的带有过滤的日志实现， 通过 setListener 即可实现将数据发送别处 ， 例如： 设置listener 为mq的发送， 通过fluentd 进行采集， 然后存储进入es中 |      |
|                       |                                                              |      |
| Config                | 解析yml 文件的小工具                                         |      |
| DriverManager         | **iot最核心类**，先扫描注解进行注册驱动，通过驱动标识进行实例化 |      |
|                       |                                                              |      |
|                       |                                                              |      |




