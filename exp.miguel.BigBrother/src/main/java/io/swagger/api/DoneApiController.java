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
public class DoneApiController implements DoneApi {

    private static final Logger log = LoggerFactory.getLogger(DoneApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public DoneApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> complete(@ApiParam(value = "",required=true) @PathVariable("id") String id) {
//        String accept = request.getHeader("Accept");
        IdLog.instance.complete(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
