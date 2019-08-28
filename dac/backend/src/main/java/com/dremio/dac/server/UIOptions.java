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

import com.dremio.options.Options;
import com.dremio.options.TypeValidators.BooleanValidator;
import com.dremio.options.TypeValidators.StringValidator;

/**
 * Options to customize the UI behavior
 */
@Options
public final class UIOptions {
  public static final BooleanValidator ALLOW_LOWER_PROVISIONING_SETTINGS = new BooleanValidator("ui.allow.lower.provisioning_settings", false);

  public static final StringValidator TABLEAU_TDS_MIMETYPE  = new StringValidator("ui.tableau.tds-mime-type", "application/tds");

  public static final BooleanValidator ALLOW_FILE_UPLOADS  = new BooleanValidator("ui.upload.allow", true);

  public static final StringValidator WHITE_LABEL_URL  = new StringValidator("ui.whitelabel.url", "dremio");

  /*
  * Specifies weather non admin users are able to perform CRUD operations for spaces
  */
  public static final BooleanValidator ALLOW_SPACE_MANAGEMENT = new BooleanValidator("ui.space.allow-manage", false);
}
