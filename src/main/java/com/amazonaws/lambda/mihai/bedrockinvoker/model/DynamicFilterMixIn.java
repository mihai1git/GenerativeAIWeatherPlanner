package com.amazonaws.lambda.mihai.bedrockinvoker.model;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("customFilter")
/**
 * Used in Jackson serialization.
 * This is a Mix-in that injects @JsonFilter annotation
 * @author mike
 *
 */
public interface DynamicFilterMixIn {

}
