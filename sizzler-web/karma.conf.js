var path = require('path');

// Reference: http://karma-runner.github.io/0.12/config/configuration-file.html
module.exports = function karmaConfig (config) {
	config.set({
		frameworks: [
			// frameworks to use
			// available frameworks: https://npmjs.org/browse/keyword/karma-adapter
			'mocha', 'chai', 'es6-shim'
		],

		/**
		 * 配置请参考如下链接
		 * https://github.com/litixsoft/karma-mocha-reporter
         */
		reporters: ['mocha'],

		files: [
			// Grab all files in the app folder that contain .spec.
			// 'src/tests.webpack.js'
			'node_modules/angular/angular.js',
			'node_modules/angular-mocks/angular-mocks.js',
			'src/assets/libs/angular/angular-translate/angular-translate.js',
			'src/assets/libs/jquery/jquery.min.js', //加载jquery插件
			'src/**/*.spec.js'
		],

		preprocessors: {
			// Reference: http://webpack.github.io/docs/testing.html
			// Reference: https://github.com/webpack/karma-webpack
			// Convert files with webpack and load sourcemaps
			'src/**/*.spec.js': ['webpack', 'sourcemap']
		},

		browsers: [
			// Run tests using PhantomJS
			'PhantomJS'
		],

		singleRun: true,

		webpack: {
			resolve: {
				root: [
					// 增加该配置可以方便require的使用
					path.resolve('./src')
				]
			},
			module: {
				loaders: [
					{
						test: /\.js$/,
						loader: 'babel?presets[]=es2015',
						exclude: [/node_modules/, /src\/assets\/libs/]
					},
					{
						test: /\.css$/,
						loaders: ["style-loader", "css-loader"]
					},
					{
						test: /\.html$/,
						loader: 'html',
						query: {
							minimize: false
						}
					}
				]
			}
		},

		// Hide webpack build information from output
		webpackMiddleware: {
			noInfo: 'errors-only'
		},

		plugins : [
			require('karma-mocha'),
			require('karma-chai'),
			require('karma-phantomjs-launcher'),
			require('karma-sourcemap-loader'),
			require('karma-webpack'),
			require('karma-mocha-reporter'),
			require('karma-es6-shim')
		]
	});
};
