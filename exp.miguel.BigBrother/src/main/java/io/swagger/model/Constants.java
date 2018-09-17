package io.swagger.model;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Constants
 */
@SuppressWarnings("ALL")
@Validated
public class Constants   {
  @JsonProperty("keepAliveTimeSeconds")
  private Integer keepAliveTimeSeconds = null;

  public Constants keepAliveTimeSeconds(Integer keepAliveTimeSeconds) {
    this.keepAliveTimeSeconds = keepAliveTimeSeconds;
    return this;
  }

  /**
   * Get keepAliveTimeSeconds
   * @return keepAliveTimeSeconds
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public Integer getKeepAliveTimeSeconds() {
    return keepAliveTimeSeconds;
  }

  public void setKeepAliveTimeSeconds(Integer keepAliveTimeSeconds) {
    this.keepAliveTimeSeconds = keepAliveTimeSeconds;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Constants constants = (Constants) o;
    return Objects.equals(this.keepAliveTimeSeconds, constants.keepAliveTimeSeconds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keepAliveTimeSeconds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Constants {\n");
    
    sb.append("    keepAliveTimeSeconds: ").append(toIndentedString(keepAliveTimeSeconds)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

