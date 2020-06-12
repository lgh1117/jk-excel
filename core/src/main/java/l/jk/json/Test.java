package l.jk.json;

import java.util.*;

/**
 * @ClassName Test
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2020/6/11 下午8:47
 */
public class Test {

    public static void main(String[] args){
        Person p = new Person();
        p.setAge(1);
        p.setBirthday(new Date(2020,1,1,12,23,25));
        p.setHappy(true);
        p.setHeigh(12.3f);
        p.setSex('9');
        p.setWeight(5.343D);
        p.setWalk(9999L);
        p.setName("test");
        JSONObject jo = JSONObject.parseObject(p);
        System.out.println(jo);
        List<Person> list = new ArrayList<>();
        list.add(p);
        JSONArray ja = JSONArray.parseArray(list);
        System.out.println(ja);
        String job = "{\"birthday\":61538675005000,\"heigh\":12.3,\"sex\":\"9\",\"happy\":true,\"name\":\"test\",\"weight\":5.343,\"age\":1,\"walk\":9999}";
        String jab = "[{\"weight\":5.343,\"heigh\":12.3,\"age\":1,\"name\":\"test\",\"birthday\":61538675005000,\"sex\":\"9\",\"happy\":true,\"walk\":9999}]";
        JSONObject rsObj = JSONObject.parseObject(job);
        System.out.println(rsObj);
        JSONArray rsArr = JSONArray.parseArray(jab);
        System.out.println(rsArr);
        List<String> strList = new ArrayList<>();
        strList.add("1");
        strList.add("2");
        strList.add("3");
        strList.add("4rr");
        strList.add("5");
        p.setList(strList);

        Map<String, Object> m = new HashMap<>();
        m.put("111",12312);
        m.put("2222",444);
        m.put("333www","123123212dfsdf");
        p.setEntry(m);

        Person pp = new Person();
        pp.setName("pppppppp");
        p.setParent(pp);

        System.out.println(JSONObject.parseObject(p));

        System.out.println(JSONUtils.toList(strList));
        System.out.println(JSONArray.parseArray(list));
        rsArr = JSONArray.parseArray(strList);
        System.out.println(rsArr);
    }
}
