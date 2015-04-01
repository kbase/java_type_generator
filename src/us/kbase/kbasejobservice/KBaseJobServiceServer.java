package us.kbase.kbasejobservice;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.UObject;

//BEGIN_HEADER
import java.util.LinkedHashMap;
import java.util.Map;
//END_HEADER

/**
 * <p>Original spec-file module name: KBaseJobService</p>
 * <pre>
 * </pre>
 */
public class KBaseJobServiceServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    private int lastJobId = 0;
    private Map<String, RunJobParams> jobs = new LinkedHashMap<>();
    //END_CLASS_HEADER

    public KBaseJobServiceServer() throws Exception {
        super("KBaseJobService");
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: run_job</p>
     * <pre>
     * Start a new job
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasejobservice.RunJobParams RunJobParams}
     * @return   parameter "job_id" of original type "job_id" (A job id.)
     */
    @JsonServerMethod(rpc = "KBaseJobService.run_job")
    public String runJob(RunJobParams params, AuthToken authPart) throws Exception {
        String returnVal = null;
        //BEGIN run_job
        lastJobId++;
        returnVal = "" + lastJobId;
        jobs.put(returnVal, params);
        //END run_job
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: get_job_params</p>
     * <pre>
     * Get job params necessary for job execution
     * </pre>
     * @param   jobId   instance of original type "job_id" (A job id.)
     * @return   parameter "params" of type {@link us.kbase.kbasejobservice.RunJobParams RunJobParams}
     */
    @JsonServerMethod(rpc = "KBaseJobService.get_job_params")
    public RunJobParams getJobParams(String jobId, AuthToken authPart) throws Exception {
        RunJobParams returnVal = null;
        //BEGIN get_job_params
        returnVal = jobs.get(jobId);
        //END get_job_params
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: finish_job</p>
     * <pre>
     * Finish already started job
     * </pre>
     * @param   params   instance of type {@link us.kbase.kbasejobservice.FinishJobParams FinishJobParams}
     */
    @JsonServerMethod(rpc = "KBaseJobService.finish_job")
    public void finishJob(FinishJobParams params, AuthToken authPart) throws Exception {
        //BEGIN finish_job
        //END finish_job
    }

    /**
     * <p>Original spec-file function name: check_job</p>
     * <pre>
     * Check if job is finished and get results/error
     * </pre>
     * @param   jobId   instance of original type "job_id" (A job id.)
     * @return   parameter "job_state" of type {@link us.kbase.kbasejobservice.JobState JobState}
     */
    @JsonServerMethod(rpc = "KBaseJobService.check_job")
    public JobState checkJob(String jobId, AuthToken authPart) throws Exception {
        JobState returnVal = null;
        //BEGIN check_job
        RunJobParams params = jobs.get(jobId);
        returnVal = new JobState();
        returnVal.setFinished(1L);
        returnVal.setResult(new UObject(params.getMethodParams()));
        //END check_job
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: <program> <server_port>");
            return;
        }
        new KBaseJobServiceServer().startupServer(Integer.parseInt(args[0]));
    }
}
