package io.swagger.api;

import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import exp.miguel.license.broker.IdLog;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@SuppressWarnings("ALL")
@Controller
public class WorkingApiController implements WorkingApi {

    private static final Logger log = LoggerFactory.getLogger(WorkingApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private IdLog idLog = IdLog.instance;

    @org.springframework.beans.factory.annotation.Autowired
    public WorkingApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<Void> stillAlive(@ApiParam(value = "",required=true) @PathVariable("id") String id) {
	    idLog.keepAlive(id);
	    log.debug("Still alive for request {}", id);
    	return new ResponseEntity<>(HttpStatus.OK);
//        String accept = request.getHeader("Accept");
//        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

}
