package jk.demo.excel.gen;

import jk.core.builder.CreateBuilder;
import jk.demo.CreateBaseTest;
import jk.demo.Person;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @ClassName BuilderTest
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2022/2/6 7:57 下午
 */
public class BuilderTest extends CreateBaseTest {

    public static void main(String[] args){
        try {
            test();
            testDatasource();
            testTpl();
            csv();
            csvDatasource();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private static void csvDatasource()throws IOException {
        File file = getFile("builder-test-datasource-csv.csv");
        CreateBuilder.create(Person.class).csvFirstHeader(true).out(file).datasource(new BuilderDatasource()).write().close();
    }

    private static void csv()throws IOException{
        File file = getFile("builder-test-csv.csv");
        CreateBuilder.create(Person.class).csvFirstHeader(true).out(file).datas(getDatas()).write().close();
    }

    private static void testTpl()throws IOException{
        File file = getFile("builder-CreateBuilderTest-tpl.xlsx");
        File tpl = getFileFromClasspath("excel-tpl.xlsx");
        CreateBuilder.create(Person.class).sheetName("sheet").startRowIndex(5).template(tpl).out(file).datas(getDatas()).write().close();
    }

    private static void test() throws IOException {
        File file = getFile("builder-CreateBuilderTest.xlsx");
        CreateBuilder.create(Person.class).out(file).datas(getDatas()).write().close();
    }

    private static void testDatasource()throws IOException {
        File file = getFile("builder-CreateBuilderTest-datasource.xlsx");
        long time = System.currentTimeMillis();
        CreateBuilder.create(Person.class).out(file).datasource(new BuilderDatasource()).write().close();
        System.out.println("times: " + (System.currentTimeMillis() - time) + "ms");
    }

    private static void sheets(){
        File file = getFile("builder-CreateBuilderTest-datasource.xlsx");
        long time = System.currentTimeMillis();
//        CreateBuilder.create(Person.class).out(file).datas(new ExcelBuilderDatasource()).write().close();
        System.out.println("times: " + (System.currentTimeMillis() - time) + "ms");
    }

    private static List getDatas() {
        List<Person> rs = new ArrayList<>();
        for(int i = 0 ; i < 1000 ; i++){
            rs.add(new Person((new Random().nextInt(2021)),new Random().nextInt(12),(long)(new Random().nextInt(100)),"name" + i,new Date()));
        }
        return rs;
    }
}
