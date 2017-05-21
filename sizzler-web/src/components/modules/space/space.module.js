import spaceSettingCtrl from './space-settings/space-settings.controller';
import './space-settings/space-settings.scss';

import spaceInviteCtrl from './space-invites/space-invites.controller';
import './space-invites/space-invites.scss';

import spaceMemberCtrl from './space-member/space-member.controller';
import './space-member/space-member.scss';

import spaceCreateCtrl from './space-create/space-create.controller';
import './space-create/space-create.scss';

import spaceService from './common/services/space.service';

import sendInviteEmailDialogDirective from './common/directives/send-invite-email-dialog/send-invite-email-dialog.directive';
import './common/directives/send-invite-email-dialog/send-invite-email-dialog.scss';

// 第三方库
import 'assets/libs/angular/angular-tags-input/angular-tags-input.min';
import 'assets/libs/angular/angular-tags-input/angular-tags-input.min.css';

export default angular.module('pt.space', ['ngTagsInput'])
.controller({
	'SpaceSettingsCtrl': spaceSettingCtrl,
	'SpaceInviteCtrl': spaceInviteCtrl,
	'SpaceMemberCtrl': spaceMemberCtrl,
	'SpaceCreateCtrl': spaceCreateCtrl
})
.directive({
	'sendInviteEmailDialog': sendInviteEmailDialogDirective
})
.service({
	'SpaceService': spaceService
})
