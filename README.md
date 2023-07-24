# fdp-core 介绍
fdp-core是一套基础工具库，它独立发展功能不断完善。  
“基础工具库” 我只维护一套，供众多客户项目使用，众多客户项目都依赖fdp-core基础工具库，实现复用的目标。
### fdp-core包含有：
- 过滤器工具；
- 拦截器工具；
- 单元测试工具；
- 数据持久层工具：缓存、Wrapper、Page分页工具、树的基类、Entity的基类、Dao的基类；
- Controller\Serivce的基类；
- shiro权限管理的类；
- 防xss攻击工具；
- Beetl模板工具；
- Global全局配置管理工具；
- 一批Utils工具；
- R工具、SpringContext工具；
### 复用场景演示1 ：
A系统、B系统一直在使用fdp-core 3.3基础工具库。  
A系统在开发中由于业务需要，要对一些基础工具类、基类增加新功能。  
这些修改是有共性的、有益处的，也要保留同步给其它系统使用。所以要在fdp-core基础工具库这个项目中来修改，使用 fdp-core 3.3 升级到了 fdp-core 3.4。  
A系统 现在改为使用fdp-core 3.4，就可以使用到最新增加的功能了。  
后来B系统也要使用到fdp-core 3.4中新增加的功能，B系统修改Maven pom的依赖就可使用到fdp-core 3.4了。
### 复用场景演示2 ：
A系统、B系统一直在使用fdp-core 3.4基础工具库。  
A系统在开发中，发现了fdp-core 3.4中的某些Bug。  
这些修改是有共性的、有益处的，也要保留同步给其它系统使用。所以要在fdp-core基础工具库这个项目中来修改，使用 fdp-core 3.4 升级到了 fdp-core 3.4.1。  
A系统 现在改为使用fdp-core 3.4.1，就可以解决Bug了。  
B系统、C系统也要修复这个Bug，B系统\、C系统修改Maven pom的依赖就可使用到fdp-core 3.4.1了。  