/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.dac.server;


import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint for TransformGroupBy
 * TODO: make it generic
 */
@Constraint(validatedBy = {TransformGroupByValidator.class})
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface TransformGroupByConstraint {
  String message() default "columnsDimensions, columnsMeasures both may not be empty(at least one of them should be set)";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
