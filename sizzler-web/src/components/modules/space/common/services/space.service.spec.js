import spaceServiceFn from './space.service';
import cookieUtils from 'utils/cookie.utils';

describe('space serivce', () => {
	var SpaceService = null,
		SpaceResourcesMock = null,
		UserResourcesMock = null,
		publicDataSrvMock = null,
		promise = null;

	// mock 依赖的服务
	beforeEach(angular.mock.inject(($q) => {
		promise = $q;
		SpaceResourcesMock = {
			getSpaceInviteInfo: function (inviteCode) {
				var result = {
					type: inviteCode, // inviteCode就设置为返回类型
					spaceId: 'spaceId',
					spaceName: 'spaceName',
					userEmail: 'userEmail',
					salesManager: 'salesManager'
				};
				return $q.resolve(result);
			}
		};

		UserResourcesMock = {};

		publicDataSrvMock = {};

	}));

	// 创建spaceService
	beforeEach(() => {
		SpaceService = new spaceServiceFn(
			SpaceResourcesMock,
			UserResourcesMock,
			publicDataSrvMock,
			promise
		);
	});

	describe("getSpaceInviteInfo", () => {

		// 需要注册
		it("type should be signup", () => {
			var inviteCode = 'signup';
			SpaceService.getSpaceInviteInfo(inviteCode).then((result) => {
				expect(result.type).to.equal('signup');
			});
		});

		// 需要跳转到dashboard页面
		it("type should be dashboard", () => {
			// 邀请邮箱和当前登录用户邮箱一致
			cookieUtils.set('sid', 'sid');
			cookieUtils.set('ptEmail', 'userEmail');

			var inviteCode = 'signin';
			SpaceService.getSpaceInviteInfo(inviteCode).then((result) => {
				expect(result.type).to.equal('dashboard');
				expect(localStorage.getItem('ptnm')).to.equal('userEmail');
			});
		});

		// 需要进行登录
		it("type should be signin", () => {
			// 邀请邮箱和当前登录用户邮箱一致
			cookieUtils.set('sid', 'sid');
			cookieUtils.set('ptEmail', 'otherUserEmail');

			var inviteCode = 'signin';
			SpaceService.getSpaceInviteInfo(inviteCode).then((result) => {
				expect(result.type).to.equal('signin');
			});
		});

		// 邀请码已失效
		it("type should be invalidate", () => {
			var inviteCode = 'invalidate';
			SpaceService.getSpaceInviteInfo(inviteCode).then((result) => {
				expect(result.type).to.equal('invalidate');
			});
		});

	});
});
