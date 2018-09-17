# DevelopersApi

All URIs are relative to *https://virtserver.swaggerhub.com/SwingGuy1024/license/1.0.0*

Method | HTTP request | Description
------------- | ------------- | -------------
[**complete**](DevelopersApi.md#complete) | **POST** /done/{id} | Notify complete.
[**licenseCount**](DevelopersApi.md#licenseCount) | **POST** /licenseCount/{count} | Change license count
[**requestLicense**](DevelopersApi.md#requestLicense) | **GET** /requestLicense/{id} | Requests a license
[**stillAlive**](DevelopersApi.md#stillAlive) | **POST** /working/{id} | Notify still alive


<a name="complete"></a>
# **complete**
> complete(id)

Notify complete.

Notify server you are done with the license

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DevelopersApi;


DevelopersApi apiInstance = new DevelopersApi();
String id = "id_example"; // String | 
try {
    apiInstance.complete(id);
} catch (ApiException e) {
    System.err.println("Exception when calling DevelopersApi#complete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="licenseCount"></a>
# **licenseCount**
> licenseCount(id)

Change license count

Change the number of licenses allowed

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DevelopersApi;


DevelopersApi apiInstance = new DevelopersApi();
Integer id = 56; // Integer | 
try {
    apiInstance.licenseCount(id);
} catch (ApiException e) {
    System.err.println("Exception when calling DevelopersApi#licenseCount");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Integer**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="requestLicense"></a>
# **requestLicense**
> RequestDetail requestLicense(id)

Requests a license

Request a license, providing an optional id

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DevelopersApi;


DevelopersApi apiInstance = new DevelopersApi();
String id = "id_example"; // String | 
try {
    RequestDetail result = apiInstance.requestLicense(id);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DevelopersApi#requestLicense");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**|  |

### Return type

[**RequestDetail**](RequestDetail.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="stillAlive"></a>
# **stillAlive**
> stillAlive(id)

Notify still alive

Notify server you are still alive

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DevelopersApi;


DevelopersApi apiInstance = new DevelopersApi();
String id = "id_example"; // String | 
try {
    apiInstance.stillAlive(id);
} catch (ApiException e) {
    System.err.println("Exception when calling DevelopersApi#stillAlive");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **String**|  |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

