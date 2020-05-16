# jk-excel
基于poi，做excel解析，自动识别excel或csv、tsv文件格式，单sheet最大生成百万数据在50秒以内，内存15m左右。

[markdown基本语法](https://yinxiang.com/new/hc/articles/%e5%8d%b0%e8%b1%a1%e7%ac%94%e8%ae%b0-markdown-%e5%85%a5%e9%97%a8%e6%8c%87%e5%8d%97/?utm_source=b1&utm_medium=b1&utm_term=bxdv1)

# 详细例子
[demo code](https://github.com/lgh1117/jk-excel/blob/master/demo/src/main/java/jk/demo)

# 个人csdn
[个人CSDN](https://blog.csdn.net/lgh1117)

## 特性
   支持csv、tsv、excel所有版本

## 效果图例
### 生成excel样式
. 表头约束

![约束限制](https://github.com/lgh1117/jk-excel/blob/master/static/constract.png)

. 表头有特定格式输出

![format](https://github.com/lgh1117/jk-excel/blob/master/static/format.png)

. 复杂表头

![mult](https://github.com/lgh1117/jk-excel/blob/master/static/mutl-header.png)

. 自定义文档说明

![nav](https://github.com/lgh1117/jk-excel/blob/master/static/nav.png)

. 根据模板生成新的excel

![tpl1](https://github.com/lgh1117/jk-excel/blob/master/static/tpl1.png)

![tpl2](https://github.com/lgh1117/jk-excel/blob/master/static/tpl2.png)

### 使用步骤
#### 一、解析Excel 详细查看[parse excel](https://github.com/lgh1117/jk-excel/blob/master/demo/src/main/java/jk/demo/excel/parse)
1. 获取文件，后缀为xls、xlsx、csv、tsv
    文件类型判断，如果不是excel格式文件，使用别的txt文件等重命名过来的，程序会检测为非法文件；
```java
File file = new File("test.xls");
```

2. 设置列头映射代码
````java
//配置映射
    public  List<Mapping> getMappings(){
        List<Mapping> mappings = new ArrayList<>();
        Mapping m = new Mapping("name", "姓名");
        mappings.add(m);
        m = new Mapping("age", "年龄");
        mappings.add(m);
        m = new Mapping("height", "身高");
        mappings.add(m);
        m = new Mapping("weight", "体重");
        mappings.add(m);
        m = new Mapping("income", "收入");
        mappings.add(m);
        m = new Mapping("birthday", "生日");
        mappings.add(m);
        m = new Mapping("entryDate", "入职时间");
        mappings.add(m);
        m = new Mapping("yearAndMonth", "年月");
        mappings.add(m);

        return  mappings;
    }
````

1. 创建解析信息对象
````java
ParseInfo info = new ParseInfo(file, mappings,3);
//如果强制检查映射是否都存在，则设置参数，默认不检查
info.setForceMatcher(true);
````

1. 调用工厂获取解析对象
````java
//获取解析器
Excel excel = ParseFactory.getExcelParse(info);
````

1.获取解析结果数据
````java
List<Map> data = excel.parseToMapList();
````

##### **注意**：解析支持的功能有如下：
* 解析每个单元格时，获取解析时的事件，根据自己需要，自行处理，需要配置解析时，在对应的单元格上配置CellHandler类；
* 映射关系支持json文件配置；
* 解析时，无需说明文件类型，程序自动识别，支持xls，xlsx，tsv，csv；
* 解析过程中，对应行事件、头部处理开始事件、头部处理结束事件、文件结束事件；
* 自动格式化
* 自动数据封装为目标Class类型



