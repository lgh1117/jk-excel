# jk-excel
基于poi，做excel解析，自动识别excel或csv、tsv文件格式，单sheet最大生成百万数据在50秒以内，内存15m左右。

## 优势：
  *  或许你解析一个Excel、CSV、TSV需要上百行代码，并且性能不佳，不利于维护、读写，使用此组件后，你会发现一行代码就可以解决你的问题，并且可以实现分批获取数据，并生成文件。

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
//返回第一个sheet解析的结果，可以指定sheet，在配置ParseInfo时
//也可以根据需要，调用不同的解析获取解析结果
List<Map> data = excel.parseToMapList();
// your's code in here for your biz
````

1.Builder的使用例子
# bean类
```java
public class Person {
    @ExcelProperty(name = "年")
    private Integer year;
    @ExcelProperty(name = "月份")
    private Integer month;
    @ExcelProperty(name = "年龄")
    private Long age;
    @ExcelProperty(name = "姓名")
    private String name;
    private Date birthday;

    public Person() {
    }

    public Person(Integer year, Integer month, Long age, String name, Date birthday) {
        this.year = year;
        this.month = month;
        this.age = age;
        this.name = name;
        this.birthday = birthday;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
}
```
# 1). 生成文件demo：
```java
    private static List getDatas() \{
        List<Person> rs = new ArrayList<>();
        for(int i = 0 ; i < 1000 ; i++){
            rs.add(new Person((new Random().nextInt(2021)),new Random().nextInt(12),(long)(new Random().nextInt(100)),"name" + i,new Date()));
        }
        return rs;
    }
    
    private static void csvDatasource()throws IOException {
        File file = new File("builder-test-datasource-csv.csv");
        CreateBuilder.create(Person.class).csvFirstHeader(true).out(file).datasource(new BuilderDatasource()).write().close();
    }

    private static void csv()throws IOException{
        File file = new File("builder-test-csv.csv");
        CreateBuilder.create(Person.class).csvFirstHeader(true).out(file).datas(getDatas()).write().close();
    }

    private static void testTpl()throws IOException{
        File file = new File("builder-CreateBuilderTest-tpl.xlsx");
        File tpl = getFileFromClasspath("excel-tpl.xlsx");
        CreateBuilder.create(Person.class).sheetName("sheet").startRowIndex(5).template(tpl).out(file).datas(getDatas()).write().close();
    }

    private static void test() throws IOException {
        File file = new File("builder-CreateBuilderTest.xlsx");
        CreateBuilder.create(Person.class).out(file).datas(getDatas()).write().close();
    }

    private static void testDatasource()throws IOException {
        File file = new File("builder-CreateBuilderTest-datasource.xlsx");
        long time = System.currentTimeMillis();
        CreateBuilder.create(Person.class).out(file).datasource(new BuilderDatasource()).write().close();
        System.out.println("times: " + (System.currentTimeMillis() - time) + "ms");
    }

    
```

# 2）. 解析文件demo
```java
public static void testHandleCsv(){
        File file = getFile("common_test.csv");
        List<Person> list = ParseBuilder.create(Person.class).dataFile(file).parseToBean();
        for(Person person : list) {
            System.out.println(person);
        }
    }


    public static void testCsv(){
        File file = getFile("common_test.csv");
        List<Person> list = ParseBuilder.create(Person.class).beforeParse(new MyExcelBuilderBeforAction()).dataFile(file).parseToBean();
        for(Person person : list) {
            System.out.println(person);
        }
    }

    private static List<Mapping> getMapping() {
        List<Mapping> rs =new ArrayList<>();
        rs.add(new Mapping("name","姓名"));
        rs.add(new Mapping("age","年龄"));
        rs.add(new Mapping("year","年"));
        rs.add(new Mapping("month","月份"));
        return rs;
    }

    public static void testNoHeaderCsv(){
        File file = getFile("common_no_header_test.csv");
        ParseBuilder builder = ParseBuilder.create().autoDetectorCharset().dataFile(file).mapping(getMappingNoHeader());
        ParseInfo info = builder.getParseInfo();
        info.setCsvFirstIsHeader(false);
        info.setFileType(FileType.TSV);
        List<Map> list = builder.parseToMapList();
        for(Map person : list) {
            System.out.println(person);
        }
    }
    private static List<Mapping> getMappingNoHeader() {
        List<Mapping> rs =new ArrayList<>();
        rs.add(new Mapping("column0","column0",0));
        rs.add(new Mapping("column1","column1",1));
        rs.add(new Mapping("column2","column2",2));
        rs.add(new Mapping("column3","column3",3));
        return rs;
    }

    private static void testOrderSheet(){
        File file = getFile("common_sheets.xlsx");
        String sheetName = "person";
        List<Map> list = ParseBuilder.create().dataFile(file).mapping(getSheetMappings()).parseAll().get(sheetName);
        for(Map d : list) {
            System.out.println(d);
        }
    }

    private static List<Mapping> getSheetMappings() {
        List<Mapping> rs =new ArrayList<>();
        rs.add(new Mapping("person","name","姓名"));
        rs.add(new Mapping("person","age","年龄"));
        rs.add(new Mapping("person","year","年"));
        rs.add(new Mapping("person","month","月份"));
        return rs;
    }


    public static void testBean(){
        File file = getFile("common_sheets.xlsx");
        List<Person> list = ParseBuilder.create(Person.class).dataIndex(3).dataFile(file).parseToBean();
        for(Person person : list) {
            System.out.println(person);
        }
    }
    public static void testMap(){
        File file = getFile("common_sheets.xlsx");
        List<Map> rs = ParseBuilder.create().mappingFile(getFile("person.json")).dataFile(file).parseToMapList();
        for(Map map : rs) {
            System.out.println(map);
        }
    }
```

##### **注意**：解析支持的功能有如下：
* 解析每个单元格时，获取解析时的事件，根据自己需要，自行处理，需要配置解析时，在对应的单元格上配置CellHandler类；
* 映射关系支持json文件配置；
* 解析时，无需说明文件类型，程序自动识别，支持xls，xlsx，tsv，csv；
* 解析过程中，对应行事件、头部处理开始事件、头部处理结束事件、文件结束事件；
* 自动格式化
* 自动数据封装为目标Class类型



