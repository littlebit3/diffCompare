package com.vt;

import com.vt.abstractz.DiffList;
import com.vt.annotation.CompareCollection;
import com.vt.annotation.CompareStringToCollection;
import com.vt.annotation.IgnoreCompare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;


@Slf4j
public class DiffCompare {

    public static <T extends DiffList> void findDiffFields(T sourceObj, T compareObj) {

        if (Objects.isNull(sourceObj) || Objects.isNull(compareObj)) {
            return;
        }
        if (sourceObj.getClass() != compareObj.getClass()) {
            return;
        }
        Field[] fields = sourceObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = null;
            Object value2 = null;
            if (field.getAnnotation(IgnoreCompare.class) != null) {
                continue;
            }

            try {
                value1 = field.get(sourceObj);
                value2 = field.get(compareObj);
            } catch (IllegalAccessException exception) {
                log.error("error");
            }
            CompareCollection compareCollectionAnnotation = field.getAnnotation(CompareCollection.class);
            if (compareCollectionAnnotation != null) {
                String compareFieldName = compareCollectionAnnotation.fieldName();

                if (Collection.class.isAssignableFrom(field.getType())) {

                    if (value1 instanceof Collection && value2 instanceof Collection) {

                        Collection<?> collection1 = (Collection<?>) value1;

                        if (isCollectionOfWrapperTypes(collection1)) {
                            Collection<?> collection2 = (Collection<?>) value2;

                            if (!(collection1.containsAll(collection2) && collection2.containsAll(collection1))) {

                                sourceObj.getDifferentList().add(field.getName());
                            }
                            continue;
                        }


                        Collection<? extends DiffList> sourceCollection = (Collection<? extends DiffList>) value1;
                        Collection<? extends DiffList> compareCollection = (Collection<? extends DiffList>) value2;

                        for (Object obj : sourceCollection) {
                            if (!(obj instanceof DiffList)) {
                                log.error("未继承DiffList:" + obj);
                                break;
                            }
                        }

                        for (Object obj : compareCollection) {
                            if (!(obj instanceof DiffList)) {
                                log.error("未继承DiffList:" + obj);
                                break;
                            }
                        }
                        //集合比较方法
                        findListDiffFields(sourceCollection, compareCollection, compareFieldName);
                        continue;
                    }

                }
            }

            if (value1 == null && value2 == null) {
                continue;
            }
            if (value1 == null && value2 != null) {
                sourceObj.getDifferentList().add(field.getName());
                continue;
            }
            if (value2 == null && value1 != null) {
                sourceObj.getDifferentList().add(field.getName());
                continue;
            }

            CompareStringToCollection compareStringToCollection = field.getAnnotation(CompareStringToCollection.class);
            if (Objects.nonNull(compareStringToCollection) && value1 instanceof String) {
                List<String> sourceList = Arrays.asList(String.valueOf(value1).split(compareStringToCollection.separator()));
                List<String> compareList = Arrays.asList(String.valueOf(value2).split(compareStringToCollection.separator()));
                if (!(sourceList.containsAll(compareList) && compareList.containsAll(sourceList))) {
                    sourceObj.getDifferentList().add(field.getName());
                }
                continue;
            }

            if (value1 instanceof String
                    || value1 instanceof Long || value1 instanceof Integer
                    || value1 instanceof Float || value1 instanceof Double) {
                if (!StringUtils.equals(value1.toString(), value2.toString())) {
                    sourceObj.getDifferentList().add(field.getName());
                }
            } else if (value1 instanceof Date) {
                    if (((Date) value1).compareTo((Date) value2) != 0) {
                        sourceObj.getDifferentList().add(field.getName());
                    }
                } else {
                    if (!Objects.equals(value1, value2)) {
                        sourceObj.getDifferentList().add(field.getName());
                    }
                }
            }

        }


    private static void findDifferentFileldsInfo(Object sourceObj, Object compareObj, List<String> differentList) {

        if (sourceObj == null || compareObj == null) {
            return;
        }
        if (sourceObj.getClass() != compareObj.getClass()) {
            return;
        }
        Field[] fields = sourceObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = null;
            Object value2 = null;
            if (field.getAnnotation(IgnoreCompare.class) != null) {
                continue;
            }

            try {
                value1 = field.get(sourceObj);
                value2 = field.get(compareObj);
            } catch (IllegalAccessException exception) {
                log.error("error");
            }
            if (value1 == null && value2 == null) {
                continue;
            }

            if (value1 == null && value2 != null) {
                differentList.add(field.getName());
                continue;
            }
            if (value1 instanceof String
                    || value1 instanceof Long || value1 instanceof Integer
                    || value1 instanceof Float || value1 instanceof Double) {
                if (StringUtils.equals(value1.toString(), value2.toString())) {
                    differentList.add(field.getName());
                }
            } else if (value1 instanceof Date) {
                if (((Date) value1).compareTo((Date) value2) != 0) {
                    differentList.add(field.getName());
                }
            } else {
                if (!Objects.equals(value1, value2)) {
                    differentList.add(field.getName());
                }
            }
        }
    }

    private static void findAllFields(Object obj, List<String> differentList) {
        if (Objects.isNull(obj)) {
            return;
        }
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(IgnoreCompare.class) != null) {
                continue;
            }
            differentList.add(field.getName());
        }
    }


    private static void findListDiffFields(Collection<? extends DiffList> sourceCollection
            , Collection<? extends DiffList> compareCollection, String fieldName) {

        if (StringUtils.isBlank(fieldName) || !isFieldPresent(sourceCollection, fieldName)){
            return;
        }

        Map<Object,?extends DiffList> sourceCollectionMap = toMapByField(sourceCollection, fieldName);
        Map<Object,?extends DiffList> compareCollectionMap = toMapByField(compareCollection, fieldName);

        for (Map.Entry<Object, ? extends DiffList> entry : sourceCollectionMap.entrySet()) {
            Object key = entry.getKey();
            DiffList sourceObj = entry.getValue();
            DiffList compareObj = compareCollectionMap.get(key);

            if (Objects.isNull(compareObj)){
                findAllFields(compareObj,sourceObj.getDifferentList());
            }else {
                findDifferentFileldsInfo(sourceObj,compareObj,sourceObj.getDifferentList());
            }
        }

    }


    private static <T> Map<Object,T> toMapByField(Collection<T> collection, String fieldName){

        Map<Object,T> resultMap = new HashMap<>();
        if (collection == null ||collection.isEmpty()){
            return resultMap;
        }
        for (T element : collection) {


            try {
                Field field = element.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object key = field.get(element);
                resultMap.put(key,element);
            }catch (NoSuchFieldException | IllegalAccessException e){
                log.error("toMapByField error",e);
            }
        }
        return resultMap;
    }

    private static <T> boolean isFieldPresent(Collection<T> collection, String fieldName){
        if (collection == null ||collection.isEmpty()){
            return false;
        }
        Class<?> clazz = collection.iterator().next().getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return true;
        }catch (NoSuchFieldException e){
            return false;
        }
    }

    private static boolean isWrapperType(Class<?> clazz){
        return clazz.equals(Integer.class)
                || clazz.equals(String.class)
                || clazz.equals(Long.class)
                || clazz.equals(Short.class)
                || clazz.equals(Byte.class)
                || clazz.equals(Double.class)
                || clazz.equals(Float.class)
                || clazz.equals(Character.class)
                || clazz.equals(Boolean.class);
    }
    private static boolean isCollectionOfWrapperTypes(Collection<?> collection){
        for (Object obj : collection) {
            if ( obj != null && !isWrapperType(obj.getClass())){
                return false;
            }
        }
        return true;
    }
}



































