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

import ReloadPage from './ReloadPage';

describe('ReloadPage', () => {

  let props;
  let context;
  beforeEach(() => {
    props = {
      location: {
        state: {
          to: {foo: 1}
        }
      }
    };

    context = {
      router: {
        replace: sinon.spy()
      }
    };
  });

  it('should render without exploding', () => {
    const wrapper = shallow(<ReloadPage {...props}/>, {context});
    expect(wrapper).to.have.length(1);
    expect(context.router.replace).to.have.been.calledWith(props.location.state.to);
  });

  it('should fall back to home if there is an error', () => {
    delete props.location.state;
    const wrapper = shallow(<ReloadPage {...props}/>, {context});
    expect(wrapper).to.have.length(1);
    expect(context.router.replace).to.have.been.calledWith('/');
  });

});
