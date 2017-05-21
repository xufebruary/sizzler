
SpaceMemberController.$inject = ['$scope', '$rootScope', '$document', 'SpaceService', 'uiLoadingSrv', 'siteEventAnalyticsSrv'];
function SpaceMemberController($scope, $rootScope, $document, SpaceService, uiLoadingSrv, siteEventAnalyticsSrv) {

	var vm = this,
	    $body = $document.find('body').eq(0);

	// 页面状态
	vm.state = {
		memberList: [], //空间成员列表
		currentMember: null, //当前操作的member
		space: null, //空间
		current: null, //当前操作的member
		dialogType: null //控制弹框类型
	};

	init();

	// 重新发送邮件
	vm.resend = resend;

	// 删除成员
	vm.deleteMember = deleteMember;

	// 弹出框显示
	vm.showDialog = showDialog;

	// 关闭弹出框
	vm.closeDialog = closeDialog;

	// 发送邀请邮件成功后回调
	vm.onSuccess = onSuccess;

	function init() {

		// 当前空间信息
		vm.state.space = getSpaceInfoFromParentScope();

		// 获取空间用户列表
		getInvitedMemberList(vm.state.space.spaceId);

	}

	function getInvitedMemberList(spaceId) {
		SpaceService.getInvitedMemberList(spaceId).then((result) => {
			vm.state.memberList = result;
		});
	}

	function resend(member) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.popup-content'));

		SpaceService.inviteMembers(vm.state.space.spaceId, [member.userEmail])
		.then(() => {
			showDialog('resend');
		})
		.finally(() => {
			uiLoadingSrv.removeLoading(angular.element('.popup-content'));
		});
	}

	function deleteMember(member) {
		//loading
		uiLoadingSrv.createLoading(angular.element('.popup-content'));

		SpaceService.deleteMember(vm.state.space.spaceId, member.userEmail)
		.then(() => {
			vm.state.memberList = vm.state.memberList.filter((o) => {
				return o.userEmail != member.userEmail;
			});
		})
		.finally(() => {
			closeDialog('confirm_delete_member');
			uiLoadingSrv.removeLoading(angular.element('.popup-content'));
		});
	}

	function showDialog(type) {
		$body.addClass('modal-open');
		vm.state.dialogType = type;
	}

	function closeDialog(type) {
		vm.state.dialogType = null;
		$body.removeClass('modal-open');


		if(type){
			//全站事件统计
	        siteEventAnalyticsSrv.createData({
	            uid: $rootScope.userInfo.ptId,
	            where: "space_member",
	            what: type,
	            how: "click",
	            value: vm.state.space.spaceId
	        });
		}
	}

	function onSuccess(emailList) {
		closeDialog('send_invites'); //关闭弹出框
		init(); //重新初始化
	}

	function getSpaceInfoFromParentScope(){
		var currentSpace = $scope.rootSpace.current;
		return {
			spaceId: currentSpace.spaceId,
			name: currentSpace.name,
			host: window.location.host,
			domain: currentSpace.domain,
			weekStart: currentSpace.weekStart,
			ownerEmail: currentSpace.ownerEmail,
			type: currentSpace.type
		};
	}
}

export default SpaceMemberController;
