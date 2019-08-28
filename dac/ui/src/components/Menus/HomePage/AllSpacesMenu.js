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
import { PureComponent } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { showConfirmationDialog } from 'actions/confirmation';

import { removeSpace } from 'actions/resources/spaces';
import AllSpacesMenuMixin from 'dyn-load/components/Menus/HomePage/AllSpacesMenuMixin';
import { getSpaceVersion, getSpaceName } from '@app/selectors/home';

const mapStateToProps = (state, { spaceId }) => {
  return {
    spaceName: getSpaceName(state, spaceId),
    spaceVersion: getSpaceVersion(state, spaceId)
  };
};

const mapDispatchToProps = {
  removeItem: removeSpace,
  showDialog: showConfirmationDialog
};

@AllSpacesMenuMixin
export class AllSpacesMenu extends PureComponent {

  static propTypes = {
    spaceId: PropTypes.string.isRequired,
    closeMenu: PropTypes.func,

    // connected
    spaceName: PropTypes.string.isRequired,
    spaceVersion: PropTypes.string.isRequired,
    removeItem: PropTypes.func,
    showDialog: PropTypes.func.isRequired
  }
  static contextTypes = {
    location: PropTypes.object.isRequired
  }

  handleRemoveSpace = () => {
    const {
      spaceId,
      spaceName,
      spaceVersion,
      closeMenu,
      showDialog,
      removeItem
    } = this.props;

    // copied from menuUtils.showConfirmRemove. Should be moved back to utils, when source would be
    // migrated to v3 api
    showDialog({
      title: la('Remove Space'),
      text: la(`Are you sure you want to remove "${spaceName}"?`),
      confirmText: la('Remove'),
      confirm: () => removeItem(spaceId, spaceVersion)
    });
    closeMenu();
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(AllSpacesMenu);
