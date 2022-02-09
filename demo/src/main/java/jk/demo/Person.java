package jk.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jk.core.ann.ExcelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
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
