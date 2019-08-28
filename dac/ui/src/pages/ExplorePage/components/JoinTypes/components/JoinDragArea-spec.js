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
import { shallow } from 'enzyme';

import ExploreDragArea from '../../ExploreDragArea';

import JoinDragArea from './JoinDragArea';

describe('JoinDragArea', () => {
  let props;

  beforeEach(() => {
    props = {
      style: {},
      dragColumntableType: 'type',
      items: Immutable.fromJS([{}, {}]),
      defaultColumns: Immutable.List(),
      customColumns: Immutable.List(),
      rightColumns: Immutable.List(),
      leftColumns: Immutable.List(),
      handleDrop: sinon.spy(),
      removeColumn: sinon.spy(),
      moveColumn: sinon.spy(),
      dragType: 'type',
      columnDragName: 'name',
      isDragInProgress: false,
      addColumn: sinon.spy()
    };
  });
  it('should render with props', () => {
    const wrapper = shallow(<JoinDragArea {...props} />);
    expect(wrapper).to.have.length(1);
  });
  it('should render JoinDragAreaColumn', () => {
    const wrapper = shallow(<JoinDragArea {...props} />);
    expect(wrapper.find(ExploreDragArea)).to.have.length(1);
  });
});
