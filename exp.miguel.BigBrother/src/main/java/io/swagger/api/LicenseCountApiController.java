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

@Controller
public class LicenseCountApiController implements LicenseCountApi {

    private static final Logger log = LoggerFactory.getLogger(LicenseCountApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
    private IdLog idLog = IdLog.instance;

    @org.springframework.beans.factory.annotation.Autowired
    public LicenseCountApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<Void> licenseCount(@ApiParam(value = "",required=true) @PathVariable("count") Integer count) {
//        String accept = request.getHeader("Accept");
        idLog.setLimit(count);
        log.debug("License Limit set to {}", count);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
