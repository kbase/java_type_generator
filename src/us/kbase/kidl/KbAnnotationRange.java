package us.kbase.kidl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class represents kind of comment annotation called 'range'.
 */
public class KbAnnotationRange {
	
	Double minValue;
	Double maxValue;
	boolean isExclusiveMin;
	boolean isExclusiveMax;
	
	KbAnnotationRange() {
		minValue = null;
		maxValue = null;
		isExclusiveMin = false;
		isExclusiveMax = false;
	}
	
	/**
	 * the range is defined in the standard way (see: http://en.wikipedia.org/wiki/ISO_31-11):
	 * [min,max] where brackets indicate inclusive, or (min,max) where parentheses indicate
	 * exclusive, or where brackets in the outer direction also indicate exclusive as ]min,max[.
	 * 
	 * If brackets are left out completely, inclusive range is assumed.
	 * 
	 * If the min or max value is ommited, then no min or max value is assumed.
	 * 
	 */
	void parseRangeString(String range) throws KidlParseException {
		String rangeStr = range.trim();
		isExclusiveMin = false;
		if(rangeStr.startsWith("[")) {
			rangeStr = rangeStr.substring(1);
		} else if(rangeStr.startsWith("(") || rangeStr.startsWith("]")) {
			isExclusiveMin = true;
			rangeStr = rangeStr.substring(1);
		}

		isExclusiveMax = false;
		if(rangeStr.endsWith("]")) {
			rangeStr = rangeStr.substring(0, rangeStr.length()-1);
		} else if(rangeStr.endsWith(")") || rangeStr.endsWith("[")) {
			isExclusiveMax = true;
			rangeStr = rangeStr.substring(0, rangeStr.length()-1);
		}
		String [] values = rangeStr.split(",");
		if(values.length>=1) {
			minValue = null; maxValue = null;
			if(!values[0].trim().isEmpty()) {
				try {
					minValue = new Double(values[0].trim());
				} catch (Exception e) {
					throw new KidlParseException("Error in specifying the valid range of a number, invalid minimum value: "+e.getMessage());
				}
			}
		}
		if(values.length==2) {
			if(!values[1].trim().isEmpty()) {
				try {
					maxValue = new Double(values[1].trim());
				} catch (Exception e) {
					throw new KidlParseException("Error in specifying the valid range of a number, invalid minimum value: "+e.getMessage());
				}
			}
		}
		if(values.length > 2) {
			throw new KidlParseException("Error in specifying the valid range of a number, too many commas given");
		}
	}
	
	
	void loadFromComment(List<String> words) throws KidlParseException {
		if (words.size() == 0)
			throw new KidlParseException("range annotation without specifying a range is invalid");
		StringBuilder concatRange = new StringBuilder();
		for(String w:words) {
			concatRange.append(w);
		}
		parseRangeString(concatRange.toString());
	}
	
	void loadFromMap(Map<String,Object> data) throws KidlParseException {
		throw new KidlParseException("loadFromMap called for KbAnnotationRange!!  not yet supported");
	}
	
	public boolean isMinSet() {
		return minValue!=null;
	}
	public boolean isMaxSet() {
		return maxValue!=null;
	}
	
	
	public double getDoubleMinValue() {
		return minValue.doubleValue();
	}
	public int getIntMinValue() {
		return minValue.intValue();
	}
	
	public double getDoubleMaxValue() {
		return maxValue.doubleValue();
	}
	public int getIntMaxValue() {
		return maxValue.intValue();
	}

	
	
	Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		
		/*  NOT SUPPORTED YET IN PERL TYPE COMPILER, SO DON'T OUTPUT */
		
		return ret;
		
		
	}
	
	Object toJsonSchemaForFloat() {
		Map<String, Object> rangeMap = new TreeMap<String, Object>();
		if(isMinSet()) {
			rangeMap.put("minimum", minValue);
			if(isExclusiveMin) {
				rangeMap.put("exclusiveMinimum",new Boolean(true));
			}
		}
		if(isMaxSet()) {
			rangeMap.put("maximum", maxValue);
			if(isExclusiveMax) {
				rangeMap.put("exclusiveMaximum",new Boolean(true));
			}
		}
		return rangeMap;
	}
	
	Object toJsonSchemaForInt() {
		Map<String, Object> rangeMap = new TreeMap<String, Object>();
		if(isMinSet()) {
			rangeMap.put("minimum", new Long(minValue.longValue()));
			if(isExclusiveMin) {
				rangeMap.put("exclusiveMinimum",new Boolean(true));
			}
		}
		if(isMaxSet()) {
			rangeMap.put("maximum", new Long(maxValue.longValue()));
			if(isExclusiveMax) {
				rangeMap.put("exclusiveMaximum",new Boolean(true));
			}
		}
		return rangeMap;
	}
}
