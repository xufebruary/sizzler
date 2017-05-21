import consts from 'configs/const.config';
import cookieUtils from 'utils/cookie.utils';

function extend(obj, props) {
	for (var prop in props) {
		if (props.hasOwnProperty(prop)) {
			obj[prop] = props[prop];
		}
	}
	return obj;
}

/**
 * 遍历resources映射文件,构造出一个service构造函数
 * @param configs resources映射文件 Array
 * @returns {Clazz}
 */
export default function(configs) {

    Clazz.$inject = ['$http', '$q'];

    function Clazz(http, promise) {
        this.http = http;
        this.promise = promise;
    };

    configs.forEach(function(config) {
        /**
         * 动态构造方法
         * @param data 调用该方法传入的值
         * @param supplants Object 变量内插对象
         * @param paramsConfig Object http请求参数配置(例如：超时)
         * @returns {*}
         */
        Clazz.prototype[config.name] = function(data, supplants, paramsConfig) {
            var params = {
                    headers: {}
                },
                data = data || {},
                paramsConfig = paramsConfig || {};

            params.url = consts.WEB_MIDDLE_URL + (supplants ? config.url.supplant(supplants) : config.url);
            //url上拼接sid
            var sid = cookieUtils.get("sid");
            if (sid) {
                params.url += (params.url.indexOf('?') == -1 ? '?' : '&') + 'sid=' + sid;
            }
            params.method = config.method || 'post';
            params.method = params.method.toUpperCase();

            if (['get'].indexOf(params.method.toLowerCase()) == -1) {
                params.headers['Content-Type'] = 'application/json;charset=UTF-8';
            }
            if(sid) params.headers['Token'] = sid;
            params.cache = config.cache || false;

			var isGet = ['post', 'put', 'delete'].indexOf(params.method.toLowerCase()) == -1;
            if (data && !isGet){
            	params.data = JSON.stringify(data);
			}else{
				params.params = data;
			}

			console.log("before params is ", params);

            //合并参数
            // params = Object.assign(params, paramsConfig);
			params = extend(params, paramsConfig);

			Object.defineProperty(params, "namea", {"value": "tst"});

			console.log("after params is ", params);

            //标准化返回结果,使用reject接收error,resolve接收success
            return this.http(params).then((res) => {
            	console.log("res", res)
                if (res) {
                    if(res.data){
                        if (res.data.status) {
                            if (res.data.status == 'success')
                                return this.promise.resolve(res.data.content);
                            else
                                return this.promise.reject(res.data);
                        } else {
                            return this.promise.resolve(res.data.data);
                        }
                    }
                    else {
                        return this.promise.resolve('success');
                    }
                }

                return this.promise.reject({ message: 'SYSTEM.SYSTEM_ERROR' });
            }, (res) => {
                if (res.data)
                    return this.promise.reject(res.data);
                else
                    return this.promise.reject({'errorCode':'timeout', 'message': 'timeout'});

            });
        };
    });

    return Clazz;
}
