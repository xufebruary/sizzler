
import './assets/css/font.css';
import './assets/css/font-awesome.min.css';
import './assets/css/simple-line-icons.css';
import './assets/css/animate.css';
import './assets/css/color.css';
import './assets/css/common.css';
import './assets/css/layer.css';
import './assets/css/alertify.css';
import './assets/css/alertify-bootstrap-3.css';

import './assets/libs/jquery/datatables/jquery.dataTables.css';
import './assets/libs/jquery/bxslider/jquery.bxslider.css';

import 'components/modules/panel/panel-list/panel-list.css';

import './components/modules/chart/chart.css';

import './assets/css/login-ptengine.css';
import './assets/sass/widget.scss';
import './assets/css/partial/header/header.css';
import './assets/css/partial/aside/aside.css';
import './assets/css/base.css';
import './components/modules/widget/widget-editor/admin/admin.css';

import './assets/css/svg.css';
import './assets/css/l18n-ja_JP.css';

//js
import './app.modules';
import './configs/app.config';
import './configs/jq.config';
import './app.routes';

import 'components/partial/body/body.ctrl';
import 'components/modules/404/404.controller';
import 'components/modules/interceptor/user-interceptor';




import 'utils/string.utils';



//处理svg
var __svg__ = {path: './assets/svg/source/**/*.svg', name: 'assets/svg/pt-icons.0323.svg'};
require('webpack-svgstore-plugin/src/helpers/svgxhr')(__svg__);







