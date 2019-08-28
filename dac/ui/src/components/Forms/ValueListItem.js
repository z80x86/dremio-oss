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
import { Component } from 'react';
import PropTypes from 'prop-types';

import FieldWithError from 'components/Fields/FieldWithError';
import TextField from 'components/Fields/TextField';
import {RemoveButton, RemoveButtonStyles } from 'components/Fields/FieldList';
import { flexContainer, flexAuto } from 'uiTheme/less/layout.less';


export default class ValueListItem extends Component {
  static propTypes = {
    field: PropTypes.object,
    onRemove: PropTypes.func
  };

  render() {
    const {onRemove, field} = this.props;
    const textInputStyle = { width: '100%' };

    return (
      <div className={flexContainer}>
        <FieldWithError {...field} className={flexAuto} errorPlacement='top'>
          <TextField {...field} style={textInputStyle}/>
        </FieldWithError>
        {onRemove && <RemoveButton onClick={onRemove} style={RemoveButtonStyles.inline}/>}
      </div>
    );
  }
}
