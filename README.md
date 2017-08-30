## Android Sqlite Framework ##

### 一个简单的面向对象的轻量级数据库框架，简化了Android开发中操作数据库的繁琐过程，将面向过程的业务逻辑封装，完全的零Sql语句。 ###

**使用方式**

- 创建数据库存储的JavaBean，给类加上 **@DbTable**注解表名,给属性加上 **@DbFiled**注解字段名
		
		@DbTable("tb_user")
		public class User {
		
		@DbFiled("_id")
		private Integer id;
		
		@DbFiled("name")
		private String userName;
		
		@DbFiled("password")
		private String password;

		}

- 直接调用IBaseDao<User> userHelper = BaseDaoFactory.getInstance("user.db").getDataHelper(BaseDao.class, User.class)，传入数据库名，BaseDao对象与JavaBean对象。然后直接使用userHelper做增删改查操作即可。

**使用注意事项：**

- 主键自动创建，且字段名为_id，如需要用到主键，**请务必使用Integer类型，使用@DbFiled注解，value用\_id**



- 如需扩展可继承**BaseDao**


**其它**

该数据库框架一共只有5个类，核心代码都在BaseDao中，有需要可以down下来。
