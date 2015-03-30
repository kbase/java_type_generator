module KBaseJobSystem {

    /* 
        A time in the format YYYY-MM-DDThh:mm:ssZ, where Z is either the
        character Z (representing the UTC timezone) or the difference
        in time to UTC in the format +/-HHMM, eg:
            2012-12-17T23:24:06-0500 (EST time)
            2013-04-03T08:56:32+0000 (UTC time)
            2013-04-03T08:56:32Z (UTC time)
    */
    typedef string timestamp;
    
    /* A job id. */
    typedef string job_id;
    
    /* A boolean. 0 = false, other = true. */
    typedef int boolean;
    
    /*
        time - the time the call was started;
        service_method - service defined in standard JSON RPC way, typically it's
            module name from spec-file like 'KBaseTrees';
        service_ver - specific version of deployed service;
        method - name of funcdef from spec-file corresponding to running method,
            like 'construct_species_tree' from trees service;
        method_params - the parameters of the method that performed this call;
        token - user token (required for any asynchronous method);
        job_id - job id if method is asynchronous (optional field).
    */
    typedef structure {
        timestamp time;
        string service;
        string service_ver;
        string method;
        list<UnspecifiedObject> method_params;
        job_id job_id;
    } MethodCall;

    /*
        service - service defined in standard JSON RPC way, typically it's
            module name from spec-file like 'KBaseTrees';
        service_ver - specific version of deployed service, last version is used 
            if this parameter is not defined (optional field);
        method - name of funcdef from spec-file corresponding to running method,
            like 'construct_species_tree' from trees service;
        method_params - the parameters of the method that performed this call;
        call_stack - upstream calls details including provenance and parent jobs 
            where calls are listed in order from outer to inner (optional field,
            could be omitted in case of empty list).
    */
    typedef structure {
        string service;
        string service_ver;
        string method;
        list<UnspecifiedObject> method_params;
        list<MethodCall> call_stack;
    } RunAsyncParams;

    funcdef run_async(RunAsyncParams params) returns (job_id job_id) authentication required;

    /*
        finished - indicates whether job is done (including error cases) or not,
            if the value is true then either of 'returned_data' or 'detailed_error'
            should be defined;
        returned_data - keeps exact copy of what original server method returned;
        detailed_error - keeps exact copy of what original server method has put
            in error block of JSON RPC response.
    */
    typedef structure {
        boolean finished;
        UnspecifiedObject returned_data;
        string detailed_error;
    } JobState;

    funcdef check_job(job_id job_id) returns (JobState job_state) authentication required;
};