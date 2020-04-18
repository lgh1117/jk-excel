package jk.demo.excel.parse;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class Person {
    private Integer year;
    private Integer month;
    private Long age;
    private String name;
    private Date birthday;

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
