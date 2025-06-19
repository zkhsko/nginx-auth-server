package org.nginx.auth.util;

import org.springframework.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dongpo.li
 * @date 2024/1/12 14:15
 */
public class BeanCopyUtil {

    private BeanCopyUtil() {
    }

    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Class<?>, BeanCopier>> cache = new ConcurrentHashMap<>();

    public static <T> T copy(Object source, T target) {
        BeanCopier beanCopier = getCacheBeanCopier(source.getClass(), target.getClass());
        beanCopier.copy(source, target, null);
        return target;
    }

    private static <S, T> BeanCopier getCacheBeanCopier(Class<S> source, Class<T> target) {
        ConcurrentHashMap<Class<?>, BeanCopier> copierConcurrentHashMap = cache.computeIfAbsent(source, k -> new ConcurrentHashMap<>(16));
        return copierConcurrentHashMap.computeIfAbsent(target, k -> BeanCopier.create(source, target, false));
    }


}
