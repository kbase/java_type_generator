package us.kbase.scripts.tests.test4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.type.TypeReference;

import junit.framework.Assert;

import us.kbase.Tuple2;
import us.kbase.UObject;
import us.kbase.scripts.tests.test4.boolandobjecttest.BoolStruct;
import us.kbase.scripts.tests.test4.boolandobjecttest.BoolandobjecttestClient;
import us.kbase.scripts.tests.test4.boolandobjecttest.ObjectStruct;

public class Test4 {
	
	public Test4(BoolandobjecttestClient client) throws Exception {
		BoolStruct boolStr = new BoolStruct();
		Map<String, Boolean> map1 = new LinkedHashMap<String, Boolean>();
		map1.put("0", false);
		map1.put("1", true);
		Tuple2<Boolean, Boolean> tuple1 = new Tuple2<Boolean, Boolean>().withE1(true).withE2(false);
		boolStr.withVal1(false).withVal2(Arrays.asList(true, false, true)).withVal3(map1).withVal4(tuple1);
		List<Object> forTest = Arrays.asList(5, "testing", 17.44, true, boolStr);
		for (Object val : forTest) {
			ObjectStruct objStr = new ObjectStruct().withVal1(new UObject(val)).withVal2(Arrays.asList(new UObject(val)));
			Map<String, UObject> map2 = new LinkedHashMap<String, UObject>();
			map2.put("key", new UObject(val));
			Tuple2<UObject, UObject> tuple2 = new Tuple2<UObject, UObject>().withE1(new UObject(val)).withE2(null);
			objStr.withVal3(map2).withVal4(tuple2);
			Tuple2<UObject, ObjectStruct> ret2 = client.objectCheck(new UObject(val), objStr);
			checkObject(val, ret2.getE1());
			checkObject(val, ret2.getE2().getVal1());
			checkObject(val, ret2.getE2().getVal2().get(0));
			checkObject(val, ret2.getE2().getVal3().get("key"));
			checkObject(val, ret2.getE2().getVal4().getE1());
			Assert.assertNull(ret2.getE2().getVal4().getE2());
			if (val instanceof BoolStruct) {
				checkBoolStruct(ret2.getE1().asClassInstance(BoolStruct.class)); 
				Tuple2<BoolStruct, BoolObjectStruct> ret3 = UObject.transformObjectToObject(ret2, new TypeReference<Tuple2<BoolStruct, BoolObjectStruct>>() {});
				checkBoolStruct(ret3.getE1());
				checkBoolObjectStruct(ret3.getE2());
				checkBoolStruct(ret2.getE1().asClassInstance(BoolStruct.class));
				checkJsonTreeForBoolStruct(ret2.getE1().asJsonNode());
				// Transform into json tree test
			    JsonNode tree = new UObject(ret2).asJsonNode();
			    Assert.assertTrue(tree.isArray());
			    Assert.assertEquals(2, tree.size());
			    checkJsonTreeForObjectStruct(tree.get(1));
			}
		}
	}

	public void checkJsonTreeForBoolStruct(JsonNode node) {
		Assert.assertFalse(node.get("val1").getBooleanValue());
		Assert.assertEquals(3, node.get("val2").size());
		Assert.assertTrue(node.get("val2").get(0).getBooleanValue());
		Assert.assertEquals(2, node.get("val3").size());
		Assert.assertFalse(node.get("val3").get("0").getBooleanValue());
		Assert.assertTrue(node.get("val4").get(0).getBooleanValue());
	}

	public void checkJsonTreeForObjectStruct(JsonNode node) {
		Assert.assertFalse(node.get("val1").get("val1").getBooleanValue());
		Assert.assertEquals(3, node.get("val2").get(0).get("val2").size());
		Assert.assertTrue(node.get("val2").get(0).get("val2").get(0).getBooleanValue());
		Assert.assertEquals(2, node.get("val3").get("key").get("val3").size());
		Assert.assertFalse(node.get("val3").get("key").get("val3").get("0").getBooleanValue());
		Assert.assertTrue(node.get("val4").get(0).get("val4").get(0).getBooleanValue());
	}

	public void checkBoolObjectStruct(BoolObjectStruct bos) {
		checkBoolStruct(bos.getVal1());
		checkBoolStruct(bos.getVal2().get(0));
		checkBoolStruct(bos.getVal3().get("key"));
		checkBoolStruct(bos.getVal4().getE1());
	}
	
	private static void checkBoolStruct(BoolStruct e2) {
		Assert.assertFalse(e2.getVal1());
		Assert.assertEquals(3, e2.getVal2().size());
		Assert.assertTrue(e2.getVal2().get(0));
		Assert.assertEquals(2, e2.getVal3().size());
		Assert.assertFalse(e2.getVal3().get("0"));
		Assert.assertTrue(e2.getVal4().getE1());
	}

	private static void checkObject(Object expected, UObject actual) throws JsonProcessingException {
		if (expected.getClass().getName().startsWith("java.lang.")) {
			Assert.assertEquals(expected, actual.asScalar());
		} else {
			Assert.assertTrue(actual.isMap());
			Map<String, UObject> map0 = actual.asMap();
			Assert.assertEquals(false, map0.get("val1").asScalar());
			Assert.assertTrue(map0.get("val2").isList());
			List<Boolean> list1 = map0.get("val2").asInstance();
			Assert.assertEquals(3, list1.size());
			Assert.assertTrue(list1.get(0));
			Assert.assertTrue(map0.get("val3").isMap());
			Map<String, Boolean> map1 = map0.get("val3").asInstance();
			Assert.assertEquals(2, map1.size());
			Assert.assertFalse(map1.get("0"));
			Assert.assertTrue(map0.get("val4").isList());
			List<Boolean> list2 = map0.get("val4").asInstance();
			Assert.assertEquals(2, list2.size());
			Assert.assertTrue(list2.get(0));
		}
	}
	
	public static class BoolObjectStruct {

	    @JsonProperty("val1")
	    private BoolStruct val1;
	    @JsonProperty("val2")
	    private List<BoolStruct> val2 = new ArrayList<BoolStruct>();
	    @JsonProperty("val3")
	    private Map<String, BoolStruct> val3;
	    @JsonProperty("val4")
	    private Tuple2 <BoolStruct, BoolStruct> val4;
	    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

	    @JsonProperty("val1")
	    public BoolStruct getVal1() {
	        return val1;
	    }

	    @JsonProperty("val1")
	    public void setVal1(BoolStruct val1) {
	        this.val1 = val1;
	    }

	    public BoolObjectStruct withVal1(BoolStruct val1) {
	        this.val1 = val1;
	        return this;
	    }

	    @JsonProperty("val2")
	    public List<BoolStruct> getVal2() {
	        return val2;
	    }

	    @JsonProperty("val2")
	    public void setVal2(List<BoolStruct> val2) {
	        this.val2 = val2;
	    }

	    public BoolObjectStruct withVal2(List<BoolStruct> val2) {
	        this.val2 = val2;
	        return this;
	    }

	    @JsonProperty("val3")
	    public Map<String, BoolStruct> getVal3() {
	        return val3;
	    }

	    @JsonProperty("val3")
	    public void setVal3(Map<String, BoolStruct> val3) {
	        this.val3 = val3;
	    }

	    public BoolObjectStruct withVal3(Map<String, BoolStruct> val3) {
	        this.val3 = val3;
	        return this;
	    }

	    @JsonProperty("val4")
	    public Tuple2 <BoolStruct, BoolStruct> getVal4() {
	        return val4;
	    }

	    @JsonProperty("val4")
	    public void setVal4(Tuple2 <BoolStruct, BoolStruct> val4) {
	        this.val4 = val4;
	    }

	    public BoolObjectStruct withVal4(Tuple2 <BoolStruct, BoolStruct> val4) {
	        this.val4 = val4;
	        return this;
	    }

	    @JsonAnyGetter
	    public Map<java.lang.String, Object> getAdditionalProperties() {
	        return this.additionalProperties;
	    }

	    @JsonAnySetter
	    public void setAdditionalProperties(java.lang.String name, Object value) {
	        this.additionalProperties.put(name, value);
	    }

	}
}