package io.swagger.api;

import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import exp.miguel.license.broker.LicenseLimit;
import io.swagger.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@SuppressWarnings("ALL")
@Controller
public class ConstantsApiController implements ConstantsApi {

	private static final Logger log = LoggerFactory.getLogger(ConstantsApiController.class);

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@org.springframework.beans.factory.annotation.Autowired
	public ConstantsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	public ResponseEntity<Constants> constants() {
		String accept = request.getHeader("Accept");
		log.debug("Constants: accept={}", accept);
		if (accept != null && accept.contains("application/json")) {
			int keepAliveMillis = (int) LicenseLimit.getKeepAliveMilliseconds();
			Constants constants = new Constants();
			constants.setKeepAliveTimeSeconds(keepAliveMillis);
			log.debug("Keep-Alive from server: {}", keepAliveMillis);
//			log.debug("Constants from server: {}", constants);
			return new ResponseEntity<>(constants, HttpStatus.OK);
		}
		log.debug("Bad Header: {}", accept);

		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

}
