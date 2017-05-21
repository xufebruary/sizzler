import categorySettingModule from './category-setting/category-setting.module';
import sortModule from './sort/sort.module';

export default angular.module('pt.widget-editor.data.dimension', [categorySettingModule.name, sortModule.name]);
