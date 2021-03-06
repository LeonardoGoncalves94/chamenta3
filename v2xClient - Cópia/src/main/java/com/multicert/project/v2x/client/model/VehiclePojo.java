/*
 * RA Service
 * The RAservice is responsible for handling the request for enrollment and authorization certificates 
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.multicert.project.v2x.client.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * VehiclePojo
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-09-26T14:56:22.071+01:00")
public class VehiclePojo {
  @JsonProperty("canonicalPublicKey")
  private String canonicalPublicKey = null;

  @JsonProperty("publicKey")
  private String publicKey = null;

  @JsonProperty("vehicleId")
  private String vehicleId = null;

  @JsonProperty("vehicleType")
  private Integer vehicleType = null;

  public VehiclePojo canonicalPublicKey(String canonicalPublicKey) {
    this.canonicalPublicKey = canonicalPublicKey;
    return this;
  }

   /**
   * Get canonicalPublicKey
   * @return canonicalPublicKey
  **/
  @ApiModelProperty(value = "")
  public String getCanonicalPublicKey() {
    return canonicalPublicKey;
  }

  public void setCanonicalPublicKey(String canonicalPublicKey) {
    this.canonicalPublicKey = canonicalPublicKey;
  }

  public VehiclePojo publicKey(String publicKey) {
    this.publicKey = publicKey;
    return this;
  }

   /**
   * Get publicKey
   * @return publicKey
  **/
  @ApiModelProperty(value = "")
  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public VehiclePojo vehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
    return this;
  }

   /**
   * Get vehicleId
   * @return vehicleId
  **/
  @ApiModelProperty(value = "")
  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  public VehiclePojo vehicleType(Integer vehicleType) {
    this.vehicleType = vehicleType;
    return this;
  }

   /**
   * Get vehicleType
   * @return vehicleType
  **/
  @ApiModelProperty(value = "")
  public Integer getVehicleType() {
    return vehicleType;
  }

  public void setVehicleType(Integer vehicleType) {
    this.vehicleType = vehicleType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VehiclePojo vehiclePojo = (VehiclePojo) o;
    return Objects.equals(this.canonicalPublicKey, vehiclePojo.canonicalPublicKey) &&
        Objects.equals(this.publicKey, vehiclePojo.publicKey) &&
        Objects.equals(this.vehicleId, vehiclePojo.vehicleId) &&
        Objects.equals(this.vehicleType, vehiclePojo.vehicleType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(canonicalPublicKey, publicKey, vehicleId, vehicleType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehiclePojo {\n");
    
    sb.append("    canonicalPublicKey: ").append(toIndentedString(canonicalPublicKey)).append("\n");
    sb.append("    publicKey: ").append(toIndentedString(publicKey)).append("\n");
    sb.append("    vehicleId: ").append(toIndentedString(vehicleId)).append("\n");
    sb.append("    vehicleType: ").append(toIndentedString(vehicleType)).append("\n");
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

