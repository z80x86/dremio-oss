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
import Switch from '@material-ui/core/Switch';
import FormControlLabel from '@material-ui/core/FormControlLabel';

export default class Toggle extends Component {
  static propTypes = {
    onChange: PropTypes.func,
    value: PropTypes.bool,
    label: PropTypes.node,
    style: PropTypes.object
  }

  static defaultProps = {
    labelPosition: 'right'
  }

  render() {
    const { onChange, value, label, style } = this.props;
    return (
      <FormControlLabel
        control={
          <Switch
            color='primary'
            onChange={onChange}
            checked={value}
            className='field'
          />
        }
        label={label}
        style={{ marginRight: 0, ...style}}
      />
    );
  }
}
