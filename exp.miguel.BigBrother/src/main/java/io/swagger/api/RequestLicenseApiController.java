package io.swagger.api;

import exp.miguel.license.broker.IdLog;
import io.swagger.annotations.ApiParam;
import io.swagger.model.RequestDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

//@SuppressWarnings({"HardCodedStringLiteral", "Convert2Diamond", "DefaultAnnotationParam"})
@SuppressWarnings("ALL")
@Controller
public class RequestLicenseApiController implements RequestLicenseApi {
	private static final Logger log = LoggerFactory.getLogger(RequestLicenseApiController.class);
//	private static final Logger log = LoggerFactory.getLogger(RequestLicenseApiController.class);

//	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;
	
	private final IdLog idLog = IdLog.instance;

	@org.springframework.beans.factory.annotation.Autowired
	public RequestLicenseApiController(HttpServletRequest request) {
//		this.objectMapper = objectMapper;
		this.request = request;
	}

	@Override
	public ResponseEntity<RequestDetail> requestLicense() {
		String accept = request.getHeader("Accept");
		if ((accept != null) && accept.contains("application/json")) {
			final RequestDetail license = idLog.getLicense();
			log.debug("new request license returned {} ({})", license.getId(), license.getAuthority());
			return new ResponseEntity<>(license, HttpStatus.OK);
		}

		log.debug("Bad Header: {}", accept);
		return new ResponseEntity<RequestDetail>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public ResponseEntity<RequestDetail> requestLicenseAgain(@ApiParam(value = "", required = true) @PathVariable("id") String id) {
		String accept = request.getHeader("Accept");
		if ((accept != null) && accept.contains("application/json")) {
			return new ResponseEntity<RequestDetail>(idLog.getLicense(id), HttpStatus.OK);
		}

		log.debug("Bad Header: {}", accept);
		return new ResponseEntity<RequestDetail>(HttpStatus.NOT_IMPLEMENTED);
	}

}
