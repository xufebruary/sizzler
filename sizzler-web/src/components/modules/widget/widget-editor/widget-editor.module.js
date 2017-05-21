
import chartDirective from './chart/chart.dire';
import calculateValueDirective from './calculatedValue/calculatedValue.dire';
import customDirective from './custom/custom.dire';
import editorDirective from './editor/widget-editor.dire';
import filterDirective from './filter/filter.dire';
import sourceDirective from './source/source.dire';
import timeDirective from './time/time.dire';
import titleDirective from './title/title.dire';
import editorBorderDirective from './editor/editor-border.dire';
import editorHeatmapDirective from './heatmap/heatmap.directive';
import dataAliasDirective from './dataAlias/dataAlias.directive';

// import datasourceModule from './datasource/datasource.module';
import dataModule from './data/data.module';

export default angular.module('pt.widget.editor', [dataModule.name])
	.directive({
		calculatedValue: calculateValueDirective,
		editorChart: chartDirective,
		editorCustom: customDirective,
		widgeteditor: editorDirective,
		widgetFilter: filterDirective,
		editorSource: sourceDirective,

		editorTime: timeDirective,
		editorTitle: titleDirective,
		editorBorder: editorBorderDirective,
		editorHeatmap: editorHeatmapDirective,
		editorDataAlias: dataAliasDirective
	});
