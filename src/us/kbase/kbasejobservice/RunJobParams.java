
package us.kbase.kbasejobservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import us.kbase.common.service.UObject;


/**
 * <p>Original spec-file type: RunJobParams</p>
 * <pre>
 * service - service defined in standard JSON RPC way, typically it's
 *     module name from spec-file like 'KBaseTrees';
 * service_ver - specific version of deployed service, last version is used 
 *     if this parameter is not defined (optional field);
 * method - name of funcdef from spec-file corresponding to running method,
 *     like 'construct_species_tree' from trees service;
 * method_params - the parameters of the method that performed this call;
 * context - context of current method call including nested call history
 *     (optional field, could be omitted in case there is no call history).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "service",
    "service_ver",
    "method",
    "method_params",
    "context"
})
public class RunJobParams {

    @JsonProperty("service")
    private String service;
    @JsonProperty("service_ver")
    private String serviceVer;
    @JsonProperty("method")
    private String method;
    @JsonProperty("method_params")
    private List<UObject> methodParams;
    /**
     * <p>Original spec-file type: Context</p>
     * <pre>
     * call_stack - upstream calls details including nested service calls and 
     *     parent jobs where calls are listed in order from outer to inner.
     * </pre>
     * 
     */
    @JsonProperty("context")
    private Context context;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("service")
    public String getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(String service) {
        this.service = service;
    }

    public RunJobParams withService(String service) {
        this.service = service;
        return this;
    }

    @JsonProperty("service_ver")
    public String getServiceVer() {
        return serviceVer;
    }

    @JsonProperty("service_ver")
    public void setServiceVer(String serviceVer) {
        this.serviceVer = serviceVer;
    }

    public RunJobParams withServiceVer(String serviceVer) {
        this.serviceVer = serviceVer;
        return this;
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method;
    }

    public RunJobParams withMethod(String method) {
        this.method = method;
        return this;
    }

    @JsonProperty("method_params")
    public List<UObject> getMethodParams() {
        return methodParams;
    }

    @JsonProperty("method_params")
    public void setMethodParams(List<UObject> methodParams) {
        this.methodParams = methodParams;
    }

    public RunJobParams withMethodParams(List<UObject> methodParams) {
        this.methodParams = methodParams;
        return this;
    }

    /**
     * <p>Original spec-file type: Context</p>
     * <pre>
     * call_stack - upstream calls details including nested service calls and 
     *     parent jobs where calls are listed in order from outer to inner.
     * </pre>
     * 
     */
    @JsonProperty("context")
    public Context getContext() {
        return context;
    }

    /**
     * <p>Original spec-file type: Context</p>
     * <pre>
     * call_stack - upstream calls details including nested service calls and 
     *     parent jobs where calls are listed in order from outer to inner.
     * </pre>
     * 
     */
    @JsonProperty("context")
    public void setContext(Context context) {
        this.context = context;
    }

    public RunJobParams withContext(Context context) {
        this.context = context;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((((((((("RunJobParams"+" [service=")+ service)+", serviceVer=")+ serviceVer)+", method=")+ method)+", methodParams=")+ methodParams)+", context=")+ context)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
