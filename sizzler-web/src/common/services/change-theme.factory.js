'use strict';


/**
 * changeThemeSrv
 * 根据语言版本切换css文件
 */

//angular
//    .module('pt')
//    .factory('changeThemeSrv', [changeThemeSrvFunc]);

function changeThemeSrvFunc() {
    return {
        changeTheme: function (themeFile) {
            //根据ID定位加载css的元素，将其href修改为特定css文件
            document.getElementById('global-css').setAttribute('href', '/css/l18n-' + themeFile + '.css');
        }
    };
}

export default changeThemeSrvFunc;
