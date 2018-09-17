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
import io.swagger.model.RequestDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SuppressWarnings("ALL")
@Api(value = "requestLicense", description = "the requestLicense API")
public interface RequestLicenseApi {

    @ApiOperation(value = "Requests a license", nickname = "requestLicense", notes = "Request a license, providing an optional id", response = RequestDetail.class, tags={ "developers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Request Detail", response = RequestDetail.class) })
    @RequestMapping(value = "/requestLicense",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<RequestDetail> requestLicense();


    @ApiOperation(value = "Requests a license", nickname = "requestLicenseAgain", notes = "Request a license, providing an optional id", response = RequestDetail.class, tags={ "developers", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Request Detail", response = RequestDetail.class) })
    @RequestMapping(value = "/requestLicense/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<RequestDetail> requestLicenseAgain(@ApiParam(value = "",required=true) @PathVariable("id") String id);

}