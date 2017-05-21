// 获取环境配置
var ENV = process.env.env || 'production';

// 如果运行的是npm start命令,则连的是local环境
if(process.env.npm_config_argv){
	var argvs = JSON.parse(process.env.npm_config_argv);
	if (argvs.original[0] == 'start') {
		ENV = 'local';
	}
}

console.log("current env is " + ENV);

var configs = isDevMode() ? require('./webpack-dev.config')(ENV) : require('./webpack-product.config')(ENV);

function isDevMode(){
	return ['local', 'dev'].indexOf(ENV) != -1;
}

module.exports = configs;
