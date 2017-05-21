var path = require('path');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var ExtractTextPlugin = require("extract-text-webpack-plugin");
var CopyWebpackPlugin = require('copy-webpack-plugin');
var CommonsChunkPlugin = require("webpack/lib/optimize/CommonsChunkPlugin");
var StringReplacePlugin = require("string-replace-webpack-plugin");
var SvgStore = require('webpack-svgstore-plugin');

var envConfigs = require("./env.config");

module.exports = function(ENV){
	return {
		context: path.join(__dirname, 'src'),
		entry: {
			app: './index.js',
			share: './components/share/index.js',
			vendor: ['angular', 'angular-translate', 'angular-ui-router', 'oclazyload', 'angular-sanitize', 'angular-cookies']
		},
		output: {
			path: path.resolve(__dirname, 'dist'),
			publicPath: '/',
			filename: "[name].js",
			chunkFilename: "[name].js"
		},
		resolve: {
			alias: {
				'angular-translate': 'assets/libs/angular/angular-translate/angular-translate',
				'search-list': 'assets/libs/search-list/search-list.angular',
				'pttable': 'assets/libs/pttable/pttable'
			},
			root: [
				// 增加该配置可以方便require的使用
				path.resolve('./src')
			]
		},
		// devtool: 'eval',
		devtool: 'eval-source-map',
		module: {
			loaders: [
				//解决angular使用jquery而不是jqlite
				{
					test: /angular\.js$/,
					loader: "imports?$=jquery"
				},
				{
					test: /jquery-mousewheel/,
					loader: "imports?define=>false&this=>window"
				},
				{
					test: /jquery\.js$/,
					loader: 'expose?jQuery'
				},
				{
					test: /gridstack\.js$/,
					loader: "imports?$=jquery"
				},
				{
					test: /\.html$/,
					loader: 'html',
					query: {
						minimize: false
					}
				},
				{
					test: /\.js$/,
					loader: 'babel?presets[]=es2015',
					exclude: [/node_modules/, /src\/assets\/libs/]
				},
				{
					test: /\.css$/,
					loader: ExtractTextPlugin.extract("style-loader", "css-loader")
				},
				{
					test: /\.scss$/,
					//http://browniefed.com/blog/webpack-and-compass/
					loader: ExtractTextPlugin.extract(
						"style",
						"css!sass?outputStyle=expanded&includePaths[]=" + path.resolve(__dirname, "./node_modules/compass-mixins/lib"))
				},
				/**
				 * 正则替换
				 * 根据命令行production
				 */
				{
					test: /const\.config\.js$/,
					loader: StringReplacePlugin.replace({
						replacements: [
							{
								pattern: new RegExp(/\$\{\s*(.*)\s*\}/g),
								replacement: function (match, p1, offset, string) {
									return envConfigs[ENV][p1];
								}
							}
						]
					})
				},
				{
				    test: /\.json$/,
				    loader: "json-loader"
				}
			]
		},
		plugins: [
			new webpack.optimize.CommonsChunkPlugin({
				chunks: ['app', 'share'],
				name: 'vendor'
			}),

			new webpack.ProvidePlugin({
				$: "jquery",
				jQuery: "jquery"
			}),

			/**
			 * https://github.com/webpack/extract-text-webpack-plugin/blob/webpack-1/README.md
			 * @type {[type]}
			 */
			new ExtractTextPlugin("[name].[contenthash].css", {
				allChunks: true
			}),

			/**
			 * https://github.com/ampedandwired/html-webpack-plugin
			 */
			// index.html
			new HtmlWebpackPlugin({
				chunks: ['app', 'vendor'],
				filename: 'index.html',
				template: 'assets/index.html',
				inject: 'body'
			}),
			// share-panel.html
			new HtmlWebpackPlugin({
				chunks: ['share', 'vendor'],
				filename: 'share-panel.html',
				template: 'assets/share-panel.html',
				inject: 'body'
			}),

			new StringReplacePlugin(),
			new SvgStore({
				prefix: '',
				// svgo options
				svgoOptions: {
					plugins: [
						{ removeTitle: true }
					]
				}
			}),
			// copy
			new CopyWebpackPlugin([
				{from: './assets/images/**/*'}, //图片
				{from: './assets/fonts/**/*'}, //字体
				{from: './assets/video/**/*'}, //视频
				{from: './assets/css/l18n*.css'}, //国际化样式
				{from: './components/modules/widget/widget-editor/chart/**/*'}, //样式
				{from: './assets/libs/**/*'},
				{from: './assets/css/browser-not-support.css', to: './browser-not-support/browser-not-support.css'},
				{from: './configs/product.config.js', to: './browser-not-support/product.config.js'},
				{from: './utils/cookie.utils.js', to: './browser-not-support/cookie.utils.js'},
				{from: './components/static/browser-not-support.js', to: './browser-not-support/main.js'},
				{from: './assets/browser-not-support.html', to: './browser-not-support.html'},
				{from: './robots.txt'}	//搜索引擎爬虫配置
			], {
				ignore: [
					// Doesn't copy any files with a txt extension
					// '*.txt',
					// Doesn't copy any file, even if they start with a dot
					// {glob: '**/*', dot: true}
				],

				// By default, we only copy modified files during
				// a watch or webpack-dev-server build. Setting this
				// to `true` copies all files.
				copyUnmodified: true
			})
		]
	};
};
