package us.kbase.kidl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import us.kbase.kidl.KbScalar.Type;

/**
 * Class represents kind of comment annotation called 'range'.
 */
public class KbAnnotationMetadata {
	
	/* keys are metadata name, values are the expression that is evaluated to compute the metadata value */
	private Map<String,String> wsMetaDataMap;
	
	public static final String TYPE_WS = "ws";
	
	KbAnnotationMetadata() {
		wsMetaDataMap = new TreeMap<String,String>();
	}
	
	/**
	 * how parse works: 
	 * first token is the metadata 'type', which for now can only be 'ws', this specifies the context for using the metadata
	 * if type is 'ws', then read in every token until we reach 'as', which becomes the expression to select the value of the metadata
	 * every token after 'as' is concatenated with a space and becomes the metadata name.  The name and expression are stored in this
	 * annotation object.
	 */
	void loadFromComment(List<String> words, KbTypedef caller) throws KidlParseException {
		
		KbType callerType = resolveTypedefs(caller);
		if (!(callerType instanceof KbStruct))
			throw new KidlParseException("Searchable annotation should be used only for structures");
		
		if (words.size() == 0)
			throw new KidlParseException("metadata annotation is invalid, must define at least the context for specifying the metadata (e.g. 'ws')");
		
		String type = words.get(0);
		words.remove(0);
		if(type.equals(TYPE_WS)) {
			if (words.size() < 1)
				throw new KidlParseException("metadata annotation is invalid, must define at least 2 tokens: @metadata ws [expression]");
			StringBuilder expression = new StringBuilder();
			StringBuilder metadataName = new StringBuilder();
			boolean seenAsKeyword = false;
			
			boolean isFirstWord = true; // 'as' cannot be the first token parsed (in case there is a field named 'as')
			for(String w:words) {
				if(!isFirstWord && w.toLowerCase().equals("as")) {
					seenAsKeyword = true;
					continue;
				}
				if(isFirstWord) { isFirstWord = false; }
				if(seenAsKeyword) { metadataName.append(w+" "); }
				else { expression.append(w); }
			}
			if(!seenAsKeyword) {
				metadataName.append(expression.toString());
			}
			
			validateExpression(expression.toString(),(KbStruct) callerType);
			
			String name = metadataName.toString().trim();
			if(wsMetaDataMap.containsKey(name)) {
				throw new KidlParseException("metadata annotation is invalid, you cannot redefine a metadata name; attempted to redefine '"+name+"'");
			}
			wsMetaDataMap.put(name,expression.toString());
		} else {
			throw new KidlParseException("metadata annotation is invalid, unsupported meta data type; only valid type is 'ws', you gave: "+ type);
		}
	}
	
	private static KbType resolveTypedefs(KbType type) {
		if (type instanceof KbTypedef) 
			return resolveTypedefs(((KbTypedef)type).getAliasType());
		return type;
	}
	
	/* we only support top level extraction of stuff, so expression must be a field name.
	 * additionally, the extraction is only valid if the item is a scalar type (in which case
	 * the type is cast to a string) or a list/mapping in which case it requires the operator
	 * length(field_name) to specify that we want the lenght of the field name.
	 * 
	 * If we extend this simple expression in the future, it should be validated here.
	 *  */
	void validateExpression(String expression, KbStruct caller) throws KidlParseException {
		List <KbStructItem> items = caller.getItems();
		expression = expression.trim();
		boolean gettingLengthOf = false;
		if(expression.startsWith("length(")) {
			if(expression.endsWith(")")) {
				expression = expression.substring(7);
				expression = expression.substring(0, expression.length()-1);
				gettingLengthOf = true;
			} else {
				throw new KidlParseException("metadata annotation is invalid, expression starts with length(, but does not have closing parenthesis");
			}
		}
		boolean foundExpression = false;
		for(KbStructItem i:items) {
			//System.out.println(i.getName() +" compare to " + expression);
			if(i.getName().equals(expression)) {
				KbType itemType = resolveTypedefs(i.getItemType());
				if(itemType instanceof KbScalar) {
					if(((KbScalar) itemType).getScalarType() != Type.stringType) {
						if(gettingLengthOf) {
							throw new KidlParseException("metadata annotation is invalid, if you are selecting an int or float for metadata, you cannot use: length("+expression+")");
						}
					}
					// we are ok
				} else if(itemType instanceof KbList) {
					if(!gettingLengthOf) {
						throw new KidlParseException("metadata annotation is invalid, if you are selecting a list for metadata, you must use: length("+expression+")");
					}
				} else if(itemType instanceof KbMapping) {
					if(!gettingLengthOf) {
						throw new KidlParseException("metadata annotation is invalid, if you are selecting a mapping for metadata, you must use: length("+expression+")");
					}
				} else if(itemType instanceof KbTuple) {
					if(!gettingLengthOf) {
						throw new KidlParseException("metadata annotation is invalid, if you are selecting a tuple for metadata, you must use: length("+expression+")");
					}
				} else {
					throw new KidlParseException("metadata annotation is invalid, you can only select fields that are scalars, lists, tuples, or mappings; '"+expression+"' is not one of these.");
				}
				foundExpression=true;
			}
		}
		if(!foundExpression) {
			throw new KidlParseException("metadata annotation is invalid, could not identify field named:'"+expression+"'");
		}
		
		
	}
	
	
	void loadFromMap(Map<String,Object> data) throws KidlParseException {
		throw new KidlParseException("loadFromMap called for KBAnnotationMetadata!!  not yet supported");
	}
	
	Object toJson() {
		Map<String, Object> ret = new TreeMap<String, Object>();
		/*  NOT SUPPORTED YET IN PERL TYPE COMPILER, SO DON'T OUTPUT */
		return ret;
	}
	
	Object toJsonSchema() {
		Map<String, Object> metaMap = new TreeMap<String, Object>();
		metaMap.put("metadata-ws", wsMetaDataMap);
		return metaMap;
	}
	
	
	
}
