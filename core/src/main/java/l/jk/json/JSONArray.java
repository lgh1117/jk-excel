package l.jk.json;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @ClassName JSONArray
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2020/6/9 下午8:32
 */
public class JSONArray implements List<Object>, Cloneable, RandomAccess, Serializable {


    private static final long  serialVersionUID = 1L;
    private final List<Object> list;
    protected transient Object relatedArray;
    protected transient Type componentType;

    public JSONArray(){
        this.list = new ArrayList<Object>();
    }

    public JSONArray(List<Object> list){
        this.list = list;
    }

    public JSONArray(int initialCapacity){
        this.list = new ArrayList<Object>(initialCapacity);
    }

    /**
     * @since 1.1.16
     * @return
     */
    public Object getRelatedArray() {
        return relatedArray;
    }

    public void setRelatedArray(Object relatedArray) {
        this.relatedArray = relatedArray;
    }

    public Type getComponentType() {
        return componentType;
    }

    public void setComponentType(Type componentType) {
        this.componentType = componentType;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public Iterator<Object> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public boolean add(Object e) {
        return list.add(e);
    }

    public JSONArray fluentAdd(Object e) {
        list.add(e);
        return this;
    }

    public boolean remove(Object o) {
        return list.remove(o);
    }

    public JSONArray fluentRemove(Object o) {
        list.remove(o);
        return this;
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public boolean addAll(Collection<? extends Object> c) {
        return list.addAll(c);
    }

    public JSONArray fluentAddAll(Collection<? extends Object> c) {
        list.addAll(c);
        return this;
    }

    public boolean addAll(int index, Collection<? extends Object> c) {
        return list.addAll(index, c);
    }

    public JSONArray fluentAddAll(int index, Collection<? extends Object> c) {
        list.addAll(index, c);
        return this;
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public JSONArray fluentRemoveAll(Collection<?> c) {
        list.removeAll(c);
        return this;
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public JSONArray fluentRetainAll(Collection<?> c) {
        list.retainAll(c);
        return this;
    }

    public void clear() {
        list.clear();
    }

    public JSONArray fluentClear() {
        list.clear();
        return this;
    }

    public Object set(int index, Object element) {
        if (index == -1) {
            list.add(element);
            return null;
        }

        if (list.size() <= index) {
            for (int i = list.size(); i < index; ++i) {
                list.add(null);
            }
            list.add(element);
            return null;
        }

        return list.set(index, element);
    }

    public JSONArray fluentSet(int index, Object element) {
        set(index, element);
        return this;
    }

    public void add(int index, Object element) {
        list.add(index, element);
    }

    public JSONArray fluentAdd(int index, Object element) {
        list.add(index, element);
        return this;
    }

    public Object remove(int index) {
        return list.remove(index);
    }

    public JSONArray fluentRemove(int index) {
        list.remove(index);
        return this;
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public Object get(int index) {
        return list.get(index);
    }

    public JSONObject getJSONObject(int index) {
        Object value = list.get(index);

        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }

        if (value instanceof Map) {
            return new JSONObject((Map) value);
        }
        Map<String, Object> rs = JSONUtils.toMap(value);
        return new JSONObject( rs);
    }

    public JSONArray getJSONArray(int index) {
        Object value = list.get(index);

        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }

        if (value instanceof List) {
            return new JSONArray((List) value);
        }

        List rs = JSONUtils.toList(value);
        return new JSONArray(rs);
    }

    public <T> T getObject(int index, Class<T> clazz) {
        Object obj = list.get(index);
        return JSONUtils.toJavaObject(obj,clazz);
    }


    public Boolean getBoolean(int index) {
        Object value = get(index);

        if (value == null) {
            return null;
        }

        return TypeUtils.castToBoolean(value);
    }

    public boolean getBooleanValue(int index) {
        Object value = get(index);

        if (value == null) {
            return false;
        }

        return TypeUtils.castToBoolean(value).booleanValue();
    }

    public Byte getByte(int index) {
        Object value = get(index);

        return TypeUtils.castToByte(value);
    }

    public byte getByteValue(int index) {
        Object value = get(index);

        Byte byteVal = TypeUtils.castToByte(value);
        if (byteVal == null) {
            return 0;
        }

        return byteVal.byteValue();
    }

    public Short getShort(int index) {
        Object value = get(index);

        return TypeUtils.castToShort(value);
    }

    public short getShortValue(int index) {
        Object value = get(index);

        Short shortVal = TypeUtils.castToShort(value);
        if (shortVal == null) {
            return 0;
        }

        return shortVal.shortValue();
    }

    public Integer getInteger(int index) {
        Object value = get(index);

        return TypeUtils.castToInt(value);
    }

    public int getIntValue(int index) {
        Object value = get(index);

        Integer intVal = TypeUtils.castToInt(value);
        if (intVal == null) {
            return 0;
        }

        return intVal.intValue();
    }

    public Long getLong(int index) {
        Object value = get(index);

        return TypeUtils.castToLong(value);
    }

    public long getLongValue(int index) {
        Object value = get(index);

        Long longVal = TypeUtils.castToLong(value);
        if (longVal == null) {
            return 0L;
        }

        return longVal.longValue();
    }

    public Float getFloat(int index) {
        Object value = get(index);

        return TypeUtils.castToFloat(value);
    }

    public float getFloatValue(int index) {
        Object value = get(index);

        Float floatValue = TypeUtils.castToFloat(value);
        if (floatValue == null) {
            return 0F;
        }

        return floatValue.floatValue();
    }

    public Double getDouble(int index) {
        Object value = get(index);

        return TypeUtils.castToDouble(value);
    }

    public double getDoubleValue(int index) {
        Object value = get(index);

        Double doubleValue = TypeUtils.castToDouble(value);
        if (doubleValue == null) {
            return 0D;
        }

        return doubleValue.doubleValue();
    }

    public BigDecimal getBigDecimal(int index) {
        Object value = get(index);

        return TypeUtils.castToBigDecimal(value);
    }

    public BigInteger getBigInteger(int index) {
        Object value = get(index);

        return TypeUtils.castToBigInteger(value);
    }

    public String getString(int index) {
        Object value = get(index);

        return TypeUtils.castToString(value);
    }

    public java.util.Date getDate(int index) {
        Object value = get(index);

        return TypeUtils.castToDate(value);
    }

    public java.sql.Date getSqlDate(int index) {
        Object value = get(index);

        return TypeUtils.castToSqlDate(value);
    }

    public java.sql.Timestamp getTimestamp(int index) {
        Object value = get(index);

        return TypeUtils.castToTimestamp(value);
    }

    /**
     * @since  1.2.23
     */
    public <T> List<T> toJavaList(Class<T> clazz) {
        return JSONUtils.toJavaObjectList(this,clazz);
    }

    public static JSONArray parseArray(String value){
        List rs = JSONUtils.toJavaObjectList(value,JSONObject.class);
        return new JSONArray(rs);
    }

    public static JSONArray parseArray(Object value){
        List rs = null;
        try {
            rs = JSONUtils.toJavaObjectList(value, JSONObject.class);
        }catch (Exception ex){
            rs = JSONUtils.toList(value);
        }
        return new JSONArray(rs);
    }

    @Override
    public Object clone() {
        return new JSONArray(new ArrayList<Object>(list));
    }

    public boolean equals(Object obj) {
        return this.list.equals(obj);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public String toString() {
        return JSONUtils.toJSONString(list);
    }
}
