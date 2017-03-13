package com.collector.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

public class ModelManipulation {

	public static Object getValueOfPropertyOfInstance(String property, Object instance)
			throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(instance.getClass());

		Object valueOfProperty = null;

		for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
			String propertyName = propertyDesc.getName();
			Object value = propertyDesc.getReadMethod().invoke(instance);

			if (property.equals(propertyName)) {
				valueOfProperty = value;
				break;
			}

		}

		return valueOfProperty;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTypeFromStringValue(String type, String value) {
		switch (type) {
		case "Integer":
			return (T) Integer.valueOf(value);
		case "Double":
			return (T) Double.valueOf(value);
		case "String":
			return (T) String.valueOf(value);
		case "Timestamp":
			return (T) Timestamp.valueOf(value);
		case "Boolean":
			return (T) Boolean.valueOf(value);
		default:
			return null;
		}
	}
}
