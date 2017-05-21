import 'assets/css/font.css';
import 'assets/css/font-awesome.min.css';
import 'assets/css/simple-line-icons.css';
import 'assets/css/animate.css';
import 'assets/css/color.css';
import 'assets/css/common.css';
import 'assets/css/layer.css';
import 'assets/css/base.css';
import 'assets/css/l18n-ja_JP.css';
import 'assets/libs/jquery/datatables/jquery.dataTables.css';
import 'components/modules/widget/widget-list/gridstack.scss';

import 'assets/sass/widget.scss';
import 'assets/libs/jquery/custom-content-scroller/jquery.mCustomScrollbar.css';
import 'components/partial/dashboard/dashboard.css';
import 'components/modules/chart/chart.css';
import 'components/share/share.scss';

import 'assets/css/svg.css';


// import 'jquery-ui';
// import 'lodash';

import 'assets/libs/jquery/datatables/jquery.dataTables.min';

import 'components/share/app/app';
import 'components/share/app/config';
import 'configs/jq.config';
import 'components/partial/dashboard/dashboard-time.dire';



import 'components/share/services/dataMutualSrv';



import 'components/share/modules/chart/chart-data.serv';
// import 'components/modules/chart/chart.ctrl';
import 'components/modules/chart/chart.dire';
import 'components/modules/chart/chart-highchart/highchart.l18n';
import 'components/modules/chart/chart-highchart/chart-highchart.dire';
import 'components/modules/chart/chart-highchart/chart-highchart.srv';
import 'components/modules/chart/chart-highmaps/chart-highmaps.dire';
import 'components/modules/chart/chart-number/chart-number.dire';
import 'components/modules/chart/chart-progressbars/chart-progressbars.dire';
import 'components/modules/chart/chart-pttable/chart-pttable.dire';
import 'components/modules/chart/chart-pttable/chart-pttable.util';
import 'components/modules/chart/chart-simplenumber/chart-simplenumber.dire';
import 'components/modules/chart/chart-heatmap/chart-heatmap.directive';
import 'components/modules/chart/chart-heatmap/chart-heatmap.scss';
import 'components/modules/widget/widget-list/gridstack.controller';
import 'components/modules/widget/widget-list/gridstack.directive';
import 'components/modules/widget/widget-list/gridstackitem.directive';
import 'components/modules/widget/widget-list/gridstackSrc';
import 'components/share/share-panel.ctrl';
import 'components/share/modules/share-login/share-login.dire';
import 'components/share/modules/share-add/share-add.dire';

import 'utils/string.utils';

//处理svg
var __svg__ = {path: './assets/svg/source/**/*.svg', name: 'assets/svg/pt-icons.0323.svg' };
require('webpack-svgstore-plugin/src/helpers/svgxhr')(__svg__);

