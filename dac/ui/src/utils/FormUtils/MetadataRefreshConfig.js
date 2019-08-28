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
import FormElementConfig from 'utils/FormUtils/FormElementConfig';
import MetadataRefresh from 'components/Forms/MetadataRefresh';
import MetadataRefreshWrapper from 'components/Forms/Wrappers/MetadataRefreshWrapper';

export default class MetadataRefreshConfig extends FormElementConfig {

  constructor(props) {
    super(props);
    this._renderer = MetadataRefreshWrapper;
  }

  getRenderer() {
    return this._renderer;
  }

  getFields() {
    return MetadataRefresh.getFields();
  }

  addInitValues(initValues) {
    //merge metadataPolicy defaults
    initValues.metadataPolicy = {
      ...initValues.metadataPolicy, // could be empty if source is not a file system
      ...MetadataRefresh.defaultFormValues().metadataPolicy
    };
    return initValues;
  }

  addValidators(validations) {
    validations.functions.push(MetadataRefresh.validate);
    return validations;
  }

}
