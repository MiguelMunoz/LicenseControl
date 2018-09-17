/*
 * NOTE: This class is auto generated by the swagger code generator program (1.0.16).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SuppressWarnings("ALL")
@Api(value = "done", description = "the done API")
public interface DoneApi {

    @ApiOperation(value = "Notify complete.", nickname = "complete", notes = "Notify server you are done with the license", tags={ "developers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Okay") })
    @RequestMapping(value = "/done/{id}",
        method = RequestMethod.POST)
    ResponseEntity<Void> complete(@ApiParam(value = "",required=true) @PathVariable("id") String id);

}