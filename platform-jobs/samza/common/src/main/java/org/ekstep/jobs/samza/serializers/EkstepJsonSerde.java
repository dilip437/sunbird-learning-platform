package org.ekstep.jobs.samza.serializers;

import org.apache.samza.serializers.Serde;
import org.apache.samza.SamzaException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/*
 * A serializer for JSON strings that
 * <ol>
 *   <li>
 *     returns a LinkedHashMap<String, Object> upon deserialization.
 *   <li>
 *     enforces the 'dash-separated' property naming convention.
 * </ol>
 *
 * @author Mahesh Kumar Gangula
 */

public class EkstepJsonSerde<T> implements Serde<T> {

	private static final Logger LOG = LoggerFactory.getLogger(EkstepJsonSerde.class);
	private final Class<T> clazz;
	private transient ObjectMapper mapper = new ObjectMapper();

	/**
	 * Constructs a EkstepJsonSerde that returns a LinkedHashMap&lt;String,
	 * Object&lt; upon deserialization.
	 */
	public EkstepJsonSerde() {
		this(null);
	}

	/**
	 * Constructs a EkstepJsonSerde that (de)serializes POJOs of class
	 * {@code clazz}.
	 *
	 * @param clazz
	 *            the class of the POJO being (de)serialized.
	 */
	public EkstepJsonSerde(Class<T> clazz) {
		this.clazz = clazz;
	}

	public static <T> EkstepJsonSerde<T> of(Class<T> clazz) {
		return new EkstepJsonSerde<>(clazz);
	}

	@Override
	public byte[] toBytes(T obj) {
		if (obj != null) {
			try {
				String str = mapper.writeValueAsString(obj);
				return str.getBytes("UTF-8");
			} catch (Exception e) {
				throw new SamzaException("Error serializing data.", e);
			}
		} else {
			return null;
		}
	}

	@Override
	public T fromBytes(byte[] bytes) {
		if (bytes != null) {
			String str = null;
			try {
				str = new String(bytes, "UTF-8");
				if (clazz != null) {
					return mapper.readValue(str, clazz);
				} else {
					return mapper.readValue(str, new TypeReference<T>() {
					});
				}
			} catch (UnsupportedEncodingException e) {
				LOG.error("Error deserializing data. Unsupported encoding: " + bytes, e);
				return null;
			} catch (Exception e) {
				LOG.error("Error deserializing data: " + str, e);
				return null;
			}
		} else {
			return null;
		}
	}

}
