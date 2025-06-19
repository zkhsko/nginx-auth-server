package org.nginx.auth.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author dongpo.li
 * @date 2024/1/19 19:53
 */
public class ValidatorUtil {
    private static final Logger logger = LoggerFactory.getLogger(ValidatorUtil.class);

    private ValidatorUtil() {
    }

    public static <T> Map<String, String> validate(T object) {
        Map<String, String> rtn = new HashMap<>();

        Validator validatorFactory = Validation.buildDefaultValidatorFactory()
                .getValidator();
        Set<ConstraintViolation<T>> validateResultSet = validatorFactory.validate(object);
        if (CollectionUtils.isNotEmpty(validateResultSet)) {
            Iterator<ConstraintViolation<T>> iterator = validateResultSet.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                Path propertyPath = violation.getPropertyPath();
                String field = propertyPath.toString();
                String message = violation.getMessage();

                rtn.put(field + "FormError", message);

                // 以下是打印出错日志,排查问题用的

                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

                StackTraceElement stackTraceElement = stackTrace[2];
                String className = stackTraceElement.getClassName();
                int lineNumber = stackTraceElement.getLineNumber();
                String methodName = stackTraceElement.getMethodName();
                logger.error("param error: {}#{}:{} {}", className, methodName, lineNumber, message);
            }
        }

        return rtn;
    }

}
