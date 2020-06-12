package l.jk.json;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Person
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2020/6/11 下午8:47
 */
public class Person {
    private double weight;
    private float heigh;
    private int age;
    private String name;
    private Date birthday;
    private char sex;
    private boolean happy;
    private Long walk;

    private Map entry;
    private Person parent;


    private List<String> list;

    public Map getEntry() {
        return entry;
    }

    public void setEntry(Map entry) {
        this.entry = entry;
    }

    public Person getParent() {
        return parent;
    }

    public void setParent(Person parent) {
        this.parent = parent;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public float getHeigh() {
        return heigh;
    }

    public void setHeigh(float heigh) {
        this.heigh = heigh;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    public Long getWalk() {
        return walk;
    }

    public void setWalk(Long walk) {
        this.walk = walk;
    }

    @Override
    public String toString() {
        return "Person{" +
                "weight=" + weight +
                ", heigh=" + heigh +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", sex=" + sex +
                ", happy=" + happy +
                ", walk=" + walk +
                '}';
    }
}
