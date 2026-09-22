# fct-util

FCT 的基础工具模块，提供框架运行所需的底层能力，无 GUI 依赖。

文案是我让AI写的，懒得洗文案。

## 工具类

- `Util`：反射实例化（`newInstance`）、UUID 哈希（`turn`）、条件匹配（`match` / `cond`）、空消费者
- `Math`：字节转字符（`toBytesChar`）、整除判断（`match`）、正交投影矩阵（`ortho`）

## 资源加载

- `Resources`：多级回退加载（clazz → URL → ClassLoader → Module），保证资源在各种环境下都能被找到

## 日志

- `LoggerFactory`：基于 Log4j 2 的封装，支持运行时切换日志级别和配置文件路径

## 锁

- `Lock`：以 `nanoTime + currentTimeMillis` 唯一标识的锁对象，配合 `synchronized` 使用

## 标记与注解

- `ApiSign`：API 标记注解（`SignInterface` / `Dangerous` / `NotRecommended` / `InternalApi`）
- `Copyable`：浅拷贝接口
- `Initializable`：初始化生命周期接口（`initialize` / `dispose` / `isInit`）
- `Readonly` / `Manager` / `Constant` / `Uninitialized`：语义标记接口

## 函数式接口与异常

- `ListTask` / `Return` / `HasTraversalTasks`
- `CopyFailedException`
