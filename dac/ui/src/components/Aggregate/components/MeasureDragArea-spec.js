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
import Immutable from 'immutable';

import ColumnDragItem from 'utils/ColumnDragItem';
import MeasureDragArea from './MeasureDragArea';

describe('MeasureDragArea', () => {

  let minimalProps;
  let commonProps;
  beforeEach(() => {
    const columnsField = [];
    columnsField.addField = sinon.spy();
    columnsField.removeField = sinon.spy();

    minimalProps = {
      isDragInProgress: true,
      dragItem: new ColumnDragItem(),
      dragType: 'groupBy',
      columnsField
    };

    commonProps = {
      ...minimalProps,
      onDrop: sinon.spy(),
      moveColumn: sinon.spy(),
      allColumns: Immutable.List()
    };
    commonProps.columnsField.push({ value: 'field1' }, { value: 'field2' });
  });

  it('should render with minimal props without exploding', () => {
    const wrapper = shallow(<MeasureDragArea {...minimalProps}/>);
    expect(wrapper).to.have.length(1);
  });
  it('should render ExploreDragArea and DragMeasureColumn based on columnsField array', () => {
    const wrapper = shallow(<MeasureDragArea {...commonProps}/>);

    expect(wrapper.find('ExploreDragArea')).to.have.length(1);
    expect(wrapper.find('DragMeasureColumn')).to.have.length(commonProps.columnsField.length);
    expect(wrapper).to.have.length(1);
  });

  describe('#handleRemoveColumn', () => {
    it('should call columnsField.removeField', () => {
      const instance = shallow(<MeasureDragArea {...commonProps}/>).instance();
      instance.handleRemoveColumn(0);
      expect(commonProps.columnsField.removeField).to.be.calledWith(0);
    });
  });

  describe('#handleDrop', () => {
    it('should call onDrop', () => {
      const instance = shallow(<MeasureDragArea {...commonProps}/>).instance();
      instance.handleDrop('foo');
      expect(commonProps.onDrop).to.be.calledWith('measures', 'foo');
    });
  });
});
