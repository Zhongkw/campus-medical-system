package com.campus.medical.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 参数验证工具类
 */
public class ValidationUtils {

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 验证对象参数
     *
     * @param obj 待验证对象
     * @return 验证结果，如果验证通过返回true，否则返回false
     */
    public static <T> boolean validate(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        return violations.isEmpty();
    }

    /**
     * 验证对象指定属性
     *
     * @param obj          待验证对象
     * @param propertyName 属性名
     * @return 验证结果，如果验证通过返回true，否则返回false
     */
    public static <T> boolean validateProperty(T obj, String propertyName) {
        Set<ConstraintViolation<T>> violations = validator.validateProperty(obj, propertyName);
        return violations.isEmpty();
    }

    /**
     * 验证邮箱格式
     *
     * @param email 待验证邮箱
     * @return 邮箱格式正确返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * 验证手机号格式
     *
     * @param phone 待验证手机号
     * @return 手机号格式正确返回true，否则返回false
     */
    public static boolean isPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String phoneRegex = "^1[3-9]\\d{9}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phone).matches();
    }

    /**
     * 验证身份证号格式
     *
     * @param idCard 待验证身份证号
     * @return 身份证号格式正确返回true，否则返回false
     */
    public static boolean isIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return false;
        }
        String idCardRegex = "^(\\d{15}(X|x)?|\\d{18})$";
        Pattern pattern = Pattern.compile(idCardRegex);
        return pattern.matcher(idCard).matches();
    }
}