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
import { Select } from 'components/Fields';

const items = [
  {
    label: 'false',
    option: 'false'
  },
  {
    label: 'true',
    option: 'true'
  }
];


const BooleanSelect = (props) => {
  return <Select items={items} {...props} {...styles} />;
};

export default BooleanSelect;

const styles = {
  style: { // todo move to less and apply mixin for select height
    height: 24,
    lineHeight: '24px'
  },
  iconStyle: {
    paddingTop: 0
  }
};
