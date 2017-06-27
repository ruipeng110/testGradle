package priv.crp.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class JsonUtil {

	public static final ObjectMapper objectMapper = new ObjectMapper();

	public static final TypeFactory typeFactory = objectMapper.getTypeFactory();

	public static final ObjectMapper objectMapper_noerror = new ObjectMapper();

	static {
		objectMapper_noerror.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}




	public static <T> T readValue(String source, Class<T> clazz, Class<?>... generics){
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(clazz, generics);
		try{
			return objectMapper.readValue(source, javaType);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 我有一个理想，之后的无论有多少层泛型，都可以通过该方法解决
	 * @param source
	 * @param <T>
	 * @return
	 */
	public static <T> T readValue(String source, Class<T> clazz){
		try{
			return objectMapper.readValue(source, clazz);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public static Object readValueWithoutError(String source, JavaType javaType){
		try {
			return objectMapper_noerror.readValue(source, javaType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object readValue(String source, JavaType javaType){
		try {
			return objectMapper.readValue(source, javaType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 只要类中需要泛型，务必把泛型的类型在generics中带上，否则会导致泛型类型作用在错误的层级结构上!!
	 * 类型需要做冗余的传递，原因是可以很直观看到传递的内容是否有错。
	 * @param raw  原始类型
	 * @param generics 所有的泛型列表，
	 * @return
	 */
	public static JavaType getJavaType(Class<?> raw, LinkedList<Class<?>> generics){
		if (generics == null){
			generics = new LinkedList<>();
		}
		try{
			if (raw == null){
				return typeFactory.constructType(Object.class);
			}
			JavaType javaType = null;
			if (Collection.class.isAssignableFrom(raw)){
				if (generics.size() == 0){
					return typeFactory.constructCollectionType((Class<? extends Collection>) raw, Object.class);
				} else {
					Class<?> newRaw = generics.get(0);
					generics.remove(0);
					return typeFactory.constructCollectionType((Class<? extends Collection>) raw, getJavaType(newRaw, generics));
				}
			} else if (Map.class.isAssignableFrom(raw)){
				if (generics.size() == 0){
					return typeFactory.constructMapType((Class<? extends Map>) raw, Object.class, Object.class);
				}
//				else if (generics.size() == 1){
//					Class<?> newRaw = generics.get(0);
//					generics.remove(0);
//					return typeFactory.constructMapType((Class<? extends Map>) raw, getJavaType(newRaw, generics), typeFactory.constructSimpleType(Object.class, new JavaType[]{}));
//				}
				else {
					Class<?> newRaw = generics.get(0);
					generics.remove(0);
					JavaType keyType = getJavaType(newRaw, generics);
					Class<?> valueRaw = generics.size() == 0 ? null : generics.get(0);
					if (generics.size() > 0){
						generics.remove(0);
					}
					JavaType valueType = getJavaType(valueRaw, generics);
					return typeFactory.constructMapType((Class<? extends Map>) raw, keyType, valueType);
				}
			} else {
				List<JavaType> newGenerics = new LinkedList<>();
				TypeVariable<? extends Class<?>>[] typeVariables = raw.getTypeParameters();
				if (typeVariables.length == 0){
					return typeFactory.constructType(raw);
				}
				for (int i = 0;i < typeVariables.length;i++){
					Class<?> newRaw = generics.get(0);
					generics.remove(0);
					newGenerics.add(getJavaType(newRaw, generics));
				}
				return typeFactory.constructSimpleType(raw, newGenerics.toArray(new JavaType[]{}));
			}

		} catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static String toString (Object object){
		try{
			return objectMapper.writeValueAsString(object);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static String read(String source, String name){
		String value = null;
		try {
			JSONObject json = JSONObject.fromObject(source);
			value = json.get(name).toString();
		} catch (Exception e) {
			value = "";
		}
		if(StringUtils.isEmpty(value)){
			return "";
		}
		return value;
	}
	
	public static String read(String source, int index){
		String value = null;
		try {
			JSONArray json = JSONArray.fromObject(source);
			value = json.get(0).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isEmpty(value)){
			return "";
		}
		return value;
	}

	public static Object[] readUseJsonObject(String source){
		Object[] list = new Object[]{};
		if(StringUtils.isBlank(source)){
			return list;
		}
		try {
			JSONArray json = JSONArray.fromObject(source);
			list = json.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<String> read(String source){
		List<String> list = null;
		try {
//			JSONArray json = JSONArray.fromObject(source);
//			list = json.toList(json);
			ObjectMapper obj = new ObjectMapper();
			list = obj.readValue(source, List.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(list == null){
			list = new ArrayList<String>();
		}
		return list;
	}
}
